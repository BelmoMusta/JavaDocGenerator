package musta.belmo.javacodegenerator.service.visitor;

import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.stmt.ReturnStmt;
import com.github.javaparser.javadoc.Javadoc;
import com.github.javaparser.javadoc.JavadocBlockTag;

public class ReturnStmtVisitor extends AbstractCommonVisitor<MethodDeclaration> {
    private static final ReturnStmtVisitor INSTANCE = new ReturnStmtVisitor();

    public static ReturnStmtVisitor getInstance() {
        return INSTANCE;
    }

    @Override
    public void visit(ReturnStmt returnStmt, MethodDeclaration methodDeclaration) {
        final Javadoc javadoc = getOrCreateJavadoc(methodDeclaration);
        boolean returnExists = javadoc.getBlockTags().stream()
                .anyMatch(bloc -> bloc.getType().equals(JavadocBlockTag.Type.RETURN));

        if (!returnExists) {
            addBlockTag(JavadocBlockTag.Type.RETURN,
                    methodDeclaration.getType().asString(),
                    javadoc);
        }
        methodDeclaration.setJavadocComment(javadoc);
        super.visit(returnStmt, methodDeclaration);
    }
}
