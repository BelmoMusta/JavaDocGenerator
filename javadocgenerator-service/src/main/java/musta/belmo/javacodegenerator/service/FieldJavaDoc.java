package musta.belmo.javacodegenerator.service;

import com.github.javaparser.ast.body.FieldDeclaration;
import com.github.javaparser.ast.body.VariableDeclarator;
import com.github.javaparser.ast.expr.Expression;
import com.github.javaparser.ast.expr.SimpleName;
import com.github.javaparser.ast.type.Type;
import com.github.javaparser.javadoc.Javadoc;
import com.github.javaparser.javadoc.description.JavadocDescription;
import com.github.javaparser.javadoc.description.JavadocSnippet;
import org.apache.commons.lang3.StringUtils;

import java.util.Optional;

/**
 * TODO: Complete the description of this class
 *
 * @author default author
 * @version 0.0.0
 * @since 0.0.0.SNAPSHOT
 */
public class FieldJavaDoc {

    /**
     * Generate field java doc
     *
     * @param fieldDeclaration {@link FieldDeclaration}
     * @param text             {@link String}
     */
    private static void generateFieldJavaDoc(FieldDeclaration fieldDeclaration, String text) {
        if (fieldDeclaration.getVariables().isNonEmpty()) {
            JavadocDescription javadocDescription = new JavadocDescription();
            Javadoc javadoc = new Javadoc(javadocDescription);
            String javaDocText;
            JavadocSnippet javadocSnippet;
            VariableDeclarator variableDeclarator = fieldDeclaration.getVariables().get(0);
            SimpleName fieldName = variableDeclarator.getName();
            VariableDeclarator variable = fieldDeclaration.getVariable(0);
            Type type = variable.getType();
            if (fieldDeclaration.isStatic() && fieldDeclaration.isFinal()
                    && variable.getInitializer().isPresent()) {
                javadocSnippet = getJavadocSnippetForStaticFields(text, fieldName, variable, type);
            } else {
                javaDocText = PropertiesHandler.readFromProperties(GeneratorConstantes.FIELD_COMMENT);
                javadocSnippet = new JavadocSnippet(String.format(javaDocText, fieldName));
            }
            javadocDescription.addElement(javadocSnippet);
            fieldDeclaration.setJavadocComment(javadoc);
        }
    }

    private static JavadocSnippet getJavadocSnippetForStaticFields(String text, SimpleName fieldName, VariableDeclarator variable, Type type) {
        String javaDocText;
        String valueText;
        Object assignedValue;
        JavadocSnippet javadocSnippet;
        javaDocText = text;
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

    /**
     * Generate field java doc
     *
     * @param fieldDeclaration {@link FieldDeclaration}
     */
    public static void generateFieldJavaDoc(FieldDeclaration fieldDeclaration) {
        generateFieldJavaDoc(fieldDeclaration, PropertiesHandler.readFromProperties(GeneratorConstantes.CONSTANT_COMMENT));
    }
}
