package musta.belmo.enumhandler.service;

import com.github.javaparser.JavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.Modifier;
import com.github.javaparser.ast.body.*;
import com.github.javaparser.ast.expr.*;
import com.github.javaparser.ast.stmt.BlockStmt;
import com.github.javaparser.ast.stmt.ExpressionStmt;
import com.github.javaparser.ast.stmt.ReturnStmt;
import com.github.javaparser.ast.type.TypeParameter;
import musta.belmo.javacodecore.Utils;

import java.util.List;

public class SpringControllerGenerator {


    public String generateService(String compilationUnit) {
        return generateService(JavaParser.parse(compilationUnit));
    }

    private String generateService(CompilationUnit compilationUnit) {
        CompilationUnit compilationUnitRet = new CompilationUnit();
        compilationUnitRet.setPackageDeclaration("com.gestparcauto.backend.controller");
        compilationUnitRet.addImport("org.springframework.beans.factory.annotation.Autowired");
        compilationUnitRet.addImport("org.springframework.http.ResponseEntity");
        compilationUnitRet.addImport("org.springframework.web.bind.annotation.*");
        compilationUnitRet.addImport("javax.validation.Valid");

        List<ClassOrInterfaceDeclaration> classes = compilationUnit.findAll(ClassOrInterfaceDeclaration.class);
        for (ClassOrInterfaceDeclaration aClass : classes) {
            String className = aClass.getName().asString();
            String classService = className + "Service";

            compilationUnitRet.addImport("com.gestparcauto.backend.model." + className);
            compilationUnitRet.addImport("com.gestparcauto.backend.service." + className + "Service");
            ClassOrInterfaceDeclaration declaration = compilationUnitRet.addClass(className + "Controller");
            MarkerAnnotationExpr restControllerAnnotation = new MarkerAnnotationExpr();
            restControllerAnnotation.setName("RestController");
            declaration.addAnnotation(restControllerAnnotation);
            SingleMemberAnnotationExpr annotationExpr = new SingleMemberAnnotationExpr(new Name("RequestMapping")
                    , new StringLiteralExpr("/api"));
            declaration.addAnnotation(annotationExpr);

            String serviceField = Utils.toLowerCaseFirstLetter(className) + "Service";
            FieldDeclaration field = declaration.addField(new TypeParameter(classService),
                    serviceField);
            AnnotationExpr ann = new MarkerAnnotationExpr();
            ann.setName("Autowired");
            field.addAnnotation(ann);
            field.setPrivate(true);
            compilationUnitRet.addImport("java.util.List");

            addGetAllMethod(declaration, className, serviceField);
            addCreateMethod(declaration, className, serviceField);
            addGetByIdMethod(declaration, className, serviceField);
            addUpdateMethod(declaration, className, serviceField);
            addDeleteMethod(declaration, className, serviceField);
        }
        return compilationUnitRet.toString();
    }

    private void addCreateMethod(ClassOrInterfaceDeclaration declaration, String aClass, String field) {

        MethodDeclaration methodDeclaration = declaration.addMethod("create" + aClass, Modifier.PUBLIC);

        SingleMemberAnnotationExpr annotationExpr = new SingleMemberAnnotationExpr();
        annotationExpr.setName("PostMapping");
        annotationExpr.setMemberValue(new StringLiteralExpr(String.format("/%ss", Utils.toLowerCaseFirstLetter(aClass))));


        MarkerAnnotationExpr requestBodyAnnotation = new MarkerAnnotationExpr();
        requestBodyAnnotation.setName("RequestBody");

        methodDeclaration.addAnnotation(annotationExpr);
        Parameter parameter = new Parameter();
        parameter.addAnnotation(requestBodyAnnotation);
        parameter.addModifier(Modifier.FINAL);
        String paramName = "p" + aClass;
        parameter.setType(new TypeParameter(aClass));
        parameter.setName(paramName);
        methodDeclaration.addParameter(parameter);
        methodDeclaration.setType(new TypeParameter(aClass));
        BlockStmt blockStmt = new BlockStmt();

        NameExpr scope = new NameExpr();
        scope.setName(field);
        MethodCallExpr methodCallExpr = new MethodCallExpr(scope, "create");
        methodCallExpr.addArgument(paramName);
        ReturnStmt statement = new ReturnStmt(methodCallExpr);

        blockStmt.addStatement(statement);
        methodDeclaration.setBody(blockStmt);

    }

    private void addUpdateMethod(ClassOrInterfaceDeclaration declaration, String aClass, String field) {
        SingleMemberAnnotationExpr annotationExpr = new SingleMemberAnnotationExpr();
        annotationExpr.setName("PutMapping");
        annotationExpr.setMemberValue(new StringLiteralExpr(String.format("/%ss/{id}", Utils.toLowerCaseFirstLetter(aClass))));
        MethodDeclaration methodDeclaration = declaration.addMethod("update" + aClass, Modifier.PUBLIC);
        methodDeclaration.addAnnotation(annotationExpr);

        Parameter classTypeParameter = new Parameter();
        SingleMemberAnnotationExpr pathVarAnnotation = new SingleMemberAnnotationExpr();
        pathVarAnnotation.setName("PathVariable");
        pathVarAnnotation.setMemberValue(new StringLiteralExpr("id"));
        Parameter idParameter = new Parameter();
        idParameter.addAnnotation(pathVarAnnotation);
        classTypeParameter.addModifier(Modifier.FINAL);

        MarkerAnnotationExpr reqBodyAnnotation = new MarkerAnnotationExpr();
        reqBodyAnnotation.setName("RequestBody");

        // MarkerAnnotationExpr validAnnotation = new MarkerAnnotationExpr();
        // validAnnotation.setName("Valid");
        classTypeParameter.addAnnotation(reqBodyAnnotation);
        //  classTypeParameter.addAnnotation(validAnnotation);
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
        MethodCallExpr methodCallExpr = new MethodCallExpr(scope, "update");
        methodCallExpr.addArgument(idName);
        methodCallExpr.addArgument(paramName);
        ReturnStmt statement = new ReturnStmt(methodCallExpr);

        blockStmt.addStatement(statement);
        methodDeclaration.setBody(blockStmt);

    }

