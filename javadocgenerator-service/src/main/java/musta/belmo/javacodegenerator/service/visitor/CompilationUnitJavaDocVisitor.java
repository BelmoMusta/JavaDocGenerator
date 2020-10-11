package musta.belmo.javacodegenerator.service.visitor;

import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;

public class CompilationUnitJavaDocVisitor extends AbstractCommonVisitor<CompilationUnit> {

    private ClassOrInterfaceJavaDocVisitor classOrInterfaceJavaDocVisitor = ClassOrInterfaceJavaDocVisitor.getInstance();

    @Override
    public void visit(ClassOrInterfaceDeclaration n, CompilationUnit arg) {
        n.accept(classOrInterfaceJavaDocVisitor, n);
        super.visit(n, arg);
    }
}
