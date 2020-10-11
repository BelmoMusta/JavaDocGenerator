package musta.belmo.javacodegenerator.service.visitor;

import com.github.javaparser.ast.body.FieldDeclaration;
import com.github.javaparser.ast.body.VariableDeclarator;
import com.github.javaparser.ast.expr.SimpleName;
import com.github.javaparser.javadoc.Javadoc;
import com.github.javaparser.javadoc.description.JavadocDescription;
import com.github.javaparser.javadoc.description.JavadocSnippet;
import musta.belmo.javacodegenerator.service.GeneratorConstantes;
import musta.belmo.javacodegenerator.service.PropertiesHandler;

public class VariableDeclaratorVisitor extends AbstractCommonVisitor<FieldDeclaration> {
    private static final VariableDeclaratorVisitor INSTANCE = new VariableDeclaratorVisitor();

    public static VariableDeclaratorVisitor getInstance() {
        return INSTANCE;
    }

    @Override
    public void visit(VariableDeclarator variableDeclarator, FieldDeclaration fieldDeclaration) {
        Javadoc javadoc = fieldDeclaration.getJavadoc().orElse(new Javadoc(new JavadocDescription()));
        JavadocDescription javadocDescription = javadoc.getDescription();
        SimpleName fieldName = variableDeclarator.getName();
        String javaDocText = PropertiesHandler.readFromProperties(GeneratorConstantes.FIELD_COMMENT);
        String s = createLinkSnippet(variableDeclarator.getType());
        JavadocSnippet javadocSnippet = new JavadocSnippet(String.format(javaDocText, fieldName, s));
        if (!javadocDescription.getElements().isEmpty()) {
            JavadocSnippet newLineSnippet = new JavadocSnippet("\n");
            javadocDescription.addElement(newLineSnippet);
        }

        javadocDescription.addElement(javadocSnippet);
        fieldDeclaration.setJavadocComment(javadoc);
        super.visit(variableDeclarator, fieldDeclaration);
    }


}
