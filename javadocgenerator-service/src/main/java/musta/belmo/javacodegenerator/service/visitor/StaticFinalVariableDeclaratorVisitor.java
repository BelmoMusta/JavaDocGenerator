package musta.belmo.javacodegenerator.service.visitor;

import com.github.javaparser.ast.body.FieldDeclaration;
import com.github.javaparser.ast.body.VariableDeclarator;
import com.github.javaparser.ast.expr.SimpleName;
import com.github.javaparser.javadoc.Javadoc;
import com.github.javaparser.javadoc.description.JavadocDescription;
import com.github.javaparser.javadoc.description.JavadocSnippet;
import musta.belmo.javacodegenerator.service.GeneratorConstantes;
import musta.belmo.javacodegenerator.service.PropertiesHandler;

public class StaticFinalVariableDeclaratorVisitor extends AbstractCommonVisitor<FieldDeclaration> {
    private static final StaticFinalVariableDeclaratorVisitor INSTANCE = new StaticFinalVariableDeclaratorVisitor();

    public static StaticFinalVariableDeclaratorVisitor getInstance() {
        return INSTANCE;
    }

    @Override
    public void visit(VariableDeclarator variableDeclarator, FieldDeclaration fieldDeclaration) {
        Javadoc javadoc = fieldDeclaration.getJavadoc().orElse(new Javadoc(new JavadocDescription()));
        JavadocDescription javadocDescription = javadoc.getDescription();
        JavadocSnippet javadocSnippet = getJavadocSnippetForStaticFields(variableDeclarator);
        if (!javadocDescription.getElements().isEmpty()) {
            JavadocSnippet newLineSnippet = new JavadocSnippet("\n");
            javadocDescription.addElement(newLineSnippet);
        }
        javadocDescription.addElement(javadocSnippet);
        fieldDeclaration.setJavadocComment(javadoc);
        super.visit(variableDeclarator, fieldDeclaration);
    }

    protected  JavadocSnippet getJavadocSnippetForStaticFields(VariableDeclarator variable) {
        final SimpleName fieldName = variable.getName();
        final String javaDocText = PropertiesHandler.readFromProperties(GeneratorConstantes.CONSTANT_COMMENT);
        final String linkSnippet = createLinkSnippet(variable.getType());
        return new JavadocSnippet(String.format(javaDocText, fieldName, linkSnippet));
    }

}
