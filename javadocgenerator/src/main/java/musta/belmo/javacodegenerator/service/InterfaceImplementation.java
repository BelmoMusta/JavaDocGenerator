package musta.belmo.javacodegenerator.service;


import com.github.javaparser.JavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.Modifier;
import com.github.javaparser.ast.PackageDeclaration;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.FieldDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.expr.*;
import com.github.javaparser.ast.stmt.BlockStmt;
import com.github.javaparser.ast.stmt.ReturnStmt;
import com.github.javaparser.ast.stmt.ThrowStmt;
import com.github.javaparser.ast.type.Type;
import com.github.javaparser.javadoc.Javadoc;
import com.github.javaparser.javadoc.description.JavadocDescription;
import com.github.javaparser.javadoc.description.JavadocSnippet;
import musta.belmo.javacodecore.CodeUtils;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;

public class InterfaceImplementation extends AbstractJavaCodeGenerator {
    static final String PATH = "C:\\Users\\mbelmokhtar\\Desktop\\Nouveau dossier\\JavaDocGenerator\\javadocgenerator\\src\\main\\resources\\";
    private static final Predicate<MethodDeclaration> IS_SET = aMethod -> aMethod.getNameAsString().length() > 3
            && aMethod.getName().toString().startsWith("set")
            && aMethod.getParameters().size() == 1;
    private static final Predicate<MethodDeclaration> IS_GET = aMethod -> aMethod.getNameAsString().startsWith("get") && aMethod.getParameters().isEmpty();
    private static final Predicate<MethodDeclaration> IS_IS = aMethod -> aMethod.getNameAsString().startsWith("is");
    private static final Predicate<MethodDeclaration> IS_VOID = aMethod -> aMethod.getType().isVoidType();
    private static final Predicate<MethodDeclaration> OTHER_METHODS = IS_SET.negate()
            .and(IS_GET.negate())
            .and(IS_IS.negate());

    @Override
    public CompilationUnit generate(CompilationUnit compilationUnitSrc) {
        CompilationUnit compilationUnit = compilationUnitSrc.clone();
        Optional<PackageDeclaration> packageDeclaration = compilationUnit.getPackageDeclaration();
        if (packageDeclaration.isPresent()) {
            compilationUnit.setPackageDeclaration(packageDeclaration.map(p -> p.getNameAsString() + ".impl").orElse("impl"));

            compilationUnit.addImport(packageDeclaration.get().getNameAsString());
        }
        compilationUnit.findAll(ClassOrInterfaceDeclaration.class)
                .stream()
                .filter(ClassOrInterfaceDeclaration::isInterface)
                .forEach(aClass -> {
                    List<MethodDeclaration> methods = aClass.findAll(MethodDeclaration.class);

                    setupClassImplementation(aClass);
                    setupGetters(aClass);
                    setupSetters(methods);
                    setupBooleanGetters(methods);
                    setupOtherMethods(methods);
                    setupOtherVoidMethods(methods);
                    setupJavaDoc(methods);
                    setupOverrideAnnotation(methods);
                });

        return compilationUnit;

    }

    private void setupBooleanGetters(List<MethodDeclaration> declarations) {
        CodeUtils.reverse(declarations.stream())
                .filter(IS_IS)
                .forEach(aMethod -> {
                    String methodeName = aMethod.getName().toString().substring(2);
                    FieldDeclaration fieldDeclaration = CodeUtils.newField(aMethod.getType(),
                            "a" + methodeName,
                            Modifier.PRIVATE);
                    ReturnStmt returnStmt = new ReturnStmt(fieldDeclaration.getVariable(0).getNameAsExpression());
                    BlockStmt blockStmt = new BlockStmt();
                    blockStmt.addStatement(returnStmt);
                    aMethod.setBody(blockStmt);
                });
    }

    private void setupGetters(ClassOrInterfaceDeclaration aClass) {
        CodeUtils.reverse(aClass.findAll(MethodDeclaration.class).stream())
                .filter(IS_GET)
                .forEach(aMethod -> {
                    String methodName = aMethod.getName().toString().substring(3);
                    FieldDeclaration fieldDeclaration = CodeUtils.newField(aMethod.getType(),
                            "a" + methodName,
                            Modifier.PRIVATE);
                    aClass.getMembers().add(0, fieldDeclaration);
                    ReturnStmt returnStmt = new ReturnStmt(fieldDeclaration.getVariable(0).getNameAsExpression());
                    BlockStmt blockStmt = new BlockStmt();
                    blockStmt.addStatement(returnStmt);
                    aMethod.setBody(blockStmt);
                });
    }

