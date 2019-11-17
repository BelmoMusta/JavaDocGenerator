package musta.belmo.javacodegenerator.service;

import com.github.javaparser.ast.NodeList;
import com.github.javaparser.ast.body.ConstructorDeclaration;
import com.github.javaparser.ast.body.Parameter;
import com.github.javaparser.ast.body.TypeDeclaration;
import com.github.javaparser.ast.comments.LineComment;
import com.github.javaparser.ast.stmt.BlockStmt;
import com.github.javaparser.javadoc.Javadoc;
import com.github.javaparser.javadoc.JavadocBlockTag;
import com.github.javaparser.javadoc.description.JavadocDescription;
import com.github.javaparser.javadoc.description.JavadocSnippet;
import musta.belmo.javacodegenerator.util.CodeUtils;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * TODO: Complete the description of this class
 *
 * @author default author
 * @since 0.0.0.SNAPSHOT
 * @version 0.0.0
 */
public class JavaTypeJavaDoc {

    /**
     * Generate constructor java doc
     *
     * @param constructorDeclaration {@link ConstructorDeclaration}
     */
    static void generateConstructorJavaDoc(ConstructorDeclaration constructorDeclaration) {
        NodeList<Parameter> constructParams = constructorDeclaration.getParameters();
        String leadingComment;
        BlockStmt body = constructorDeclaration.getBody();
        if (constructParams.isEmpty() && body.getOrphanComments().isEmpty() && body.getStatements().isEmpty()) {
            leadingComment = PropertiesHandler.readFromProperties(GeneratorConstantes.DEFAULT_CONSTR_COMMENT);
            body.addOrphanComment(new LineComment(leadingComment));
        } else {
            leadingComment = String.format(PropertiesHandler.readFromProperties(GeneratorConstantes.CONSTR_COMMENT), constructorDeclaration.getName().asString());
        }
        generateConstructorJavaDoc(constructorDeclaration, leadingComment);
    }

    /**
     * Generate java doc for type declaration
     *
     * @param typeDeclaration {@link TypeDeclaration}
     */
    @SuppressWarnings("unchecked")
    static void generateJavaDocForTypeDeclaration(TypeDeclaration typeDeclaration) {
        if (typeDeclaration.hasJavaDocComment()) {
            Optional<Javadoc> optionalJavaDoc = typeDeclaration.getJavadoc();
            optionalJavaDoc.ifPresent(javadoc -> {
                List<JavadocBlockTag.Type> blockTypes = javadoc.getBlockTags().stream().map(JavadocBlockTag::getType).collect(Collectors.toList());
                if (!blockTypes.contains(JavadocBlockTag.Type.SINCE)) {
                    CodeUtils.addBlockTagToClassJavaDoc(JavadocBlockTag.Type.SINCE, javadoc, PropertiesHandler.readFromProperties(GeneratorConstantes.SINCE_VERSION));
                }
                if (!blockTypes.contains(JavadocBlockTag.Type.AUTHOR)) {
                    CodeUtils.addBlockTagToClassJavaDoc(JavadocBlockTag.Type.AUTHOR, javadoc, PropertiesHandler.readFromProperties(GeneratorConstantes.AUTHOR));
                }
                if (!blockTypes.contains(JavadocBlockTag.Type.VERSION)) {
                    CodeUtils.addBlockTagToClassJavaDoc(JavadocBlockTag.Type.VERSION, javadoc, PropertiesHandler.readFromProperties(GeneratorConstantes.VERSION));
                }
                typeDeclaration.setJavadocComment(javadoc);
            });
        } else {
            JavadocDescription javadocDescription = new JavadocDescription();
            Javadoc javadoc = new Javadoc(javadocDescription);
            String text = PropertiesHandler.readFromProperties(GeneratorConstantes.TODO_CLASS_TEXT);
            if (typeDeclaration.isEnumDeclaration()) {
                text = PropertiesHandler.readFromProperties(GeneratorConstantes.TODO_ENUM_TEXT);
            }
            JavadocSnippet element = new JavadocSnippet(text);
            javadocDescription.addElement(element);
            CodeUtils.addBlockTagToClassJavaDoc(JavadocBlockTag.Type.AUTHOR, javadoc, PropertiesHandler.readFromProperties(GeneratorConstantes.AUTHOR));
            CodeUtils.addBlockTagToClassJavaDoc(JavadocBlockTag.Type.SINCE, javadoc, PropertiesHandler.readFromProperties(GeneratorConstantes.SINCE_VERSION));
            CodeUtils.addBlockTagToClassJavaDoc(JavadocBlockTag.Type.VERSION, javadoc, PropertiesHandler.readFromProperties(GeneratorConstantes.VERSION));
            typeDeclaration.setJavadocComment(javadoc);
        }
    }

    /**
     * Generate constructor java doc
     *
     * @param constructorDeclaration {@link ConstructorDeclaration}
     * @param text {@link String}
     */
    public static void generateConstructorJavaDoc(ConstructorDeclaration constructorDeclaration, String text) {
        JavadocDescription javadocDescription = new JavadocDescription();
        Javadoc javadoc = new Javadoc(javadocDescription);
        NodeList<Parameter> constructParams = constructorDeclaration.getParameters();
        JavadocSnippet element = new JavadocSnippet(text);
        javadocDescription.addElement(element);
        if (!constructorDeclaration.getJavadoc().isPresent()) {
            CodeUtils.addParamsToJavaDoc(constructParams, javadoc);
            MethodJavaDoc.addExceptionsToJavaDoc(constructorDeclaration.getThrownExceptions(), javadoc);
            constructorDeclaration.setJavadocComment(javadoc);
        }
    }
}
