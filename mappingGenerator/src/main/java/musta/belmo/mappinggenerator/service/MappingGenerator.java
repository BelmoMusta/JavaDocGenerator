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
import musta.belmo.javacodecore.Utils;
import musta.belmo.javacodegenerator.service.JavaDocGenerator;
import org.apache.commons.lang3.StringUtils;

import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class MappingGenerator {
    private CompilationUnit source;
    private String destinationClassName;
    private String destinationPackage;
    private Map<String, String> fieldsMapper = new LinkedHashMap<>();
    private String mappingMethodPrefix;
    private String mapperClassPrefix;
    private boolean staticMethod;
    private boolean accessCollectionByGetter;
    private CompilationUnit result;

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
        result = new CompilationUnit();
        result.setPackageDeclaration(destinationPackage);
        List<ClassOrInterfaceDeclaration> allClasses = source.findAll(ClassOrInterfaceDeclaration.class);

        for (ClassOrInterfaceDeclaration classDef : allClasses) {
            String srcClassName = classDef.getName().asString();
            ClassOrInterfaceDeclaration myClass = result.addClass(srcClassName + mapperClassPrefix);
            myClass.setModifiers(classDef.getModifiers());
            ConstructorDeclaration constructorDeclaration = myClass.addConstructor();
            constructorDeclaration.setPrivate(staticMethod); // if only static methods, then make the constructor private
            MethodDeclaration mapperMethod = myClass.addMethod(mappingMethodPrefix + srcClassName);
            ClassOrInterfaceType destClassType = new ClassOrInterfaceType();
            ClassOrInterfaceType srcClassType = new ClassOrInterfaceType();
            destClassType.setName(destinationClassName);
            if (StringUtils.isNotBlank(packageDeclaration)) {
                packageDeclaration = packageDeclaration + ".";
            }
            srcClassType.setName(packageDeclaration + srcClassName);
            mapperMethod.setType(destClassType);
            mapperMethod.addModifier(Modifier.PUBLIC);
            mapperMethod.setStatic(staticMethod);
            Parameter param = new Parameter(srcClassType, "p" + srcClassName);
            param.addModifier(Modifier.FINAL);
            mapperMethod.addParameter(param);
            Optional<BlockStmt> body = mapperMethod.getBody();
            if (body.isPresent()) {
                BlockStmt methodBody = body.get();
                VariableDeclarationExpr variableDeclarationExpr = new VariableDeclarationExpr();
                VariableDeclarator variableDeclarator = new VariableDeclarator();
                variableDeclarator.setType(destClassType);
                variableDeclarator.setName("l" + Utils.getSimpleClassName(destinationClassName));
                variableDeclarationExpr.addVariable(variableDeclarator);
                variableDeclarationExpr.addModifier(Modifier.FINAL);

                AssignExpr objectDeclarationStmt = new AssignExpr(variableDeclarator.getNameAsExpression(),
                        nullLiteralExpr, AssignExpr.Operator.ASSIGN);
                methodBody.addStatement(variableDeclarationExpr);
                IfStmt ifStmt = new IfStmt();
                Expression checkNotNullExpression = new BinaryExpr(param.getNameAsExpression(),
                        nullLiteralExpr, BinaryExpr.Operator.EQUALS);

                ifStmt.setCondition(checkNotNullExpression);
                ObjectCreationExpr objectCreationExpr = new ObjectCreationExpr();
                objectCreationExpr.setType(destClassType);
                AssignExpr objectCreationStmt = new AssignExpr(variableDeclarator.getNameAsExpression(),
                        objectCreationExpr, AssignExpr.Operator.ASSIGN);
                BlockStmt blockStmt = new BlockStmt();
                BlockStmt thenStatement = new BlockStmt();

                thenStatement.addStatement(objectDeclarationStmt);
                blockStmt.addStatement(objectCreationStmt);
                ifStmt.setThenStmt(thenStatement);
                methodBody.addStatement(ifStmt);

                ifStmt.setElseStmt(blockStmt);


                source.findAll(MethodDeclaration.class).forEach(methodDeclaration -> {
                    MethodCallExpr call;
                    String methodName = methodDeclaration.getName().asString();
                    String methodReturnType = methodDeclaration.getType().asString();
                    boolean isCollectionType = false;
                    NodeList<Parameter> methodParameters = methodDeclaration.getParameters();
                    if (methodName.startsWith("set")) {
                        if (methodParameters.size() == 1) {
                            Parameter parameter = methodParameters.get(0);
                            isCollectionType = isCollectionType(parameter.getType().asString());
                        }
                        if (accessCollectionByGetter && isCollectionType) {
                            return;
                        } else {
                            call = createCallStmt(methodDeclaration, param, variableDeclarator, true);
                        }
                    } else if (methodName.startsWith("get")
                            && accessCollectionByGetter
                            && isCollectionType(methodReturnType)) {
                        call = createCallStmt(methodDeclaration, param, variableDeclarator, false);
                    } else {
                        return;
                    }
                    blockStmt.addStatement(call);
                });
                methodBody.addStatement(new ReturnStmt(variableDeclarator.getNameAsExpression()));
            }
        }
    }

    public static boolean isCollectionType(String methodReturnType) {
        boolean ret;
        int index = StringUtils.indexOf(methodReturnType, "<");
        if (index >= 0) {
            methodReturnType = methodReturnType.substring(0, index);
        }
        try {
            Class clazz = Class.forName("java.util." + methodReturnType);
            ret = java.util.Collection.class.isAssignableFrom(clazz) ||
                    java.util.Map.class.isAssignableFrom(clazz);
        } catch (ClassNotFoundException e) {
            ret = false;
        }
        return ret;
    }

    public CompilationUnit createMapper(CompilationUnit source, String destClass) {
        // CompilationUnit destination  = new CompilationUnit();

        String packageDeclaration = "";
        if (source.getPackageDeclaration().isPresent()) {
            packageDeclaration = source.getPackageDeclaration().get().getName().asString();
        }
        CompilationUnit result = new CompilationUnit();
        String srcClassName = source.findFirst(ClassOrInterfaceDeclaration.class)
                .get().getName().asString();
        ClassOrInterfaceDeclaration myClass = result.addClass(srcClassName + "Mapper").setPublic(true);
        MethodDeclaration mapperMethod = myClass.addMethod("map" + srcClassName);
        ClassOrInterfaceType destClassType = new ClassOrInterfaceType();
        ClassOrInterfaceType srcClassType = new ClassOrInterfaceType();
        destClassType.setName(destClass);
        srcClassType.setName(packageDeclaration + "." + srcClassName);
        mapperMethod.setType(destClassType);
        mapperMethod.addModifier(Modifier.PUBLIC);
        Parameter param = new Parameter(srcClassType, "p" + srcClassName);
        param.addModifier(Modifier.FINAL);
        mapperMethod.addParameter(param);
        Optional<BlockStmt> body = mapperMethod.getBody();
        if (body.isPresent()) {
            VariableDeclarationExpr variableDeclarationExpr = new VariableDeclarationExpr();
            VariableDeclarator variableDeclarator = new VariableDeclarator();
            variableDeclarator.setType(destClassType);
            variableDeclarator.setName("l" + Utils.getSimpleClassName(destClass));
            variableDeclarationExpr.addVariable(variableDeclarator);
            AssignExpr objectDeclarationStmt = new AssignExpr(variableDeclarationExpr,
                    new NullLiteralExpr(), AssignExpr.Operator.ASSIGN);
            body.get().addStatement(objectDeclarationStmt);
            IfStmt ifStmt = new IfStmt();
            Expression checkNotNullExpression = new BinaryExpr(param.getNameAsExpression(),
                    new NullLiteralExpr(),
                    BinaryExpr.Operator.NOT_EQUALS);
            ifStmt.setCondition(checkNotNullExpression);
            ObjectCreationExpr objectCreationExpr = new ObjectCreationExpr();
            objectCreationExpr.setType(destClassType);
            AssignExpr objectCreationStmt = new AssignExpr(variableDeclarator.getNameAsExpression(),
                    objectCreationExpr, AssignExpr.Operator.ASSIGN);
            BlockStmt blockStmt = new BlockStmt();
            blockStmt.addStatement(objectCreationStmt);
            ifStmt.setThenStmt(blockStmt);
            body.get().addStatement(ifStmt);
            source.findAll(MethodDeclaration.class).forEach(methodDeclaration -> {
                MethodCallExpr call;
                if (methodDeclaration.getName().asString().startsWith("set")) {
                    call = createCallStmt(methodDeclaration, param, variableDeclarator, true);
                } else if (methodDeclaration.getName().asString().startsWith("get")
                        && (methodDeclaration.getType().asString().contains("List")
                        || methodDeclaration.getType().asString().contains("Collection"))) {
                    call = createCallStmt(methodDeclaration, param, variableDeclarator, false);
                } else {
                    return;
                }
                blockStmt.addStatement(call);
            });
            body.get().addStatement(new ReturnStmt(variableDeclarator.getNameAsExpression()));
        }
        return result;
    }

    private MethodCallExpr createCallStmt(MethodDeclaration methodDeclaration,
                                          Parameter param,
                                          VariableDeclarator variableDeclarator,
                                          boolean isSetter) {
        MethodCallExpr call = new MethodCallExpr(variableDeclarator.getNameAsExpression(),
                methodDeclaration.getName().asString());
        MethodCallExpr addAllMethod;
        MethodCallExpr retValue;
        String methodGetter = "get" + methodDeclaration.getName().asString().substring(3);
        MethodCallExpr getExpression = new MethodCallExpr(param.getNameAsExpression(), methodGetter);

        if (!isSetter) {
            addAllMethod = new MethodCallExpr(call, "addAll");
            addAllMethod.addArgument(getExpression);
            retValue = addAllMethod;
        } else {
            call.addArgument(getExpression);
            retValue = call;
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

    public void addJavaDocToResult() {
        JavaDocGenerator javaDocGenerator = JavaDocGenerator.getInstance();
        javaDocGenerator.generateJavaDocAsString(result, false);


    }
}