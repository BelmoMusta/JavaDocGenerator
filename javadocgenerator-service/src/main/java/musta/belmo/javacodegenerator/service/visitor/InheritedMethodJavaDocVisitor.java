package musta.belmo.javacodegenerator.service.visitor;

import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.javadoc.Javadoc;
import com.github.javaparser.javadoc.description.JavadocSnippet;
import musta.belmo.javacodegenerator.service.GeneratorConstantes;
import musta.belmo.javacodegenerator.service.PropertiesHandler;

public class InheritedMethodJavaDocVisitor extends AbstractCommonVisitor<MethodDeclaration> {
    private static final InheritedMethodJavaDocVisitor INSTANCE = new InheritedMethodJavaDocVisitor();

    public static InheritedMethodJavaDocVisitor getInstance() {
        return INSTANCE;
    }

    @Override
    public void visit(MethodDeclaration src, MethodDeclaration methodDeclaration) {
        Javadoc javadoc = getOrCreateJavadoc(methodDeclaration);
        JavadocSnippet inheritDocSnippet = new JavadocSnippet(PropertiesHandler.readFromProperties(GeneratorConstantes.INHERIT_DOC));
        javadoc.getDescription().addElement(inheritDocSnippet);
        methodDeclaration.setJavadocComment(javadoc);
        super.visit(src, methodDeclaration);
    }
}
