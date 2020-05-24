package musta.belmo.javacodegenerator.service.visitor;

import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.body.Parameter;
import com.github.javaparser.javadoc.Javadoc;

public class ParameterVisitor extends AbstractCommonVisitor<MethodDeclaration> {
    private static final ParameterVisitor INSTANCE = new ParameterVisitor();

    public static ParameterVisitor getInstance() {
        return INSTANCE;
    }

    @Override
    public void visit(Parameter parameter, MethodDeclaration methodDeclaration) {
        final Javadoc javadoc = getOrCreateJavadoc(methodDeclaration);
        addBlockTag(parameter, javadoc);
        methodDeclaration.setJavadocComment(javadoc);
        super.visit(parameter, methodDeclaration);
    }
}
