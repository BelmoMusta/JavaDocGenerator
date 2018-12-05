package musta.belmo.enumhandler.service;

import com.github.javaparser.JavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.expr.MarkerAnnotationExpr;

public class RepostioryController {

    public String createRepository(String className) {
        CompilationUnit compilationUnitRet = new CompilationUnit();
        compilationUnitRet.setPackageDeclaration("com.gestparcauto.backend.repository");
        compilationUnitRet.addImport("org.springframework.stereotype.Repository");
        compilationUnitRet.addImport("org.springframework.data.jpa.repository.JpaRepository");
        compilationUnitRet.addImport("com.gestparcauto.backend.model." + className);

        ClassOrInterfaceDeclaration interfaceDeclaration =
                compilationUnitRet.addInterface(className + "Repository");
        interfaceDeclaration.addExtendedType(String.format("JpaRepository<%s, Long>", className));

        MarkerAnnotationExpr repoAnnotation = new MarkerAnnotationExpr();
        repoAnnotation.setName("Repository");
        interfaceDeclaration.addAnnotation(repoAnnotation);
        return compilationUnitRet.toString();
    }

    public static void main(String[] args) {
        RepostioryController repo = new RepostioryController();
        System.out.println(repo.createRepository("Assurance"));
    }
}