    private void addGetByIdMethod(ClassOrInterfaceDeclaration declaration, String aClass, String field) {
        String entityPlural = String.format("%ss", Utils.toLowerCaseFirstLetter(aClass));

        SingleMemberAnnotationExpr annotationExpr = new SingleMemberAnnotationExpr();
        annotationExpr.setName("GetMapping");
        annotationExpr.setMemberValue(new StringLiteralExpr(String.format("(/%s/{id})", entityPlural)));
        MethodDeclaration methodDeclaration = declaration.addMethod(String.format("get%sById", aClass), Modifier.PUBLIC);
        methodDeclaration.addAnnotation(annotationExpr);

        Parameter parameter = new Parameter();
        parameter.addModifier(Modifier.FINAL);
        SingleMemberAnnotationExpr annotation = new SingleMemberAnnotationExpr();
        annotation.setName("PathVariable");
        annotation.setMemberValue(new StringLiteralExpr("id"));
        parameter.addAnnotation(annotation);
        String paramName = "p" + aClass + "Id";
        parameter.setType(new TypeParameter("Long"));
        parameter.setName(paramName);
        methodDeclaration.addParameter(parameter);
        methodDeclaration.setType(new TypeParameter(aClass));
        BlockStmt blockStmt = new BlockStmt();

        NameExpr scope = new NameExpr();
        scope.setName(field);
        MethodCallExpr methodCallExpr = new MethodCallExpr(scope, "getById");
        methodCallExpr.addArgument(paramName);

        ReturnStmt statement = new ReturnStmt(methodCallExpr);

        blockStmt.addStatement(statement);
        methodDeclaration.setBody(blockStmt);

    }

    private void addDeleteMethod(ClassOrInterfaceDeclaration declaration, String aClass, String field) {
        MethodDeclaration methodDeclaration = declaration.addMethod("delete" + aClass, Modifier.PUBLIC);
        SingleMemberAnnotationExpr deleteMappingAnnotation = new SingleMemberAnnotationExpr();
        deleteMappingAnnotation.setName("DeleteMapping");
        String entityPlural = String.format("%ss", Utils.toLowerCaseFirstLetter(aClass));

        deleteMappingAnnotation.setMemberValue(new StringLiteralExpr("/" + entityPlural + "/{id}"));
        methodDeclaration.addAnnotation(deleteMappingAnnotation);

        Parameter parameter = new Parameter();

        parameter.addModifier(Modifier.FINAL);
        String paramName = "p" + aClass + "Id";
        parameter.setType(new TypeParameter("Long"));
        parameter.setName(paramName);
        SingleMemberAnnotationExpr annotation = new SingleMemberAnnotationExpr();
        annotation.setName("PathVariable");
        annotation.setMemberValue(new StringLiteralExpr("id"));
        parameter.addAnnotation(annotation);
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
        MethodCallExpr methodCallExpr = new MethodCallExpr(scope, "delete");
        methodCallExpr.addArgument(paramName);
        ReturnStmt returnStmt = new ReturnStmt(methodCallExpr);
        blockStmt.addStatement(returnStmt);
        methodDeclaration.setBody(blockStmt);
    }

    private void addGetAllMethod(ClassOrInterfaceDeclaration declaration, String aClass, String field) {

        String entityPlural = String.format("%ss", Utils.toLowerCaseFirstLetter(aClass));
        MethodDeclaration methodDeclaration = declaration.addMethod("getAll" + aClass, Modifier.PUBLIC);
        SingleMemberAnnotationExpr annotationExpr = new SingleMemberAnnotationExpr();
        annotationExpr.setName("GetMapping");
        annotationExpr.setMemberValue(new StringLiteralExpr("/" + entityPlural));

        methodDeclaration.addAnnotation(annotationExpr);
        methodDeclaration.setType(new TypeParameter("List<" + aClass + ">"));
        BlockStmt blockStmt = new BlockStmt();

        NameExpr scope = new NameExpr();
        scope.setName(field);
        MethodCallExpr methodCallExpr = new MethodCallExpr(scope, "getAll");
        ReturnStmt statement = new ReturnStmt(methodCallExpr);

        blockStmt.addStatement(statement);
        methodDeclaration.setBody(blockStmt);
    }

    public static void main(String[] args) {
        SpringControllerGenerator springBootGenerator = new SpringControllerGenerator();
        System.out.println(springBootGenerator.generateService("class Vehicule {}"));
    }
}
