package musta.belmo.enumhandler.service;

import com.github.javaparser.JavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.Modifier;
import com.github.javaparser.ast.body.*;
import com.github.javaparser.ast.expr.*;
import com.github.javaparser.ast.type.TypeParameter;

import java.util.List;

public class SpringBootServiceApiGenerator {


    public CompilationUnit generateService(String compilationUnit) {
        return generateService(JavaParser.parse(compilationUnit));
    }

    private CompilationUnit generateService(CompilationUnit compilationUnit) {
        CompilationUnit compilationUnitRet = new CompilationUnit();
        compilationUnitRet.setPackageDeclaration("com.gestparcauto.backend.service.api");
        compilationUnitRet.addImport("org.springframework.http.ResponseEntity");

        List<ClassOrInterfaceDeclaration> classes = compilationUnit.findAll(ClassOrInterfaceDeclaration.class);
        for (ClassOrInterfaceDeclaration aClass : classes) {
            String className = aClass.getName().asString();
            compilationUnitRet.addImport("com.gestparcauto.backend.model." + className);
            ClassOrInterfaceDeclaration declaration = compilationUnitRet.addInterface(className + "Service");
            MarkerAnnotationExpr componentAnnotation = new MarkerAnnotationExpr();
            componentAnnotation.setName("Component");
            // declaration.addAnnotation(componentAnnotation);

            //String repoField = Utils.toLowerCaseFirstLetter(className) + "Repository";
            //FieldDeclaration field = declaration.addField(new TypeParameter(classRepository),
            //         repoField);
            // MarkerAnnotationExpr autoWiredAnnotation = new MarkerAnnotationExpr();
            // autoWiredAnnotation.setName("Autowired");
            // field.addAnnotation(autoWiredAnnotation);
            // field.setPrivate(true);
            compilationUnitRet.addImport("java.util.List");

            addGetAllMethod(declaration, className);
            addCreateMethod(declaration, className);
            addGetByIdMethod(declaration, className);
            addUpdateMethod(declaration, className);
            addDeleteMethod(declaration, className);
        }
        return compilationUnitRet;
    }

    private void addCreateMethod(ClassOrInterfaceDeclaration declaration, String aClass) {
        MethodDeclaration methodDeclaration = declaration.addMethod("create");
        Parameter parameter = new Parameter();
        parameter.addModifier(Modifier.FINAL);
        String paramName = "p" + aClass;
        parameter.setType(new TypeParameter(aClass));
        parameter.setName(paramName);
        methodDeclaration.addParameter(parameter);
        methodDeclaration.setType(new TypeParameter(aClass));
        methodDeclaration.setBody(null);

    }

    private void addUpdateMethod(ClassOrInterfaceDeclaration declaration, String aClass) {
        MethodDeclaration methodDeclaration = declaration.addMethod("update");
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
        methodDeclaration.setBody(null);

    }

    private void addGetByIdMethod(ClassOrInterfaceDeclaration declaration, String aClass) {
        MethodDeclaration methodDeclaration = declaration.addMethod("getById");
        Parameter parameter = new Parameter();
        parameter.addModifier(Modifier.FINAL);
        String paramName = "p" + aClass + "Id";
        parameter.setType(new TypeParameter("Long"));
        parameter.setName(paramName);
        methodDeclaration.addParameter(parameter);
        methodDeclaration.setType(new TypeParameter(aClass));
        methodDeclaration.setBody(null);

    }

    private void addDeleteMethod(ClassOrInterfaceDeclaration declaration, String aClass) {
        MethodDeclaration methodDeclaration = declaration.addMethod("delete");
        Parameter parameter = new Parameter();
        parameter.addModifier(Modifier.FINAL);
        String paramName = "p" + aClass + "Id";
        parameter.setType(new TypeParameter("Long"));
        parameter.setName(paramName);
        methodDeclaration.addParameter(parameter);
        methodDeclaration.setType(new TypeParameter("ResponseEntity<?>"));
        methodDeclaration.setBody(null);

    }

    private void addGetAllMethod(ClassOrInterfaceDeclaration declaration, String aClass) {
        MethodDeclaration methodDeclaration = declaration.addMethod("getAll");
        methodDeclaration.setBody(null);
        methodDeclaration.setType(new TypeParameter("List<" + aClass + ">"));

    }

    public static void main(String[] args) {
        SpringBootServiceApiGenerator springBootServiceGenerator = new SpringBootServiceApiGenerator();
        System.out.println(springBootServiceGenerator.generateService("class Vehicule {}"));

    }
}
