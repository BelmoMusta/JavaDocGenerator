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

    protected static JavadocSnippet getJavadocSnippetForStaticFields(VariableDeclarator variable) {

        Type type = variable.getType();
        SimpleName fieldName = variable.getName();
        String javaDocText;
        String valueText;
        Object assignedValue;
        JavadocSnippet javadocSnippet;
        javaDocText = PropertiesHandler.readFromProperties(GeneratorConstantes.CONSTANT_COMMENT);
        String typeText = GeneratorConstantes.SINGLE_STRING_FORMAT;
        if (!type.isPrimitiveType()) {
            typeText = PropertiesHandler.readFromProperties(GeneratorConstantes.LINK_COMMENT);
        }
        if (type.isPrimitiveType() || GeneratorConstantes.STRING.equals(type.asString())) {
            valueText = PropertiesHandler.readFromProperties(GeneratorConstantes.FIELD_VALUE_COMMENT);
            assignedValue = fieldName;
        } else {
            assignedValue = StringUtils.EMPTY;
            Optional<Expression> initializer = variable.getInitializer();
            if (initializer.isPresent()) {
                assignedValue = initializer.get().toString();
            }
            valueText = GeneratorConstantes.SINGLE_STRING_FORMAT;
        }
        javadocSnippet = new JavadocSnippet(String.format(javaDocText, fieldName, String.format(typeText, type.asString()), String.format(valueText, assignedValue)));
        return javadocSnippet;
    }

}
