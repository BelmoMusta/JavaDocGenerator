package musta.belmo.javacodegenerator.service.visitor;

import com.github.javaparser.ast.body.MethodDeclaration;
import musta.belmo.javacodegenerator.service.MethodJavaDoc;

public class MethodDeclarationVisitor extends AbstractCommonVisitor<MethodDeclaration> {
    private static final MethodDeclarationVisitor INSTANCE = new MethodDeclarationVisitor();

    public static MethodDeclarationVisitor getInstance() {
        return INSTANCE;
    }

    @Override
    public void visit(MethodDeclaration src, MethodDeclaration methodDeclaration) {
        MethodJavaDoc.addMethodJavaDocDescription(methodDeclaration);
        addThrowBlockTags(src, methodDeclaration);
        super.visit(src, methodDeclaration);
    }
}
