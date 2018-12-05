package musta.belmo.enumhandler.service;

import com.github.javaparser.JavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.Modifier;
import com.github.javaparser.ast.body.*;
import com.github.javaparser.ast.expr.*;
import com.github.javaparser.ast.stmt.*;
import com.github.javaparser.ast.type.TypeParameter;
import musta.belmo.javacodecore.Utils;

import java.util.List;

public class SpringBootServiceGenerator {


    public CompilationUnit generateService(String compilationUnit) {
        return generateService(JavaParser.parse(compilationUnit));
    }

    private CompilationUnit generateService(CompilationUnit compilationUnit) {
        CompilationUnit compilationUnitRet = new CompilationUnit();
        compilationUnitRet.setPackageDeclaration("com.gestparcauto.backend.service.impl");
        compilationUnitRet.addImport("org.springframework.beans.factory.annotation.Autowired");
        compilationUnitRet.addImport("org.springframework.stereotype.Component");
        compilationUnitRet.addImport("org.springframework.http.ResponseEntity");
        compilationUnitRet.addImport("com.gestparcauto.backend.exception.ResourceNotFoundException");

        List<ClassOrInterfaceDeclaration> classes = compilationUnit.findAll(ClassOrInterfaceDeclaration.class);
        for (ClassOrInterfaceDeclaration aClass : classes) {
            String className = aClass.getName().asString();
            String classRepository = className + "Repository";
            compilationUnitRet.addImport("com.gestparcauto.backend.service.api." + className + "Service");
            compilationUnitRet.addImport("com.gestparcauto.backend.model." + className);
            compilationUnitRet.addImport("com.gestparcauto.backend.repository." + classRepository);
            ClassOrInterfaceDeclaration declaration = compilationUnitRet.addClass(className + "ServiceImpl");
            declaration.addImplementedType(className + "Service");
            MarkerAnnotationExpr componentAnnotation = new MarkerAnnotationExpr();
            componentAnnotation.setName("Component");
            declaration.addAnnotation(componentAnnotation);

            String repoField = Utils.toLowerCaseFirstLetter(className) + "Repository";
            FieldDeclaration field = declaration.addField(new TypeParameter(classRepository),
                    repoField);
            MarkerAnnotationExpr autoWiredAnnotation = new MarkerAnnotationExpr();
            autoWiredAnnotation.setName("Autowired");
            field.addAnnotation(autoWiredAnnotation);
            field.setPrivate(true);
            compilationUnitRet.addImport("java.util.List");
            addGetAllMethod(declaration, className, repoField);
            addCreateMethod(declaration, className, repoField);
            addGetByIdMethod(declaration, className, repoField);
            addUpdateMethod(declaration, className, repoField);
            addDeleteMethod(declaration, className, repoField);
        }
        return compilationUnitRet;
    }

    private void addCreateMethod(ClassOrInterfaceDeclaration declaration, String aClass, String field) {
        MethodDeclaration methodDeclaration = declaration.addMethod("create", Modifier.PUBLIC);
        MarkerAnnotationExpr overrideAnnotation = new MarkerAnnotationExpr();
        overrideAnnotation.setName("Override");
        methodDeclaration.addAnnotation(overrideAnnotation);
        Parameter parameter = new Parameter();
        parameter.addModifier(Modifier.FINAL);
        String paramName = "p" + aClass;
        parameter.setType(new TypeParameter(aClass));
        parameter.setName(paramName);
        methodDeclaration.addParameter(parameter);
        methodDeclaration.setType(new TypeParameter(aClass));
        BlockStmt blockStmt = new BlockStmt();

        NameExpr scope = new NameExpr();
        scope.setName(field);
        MethodCallExpr methodCallExpr = new MethodCallExpr(scope, "save");
        methodCallExpr.addArgument(paramName);
        ReturnStmt statement = new ReturnStmt(methodCallExpr);

        blockStmt.addStatement(statement);
        methodDeclaration.setBody(blockStmt);

    }

    private void addUpdateMethod(ClassOrInterfaceDeclaration declaration, String aClass, String field) {
        MethodDeclaration methodDeclaration = declaration.addMethod("update", Modifier.PUBLIC);
        MarkerAnnotationExpr overrideAnnotation = new MarkerAnnotationExpr();
        overrideAnnotation.setName("Override");
        methodDeclaration.addAnnotation(overrideAnnotation);
        Parameter classTypeParameter = new Parameter();
        Parameter idParameter = new Parameter();
        classTypeParameter.addModifier(Modifier.FINAL);
        idParameter.addModifier(Modifier.FINAL);
        String paramName = "p" + aClass;
        String idName = "p" + aClass + "Id";
        classTypeParameter.setType(new TypeParameter(aClass));
        idParameter.setType(new TypeParameter("Long"));
        classTypeParameter.setName(paramName);
        idParameter.setName(idName);
        methodDeclaration.addParameter(idParameter);
        methodDeclaration.addParameter(classTypeParameter);
        methodDeclaration.setType(new TypeParameter(aClass));
        BlockStmt blockStmt = new BlockStmt();

        NameExpr scope = new NameExpr();
        scope.setName(field);
        MethodCallExpr methodCallExpr = new MethodCallExpr(scope, "save");
        methodCallExpr.addArgument(paramName);
        ReturnStmt statement = new ReturnStmt(methodCallExpr);

        blockStmt.addStatement(statement);
        methodDeclaration.setBody(blockStmt);

    }