    private void setupClassImplementation(ClassOrInterfaceDeclaration aClass) {
        aClass.setInterface(false);
        aClass.addImplementedType(aClass.getNameAsString());
        aClass.setName(aClass.getName() + "Impl");
    }

    private void setupSetters(List<MethodDeclaration> declarations) {
        declarations.stream()
                .filter(IS_SET)
                .forEach(setterMethod -> {
                    String methodName = setterMethod.getName().toString().substring(3);
                    BlockStmt blockStmt = new BlockStmt();
                    if (setterMethod.getParameters().size() == 1) {
                        NameExpr paramName = setterMethod.getParameter(0).getNameAsExpression();
                        String fieldName = "a" + methodName;
                        if (fieldName.equals(paramName.getNameAsString())) {
                            paramName.setName("p" + StringUtils.capitalize(paramName.getNameAsString()));
                        }
                        Expression assign = new AssignExpr(new NameExpr(fieldName),
                                paramName, AssignExpr.Operator.ASSIGN);
                        blockStmt.addStatement(assign);
                    } else {
                        blockStmt.setBlockComment("TODO");
                    }
                    setterMethod.setBody(blockStmt);
                });
    }

    private void setupOtherMethods(List<MethodDeclaration> declarations) {
        declarations.stream()
                .filter(OTHER_METHODS.and(IS_VOID.negate()))
                .forEach(setterMethod -> {
                    BlockStmt blockStmt = new BlockStmt();
                    setterMethod.setBody(blockStmt);
                    Type type = setterMethod.getType();
                    final Expression expression;
                    if (type.isPrimitiveType()) {
                        String defaultValue = CodeUtils.getTypeDefaultValue(type.asPrimitiveType());
                        expression = new NameExpr(defaultValue);
                    } else {
                        expression = new NullLiteralExpr();
                    }
                    blockStmt.addStatement(new ReturnStmt(expression));
                });
    }

    private void setupOtherVoidMethods(List<MethodDeclaration> declarations) {
        declarations.stream()
                .filter(OTHER_METHODS)
                .filter(IS_VOID)
                .forEach(setterMethod -> {
                    BlockStmt blockStmt = new BlockStmt();
                    setterMethod.setBody(blockStmt);
                    ObjectCreationExpr objectCreationExpr = new ObjectCreationExpr();
                    objectCreationExpr.setType("UnsupportedOperationException");
                    objectCreationExpr.addArgument(new StringLiteralExpr("FIXME : Not implemented yet"));
                    ThrowStmt throwStmt = new ThrowStmt(objectCreationExpr);
                    blockStmt.addStatement(throwStmt);

                });
    }

    private void setupJavaDoc(List<MethodDeclaration> declarations) {
        declarations
                .forEach(methodDeclaration -> {
                    JavadocDescription javadocDescription = new JavadocDescription();
                    Javadoc javadoc = new Javadoc(javadocDescription);
                    JavadocSnippet inheritDocSnippet = new JavadocSnippet("{@inheritDoc}");
                    methodDeclaration.removeJavaDocComment();
                    javadocDescription.addElement(inheritDocSnippet);
                    methodDeclaration.setJavadocComment(javadoc);
                });
    }

    private void setupOverrideAnnotation(List<MethodDeclaration> declarations) {
        declarations
                .forEach(methodDeclaration -> {
                    methodDeclaration.addModifier(Modifier.PUBLIC);
                    methodDeclaration.addAnnotation(new MarkerAnnotationExpr("Override"));
                });
    }

    public static void main(String[] args) {

        FileUtils.listFiles(new File(PATH), new String[]{"java"}, true).forEach(file -> {


            CompilationUnit compilationUnit = null;
            try {
                compilationUnit = JavaParser.parse(file);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
            CompilationUnit fieldsFromGetters = new InterfaceImplementation().generate(compilationUnit);
            System.out.println(fieldsFromGetters);
        });

    }
}
