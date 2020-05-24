package musta.belmo.javacodegenerator.service.visitor;

import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.FieldDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.visitor.VoidVisitorAdapter;

public class CompilationUnitJavaDocVisitor extends VoidVisitorAdapter<CompilationUnit> {

    private MethodJavaDocVisitor methodJavaDocVisitor = MethodJavaDocVisitor.getInstance();
    private FieldJavaDocVisitor fieldJavaDocVisitor = FieldJavaDocVisitor.getInstance();

    @Override
    public void visit(MethodDeclaration methodDeclaration, CompilationUnit compilationUnit) {
        methodDeclaration.accept(methodJavaDocVisitor, methodDeclaration);
        super.visit(methodDeclaration, compilationUnit);
    }

    @Override
    public void visit(FieldDeclaration fieldDeclaration, CompilationUnit compilationUnit) {
        fieldDeclaration.accept(fieldJavaDocVisitor, fieldDeclaration);
        super.visit(fieldDeclaration, compilationUnit);
    }
}