    private void addGetByIdMethod(ClassOrInterfaceDeclaration declaration, String aClass, String field) {
        MethodDeclaration methodDeclaration = declaration.addMethod("getById", Modifier.PUBLIC);
        MarkerAnnotationExpr overrideAnnotation = new MarkerAnnotationExpr();
        overrideAnnotation.setName("Override");
        methodDeclaration.addAnnotation(overrideAnnotation);
        Parameter parameter = new Parameter();
        parameter.addModifier(Modifier.FINAL);
        String paramName = "p" + aClass + "Id";
        parameter.setType(new TypeParameter("Long"));
        parameter.setName(paramName);
        methodDeclaration.addParameter(parameter);
        methodDeclaration.setType(new TypeParameter(aClass));
        BlockStmt blockStmt = new BlockStmt();

        NameExpr scope = new NameExpr();
        scope.setName(field);
        MethodCallExpr methodCallExpr = new MethodCallExpr(scope, "findById");
        methodCallExpr.addArgument(paramName);

        MethodCallExpr orElseThrowExpression = new MethodCallExpr(methodCallExpr, "orElseThrow");
        LambdaExpr lambdaExpr = new LambdaExpr();
        ObjectCreationExpr objectCreationExpr = new ObjectCreationExpr();
        objectCreationExpr.addArgument(new StringLiteralExpr(aClass));
        objectCreationExpr.addArgument(new StringLiteralExpr("id"));
        objectCreationExpr.addArgument(paramName);
        objectCreationExpr.setType("ResourceNotFoundException");
        BlockStmt lambdaBlockStmt = new BlockStmt();
        ExpressionStmt expressionStmt = new ExpressionStmt(objectCreationExpr);
        lambdaBlockStmt.addStatement(objectCreationExpr);
        lambdaExpr.setEnclosingParameters(true);
        lambdaExpr.setBody(expressionStmt);
        orElseThrowExpression.addArgument(lambdaExpr);
        ReturnStmt statement = new ReturnStmt(orElseThrowExpression);

        blockStmt.addStatement(statement);
        methodDeclaration.setBody(blockStmt);

    }

    private void addDeleteMethod(ClassOrInterfaceDeclaration declaration, String aClass, String field) {
        MethodDeclaration methodDeclaration = declaration.addMethod("delete", Modifier.PUBLIC);
        MarkerAnnotationExpr overrideAnnotation = new MarkerAnnotationExpr();
        overrideAnnotation.setName("Override");
        methodDeclaration.addAnnotation(overrideAnnotation);
        Parameter parameter = new Parameter();
        parameter.addModifier(Modifier.FINAL);
        String paramName = "p" + aClass + "Id";
        parameter.setType(new TypeParameter("Long"));
        parameter.setName(paramName);
        methodDeclaration.addParameter(parameter);
        methodDeclaration.setType(new TypeParameter("ResponseEntity<?>"));
        BlockStmt blockStmt = new BlockStmt();
        VariableDeclarationExpr variableDeclarationExpr = new VariableDeclarationExpr();
        VariableDeclarator variableDeclarator = new VariableDeclarator();
        String varName = "l" + aClass;
        variableDeclarator.setName(varName);
        variableDeclarator.setType(aClass);
        variableDeclarationExpr.addVariable(variableDeclarator);
        NameExpr scope = new NameExpr();
        scope.setName(field);
        MethodCallExpr methodCallExpr = new MethodCallExpr(scope, "findById");
        methodCallExpr.addArgument(paramName);
        MethodCallExpr orElseThrowExpression = new MethodCallExpr(methodCallExpr, "orElseThrow");
        LambdaExpr lambdaExpr = new LambdaExpr();
        ObjectCreationExpr objectCreationExpr = new ObjectCreationExpr();
        objectCreationExpr.addArgument(new StringLiteralExpr(aClass));
        objectCreationExpr.addArgument(new StringLiteralExpr("id"));
        objectCreationExpr.addArgument(paramName);
        objectCreationExpr.setType("ResourceNotFoundException");
        ExpressionStmt expressionStmt = new ExpressionStmt(objectCreationExpr);
        lambdaExpr.setEnclosingParameters(true);
        lambdaExpr.setBody(expressionStmt);
        orElseThrowExpression.addArgument(lambdaExpr);
        AssignExpr assignExpr = new AssignExpr(variableDeclarationExpr, orElseThrowExpression, AssignExpr.Operator.ASSIGN);
        blockStmt.addStatement(assignExpr);
        MethodCallExpr repoDeleteMethodCall = new MethodCallExpr(scope, "delete");
        repoDeleteMethodCall.addArgument(varName);
        blockStmt.addStatement(repoDeleteMethodCall);
        methodDeclaration.setBody(blockStmt);
        NameExpr expr = new NameExpr("ResponseEntity.ok().build()");
        ReturnStmt returnStmt = new ReturnStmt(expr);
        blockStmt.addStatement(returnStmt);
    }

    private void addGetAllMethod(ClassOrInterfaceDeclaration declaration, String aClass, String field) {
        MethodDeclaration methodDeclaration = declaration.addMethod("getAll", Modifier.PUBLIC);
        methodDeclaration.setType(new TypeParameter("List<" + aClass + ">"));

        MarkerAnnotationExpr overrideAnnotation = new MarkerAnnotationExpr();
        overrideAnnotation.setName("Override");
        methodDeclaration.addAnnotation(overrideAnnotation);
        BlockStmt blockStmt = new BlockStmt();

        NameExpr scope = new NameExpr();
        scope.setName(field);
        MethodCallExpr methodCallExpr = new MethodCallExpr(scope, "findAll");
        ReturnStmt statement = new ReturnStmt(methodCallExpr);

        blockStmt.addStatement(statement);
        methodDeclaration.setBody(blockStmt);
    }


    public static void main(String[] args) {
        SpringBootServiceGenerator springBootServiceGenerator = new SpringBootServiceGenerator();
        System.out.println(springBootServiceGenerator.generateService("class Vehicule {}"));

    }
}
