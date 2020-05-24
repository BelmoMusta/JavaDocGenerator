package musta.belmo.javacodegenerator.service.visitor;

import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.body.Parameter;
import com.github.javaparser.ast.stmt.ReturnStmt;
import com.github.javaparser.ast.type.TypeParameter;

public class MethodJavaDocVisitor extends AbstractCommonVisitor<MethodDeclaration> {
    private static final MethodJavaDocVisitor INSTANCE = new MethodJavaDocVisitor();
    private static final TypeParameterVisitor TYPE_PARAMETER_VISITOR = TypeParameterVisitor.getInstance();
    private static final ReturnStmtVisitor RETURN_STMT_VISITOR = ReturnStmtVisitor.getInstance();
    private static final MethodDeclarationVisitor METHOD_DECLARATION_VISITOR = MethodDeclarationVisitor.getInstance();
    private static final ParameterVisitor PARAMETER_VISITOR = ParameterVisitor.getInstance();

    public static MethodJavaDocVisitor getInstance() {
        return INSTANCE;
    }

    @Override
    public void visit(Parameter parameter, MethodDeclaration methodDeclaration) {
        parameter.accept(PARAMETER_VISITOR, methodDeclaration);
        super.visit(parameter, methodDeclaration);
    }

    @Override
    public void visit(ReturnStmt returnStmt, MethodDeclaration methodDeclaration) {
        returnStmt.accept(RETURN_STMT_VISITOR, methodDeclaration);
        super.visit(returnStmt, methodDeclaration);
    }

    @Override
    public void visit(TypeParameter typeParameter, MethodDeclaration destinationMethod) {
        typeParameter.accept(TYPE_PARAMETER_VISITOR, destinationMethod);
        super.visit(typeParameter, destinationMethod);
    }

    @Override
    public void visit(MethodDeclaration src, MethodDeclaration methodDeclaration) {
        src.accept(METHOD_DECLARATION_VISITOR, methodDeclaration);
        super.visit(src, methodDeclaration);
    }
}
