package musta.belmo.javacodegenerator.service.visitor;

import com.github.javaparser.ast.body.FieldDeclaration;
import com.github.javaparser.ast.body.VariableDeclarator;
import com.github.javaparser.ast.visitor.VoidVisitorAdapter;

public class FieldJavaDocVisitor extends AbstractCommonVisitor<FieldDeclaration> {
    private static final FieldJavaDocVisitor INSTANCE = new FieldJavaDocVisitor();
    private static final VariableDeclaratorVisitor VARIABLE_DECLARATOR = VariableDeclaratorVisitor.getInstance();
    private static final StaticFinalVariableDeclaratorVisitor STATIC_FINAL_VARIABLE_DECLARATOR_VISITOR = StaticFinalVariableDeclaratorVisitor.getInstance();

    public static FieldJavaDocVisitor getInstance() {
        return INSTANCE;
    }

    @Override
    public void visit(VariableDeclarator variableDeclarator, FieldDeclaration fieldDeclaration) {

        VoidVisitorAdapter<FieldDeclaration> visitor = VARIABLE_DECLARATOR;
        if (fieldDeclaration.isStatic() && fieldDeclaration.isFinal()) {
            visitor = STATIC_FINAL_VARIABLE_DECLARATOR_VISITOR;
        }
        variableDeclarator.accept(visitor, fieldDeclaration);
        super.visit(variableDeclarator, fieldDeclaration);
    }
}
