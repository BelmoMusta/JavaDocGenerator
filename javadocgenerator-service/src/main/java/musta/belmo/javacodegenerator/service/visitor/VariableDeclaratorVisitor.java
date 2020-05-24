package musta.belmo.javacodegenerator.service.visitor;

import com.github.javaparser.ast.body.FieldDeclaration;
import com.github.javaparser.ast.body.VariableDeclarator;
import com.github.javaparser.ast.expr.Expression;
import com.github.javaparser.ast.expr.SimpleName;
import com.github.javaparser.ast.type.Type;
import com.github.javaparser.javadoc.Javadoc;
import com.github.javaparser.javadoc.description.JavadocDescription;
import com.github.javaparser.javadoc.description.JavadocSnippet;
import musta.belmo.javacodegenerator.service.GeneratorConstantes;
import musta.belmo.javacodegenerator.service.PropertiesHandler;
import org.apache.commons.lang3.StringUtils;

import java.util.Optional;

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
        JavadocSnippet javadocSnippet = new JavadocSnippet(String.format(javaDocText, fieldName));
        if (!javadocDescription.getElements().isEmpty()) {
            JavadocSnippet newLineSnippet = new JavadocSnippet("\n");
            javadocDescription.addElement(newLineSnippet);
        }

        javadocDescription.addElement(javadocSnippet);
        fieldDeclaration.setJavadocComment(javadoc);
        super.visit(variableDeclarator, fieldDeclaration);
    }


}
