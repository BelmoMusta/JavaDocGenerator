package musta.belmo.mappinggenerator.service;

import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.Modifier;
import com.github.javaparser.ast.NodeList;
import com.github.javaparser.ast.PackageDeclaration;
import com.github.javaparser.ast.body.*;
import com.github.javaparser.ast.expr.*;
import com.github.javaparser.ast.stmt.BlockStmt;
import com.github.javaparser.ast.stmt.IfStmt;
import com.github.javaparser.ast.stmt.ReturnStmt;
import com.github.javaparser.ast.type.ClassOrInterfaceType;
import musta.belmo.javacodecore.CodeUtils;
import musta.belmo.javacodecore.Utils;
import org.apache.commons.lang3.StringUtils;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class MappingGenerator {
    private CompilationUnit source;
    private String destinationClassName;
    private String destinationPackage;
    private Map<String, String> fieldsMapper;
    private String mappingMethodPrefix;
    private String mapperClassPrefix;
    private boolean staticMethod;
    private boolean accessCollectionByGetter;
    private CompilationUnit result;


    public MappingGenerator() {
        fieldsMapper = new LinkedHashMap<>();
    }

    public CompilationUnit getSource() {
        return source;
    }

    public void setSource(CompilationUnit source) {
        this.source = source;
    }

    public String getDestinationClassName() {
        return destinationClassName;
    }

    public void setDestinationClassName(String destinationClassName) {
        this.destinationClassName = destinationClassName;
    }

    public String getDestinationPackage() {
        return destinationPackage;
    }

    public void setDestinationPackage(String destinationPackage) {
        this.destinationPackage = destinationPackage;
    }

    public Map<String, String> getFieldsMapper() {
        return fieldsMapper;
    }

    public void setFieldsMapper(Map<String, String> fieldsMapper) {
        this.fieldsMapper = fieldsMapper;
    }

    public String getMappingMethodPrefix() {
        return mappingMethodPrefix;
    }

    public void setMappingMethodPrefix(String mappingMethodPrefix) {
        this.mappingMethodPrefix = mappingMethodPrefix;
    }

    public void createMapperV2() {
        NullLiteralExpr nullLiteralExpr = new NullLiteralExpr();
        String packageDeclaration = "";
        Optional<PackageDeclaration> sourcePackageDeclaration = source.getPackageDeclaration();
        if (sourcePackageDeclaration.isPresent()) {
            packageDeclaration = sourcePackageDeclaration.get().getName().asString();
        }
        result = new CompilationUnit()
                .setPackageDeclaration(destinationPackage);
        List<ClassOrInterfaceDeclaration> allClasses = source.findAll(ClassOrInterfaceDeclaration.class);

        for (ClassOrInterfaceDeclaration classDef : allClasses) {
            String srcClassName = classDef.getName().asString();
            ClassOrInterfaceDeclaration myClass = result.addClass(srcClassName + mapperClassPrefix)
                    .setModifiers(classDef.getModifiers());
            myClass.addConstructor()
                    .setPrivate(staticMethod); // if only static methods, then make the constructor private
            MethodDeclaration mapperMethod = myClass.addMethod(mappingMethodPrefix + srcClassName);

            ClassOrInterfaceType destClassType = new ClassOrInterfaceType()
                    .setName(destinationClassName);
            if (StringUtils.isNotBlank(packageDeclaration)) {
                packageDeclaration = String.format("%s.", packageDeclaration);
            }
            ClassOrInterfaceType srcClassType = new ClassOrInterfaceType()
                    .setName(packageDeclaration + srcClassName);
            mapperMethod.setType(destClassType)
                    .addModifier(Modifier.PUBLIC)
                    .setStatic(staticMethod);
            Parameter param = new Parameter(srcClassType, String.format("p%s", srcClassName))
                    .addModifier(Modifier.FINAL);
            mapperMethod.addParameter(param);
            Optional<BlockStmt> body = mapperMethod.getBody();
            if (body.isPresent()) {
                BlockStmt methodBody = body.get();
                VariableDeclarator variableDeclarator = CodeUtils.variableDeclaratorFromType(destClassType,
                        String.format("l%s", Utils.getSimpleClassName(destinationClassName)));

                VariableDeclarationExpr variableDeclarationExpr =
                        CodeUtils.variableDeclarationExprFromVariable(variableDeclarator)
                                .addModifier(Modifier.FINAL);

                NameExpr varName = variableDeclarator.getNameAsExpression();
                AssignExpr objectDeclarationStmt = CodeUtils.createAssignExpression(varName,
                        nullLiteralExpr);
                methodBody.addStatement(variableDeclarationExpr);

                Expression condition = new BinaryExpr(param.getNameAsExpression(),
                        nullLiteralExpr, BinaryExpr.Operator.EQUALS);

                ObjectCreationExpr objectCreationExpr = CodeUtils.objectCreationExpFromType(destClassType);

                AssignExpr objectCreationStmt = CodeUtils.createAssignExpression(varName,
                        objectCreationExpr);

                BlockStmt elseStatement = new BlockStmt()
                        .addStatement(objectCreationStmt);

                BlockStmt thenStatement = new BlockStmt()
                        .addStatement(objectDeclarationStmt);

                IfStmt ifStmt = CodeUtils.createIfStamtement(condition, thenStatement, elseStatement);
                methodBody.addStatement(ifStmt);

                source.findAll(MethodDeclaration.class).forEach(methodDeclaration -> {
                    boolean isCollectionType = false;
                    MethodCallExpr methodCallExpr;
                    if (CodeUtils.isSetter(methodDeclaration)) {
                        NodeList<Parameter> methodParameters = methodDeclaration.getParameters();
                        if (methodParameters.size() == 1) {
                            isCollectionType = CodeUtils.isCollectionType(methodParameters.get(0));
                        }
                        if (accessCollectionByGetter && isCollectionType) {
                            return;
                        } else {
                            methodCallExpr = createCallStmt(methodDeclaration, param, variableDeclarator, true);
                        }
                    } else if (CodeUtils.isGetter(methodDeclaration)
                            && accessCollectionByGetter
                            && CodeUtils.isCollectionType(methodDeclaration)) {
                        methodCallExpr = createCallStmt(methodDeclaration, param, variableDeclarator, false);
                    } else {
                        return;
                    }
                    elseStatement.addStatement(methodCallExpr);
                });
                methodBody.addStatement(new ReturnStmt(varName));
            }
        }
    }

    /**
     * @param methodDeclaration
     * @param param
     * @param variableDeclarator
     * @param isSetter
     * @return MethodCallExpr
     */
    private MethodCallExpr createCallStmt(MethodDeclaration methodDeclaration,
                                          Parameter param,
                                          VariableDeclarator variableDeclarator,
                                          boolean isSetter) {
        String srcSetterName = methodDeclaration.getName().asString();
        String srcFieldName = srcSetterName.substring(3);

        String destSetterName = srcSetterName;
        String mappedName = fieldsMapper.get(Utils.toLowerCaseFirstLetter(srcFieldName));
        if (mappedName != null) {
            destSetterName = "set" + StringUtils.capitalize(mappedName);
        }
        MethodCallExpr call = new MethodCallExpr(
                variableDeclarator.getNameAsExpression(), destSetterName);
        MethodCallExpr addAllMethod;
        MethodCallExpr retValue;
        String methodGetter = String.format("get%s", srcFieldName);
        MethodCallExpr getExpression = new MethodCallExpr(param.getNameAsExpression(), methodGetter);

        if (isSetter) {
            call.addArgument(getExpression);
            retValue = call;
        } else {
            addAllMethod = new MethodCallExpr(call, "addAll");
            addAllMethod.addArgument(getExpression);
            retValue = addAllMethod;
        }
        return retValue;
    }

    public void setMapperClassPrefix(String mapperClassPrefix) {
        this.mapperClassPrefix = mapperClassPrefix;
    }

    public void setStaticMethod(boolean staticMethod) {
        this.staticMethod = staticMethod;
    }

    public boolean isAccessCollectionByGetter() {
        return accessCollectionByGetter;
    }

    public void setAccessCollectionByGetter(boolean accessCollectionByGetter) {
        this.accessCollectionByGetter = accessCollectionByGetter;
    }

    public CompilationUnit getResult() {
        return result;
    }


    public void mapField(String oldField, String newField) {
        fieldsMapper.put(oldField, newField);
    }
}