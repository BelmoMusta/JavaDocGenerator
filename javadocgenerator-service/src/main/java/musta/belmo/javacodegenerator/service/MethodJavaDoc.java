package musta.belmo.javacodegenerator.service;

import com.github.javaparser.ast.NodeList;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.body.Parameter;
import com.github.javaparser.ast.expr.SimpleName;
import com.github.javaparser.ast.type.Type;
import com.github.javaparser.javadoc.Javadoc;
import com.github.javaparser.javadoc.JavadocBlockTag;
import com.github.javaparser.javadoc.description.JavadocDescription;
import com.github.javaparser.javadoc.description.JavadocSnippet;
import musta.belmo.javacodegenerator.util.CodeUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * TODO: Complete the description of this class
 *
 * @author default author
 * @version 0.0.0
 * @since 0.0.0.SNAPSHOT
 */
public class MethodJavaDoc {

    /**
     * Generate method java doc
     *
     * @param methodDeclaration {@link MethodDeclaration}
     */
    static void generateMethodJavaDoc(MethodDeclaration methodDeclaration) {
        JavadocDescription javadocDescription = new JavadocDescription();
        Javadoc javadoc = new Javadoc(javadocDescription);
        String methodName = methodDeclaration.getName().asString();
        boolean isGetter = CodeUtils.isGetter(methodDeclaration);
        boolean isIs = CodeUtils.isIs(methodDeclaration);
        JavadocSnippet inheritDocSnippet = new JavadocSnippet(PropertiesHandler.readFromProperties(GeneratorConstantes.INHERIT_DOC));
        String methodReturnType = methodDeclaration.getType().asString();
        NodeList<Parameter> parameters = methodDeclaration.getParameters();
        JavadocBlockTag.Type returnType = JavadocBlockTag.Type.PARAM;
        if (!methodDeclaration.hasJavaDocComment()) {
            if (methodDeclaration.isAnnotationPresent(Override.class)) {
                javadocDescription.addElement(inheritDocSnippet);
                methodDeclaration.setJavadocComment(javadoc);
            } else {
                handleMethodWithReturnTypes(methodDeclaration, javadocDescription, javadoc, parameters, returnType);
            }
        } else if (methodDeclaration.isAnnotationPresent(Override.class)) {
            setupOverriddenMethods(methodDeclaration);
        } else {
            Javadoc oldJavaDoc = javadoc;
            Optional<Javadoc> optionalJavaDoc = methodDeclaration.getJavadoc();
            if (optionalJavaDoc.isPresent()) {
                oldJavaDoc = optionalJavaDoc.get();
            }
            paramTypesJavaDoc(methodDeclaration,
                     oldJavaDoc);
            ExceptionJavaDoc.addExceptionsToJavaDoc(methodDeclaration.getThrownExceptions(), oldJavaDoc);
            addReturnTagToJavadoc(methodDeclaration, methodName, isGetter, isIs, methodReturnType, oldJavaDoc);
            methodDeclaration.setJavadocComment(oldJavaDoc);
        }
    }

    private static void paramTypesJavaDoc(MethodDeclaration methodDeclaration,
                                          Javadoc oldJavaDoc) {

        final String setterCommentFormat = PropertiesHandler.readFromProperties(GeneratorConstantes.SETTER_COMMENT);
        NodeList<Parameter> parameters = methodDeclaration.getParameters();
        String paramFormat = "%s {@link %s}";
        boolean isSetter = CodeUtils.isSetter(methodDeclaration);
        String methodName = methodDeclaration.getNameAsString();
        List<String> paramTypes = oldJavaDoc.getBlockTags().stream().filter(block -> block.getType().equals(JavadocBlockTag.Type.PARAM)).filter(block -> block.getName().isPresent()).map(block -> block.getName().get()).collect(Collectors.toList());
        for (Parameter parameter : parameters) {
            if (!paramTypes.contains(parameter.getNameAsString())) {
                Type paramType = parameter.getType();
                SimpleName paramName = parameter.getName();
                if (paramType.isPrimitiveType()) {
                    paramFormat = "%s %s ";
                }
                if (isSetter) {
                    oldJavaDoc.addBlockTag(new FormattedJavadocBlockTag(JavadocBlockTag.Type.PARAM, String.format(setterCommentFormat, paramName.asString(), CodeUtils.toLowerCaseFirstLetter(CodeUtils.getMethodeConcreteName(methodName, 3)))));
                } else {
                    JavadocBlockTag blockTag = new FormattedJavadocBlockTag(JavadocBlockTag.Type.PARAM, String.format(paramFormat, paramName.asString(), paramType.asString()));
                    oldJavaDoc.addBlockTag(blockTag);
                }
            }
        }
    }

    private static void handleMethodWithReturnTypes(MethodDeclaration methodDeclaration,
                                                    JavadocDescription javadocDescription,
                                                    Javadoc javadoc,
                                                    NodeList<Parameter> parameters,
                                                    JavadocBlockTag.Type returnType) {
        JavadocSnippet element;
        final String setterCommentFormat = PropertiesHandler.readFromProperties(GeneratorConstantes.SETTER_COMMENT);
        String paramFormat = "%s {@link %s}";
        boolean isSetter = CodeUtils.isSetter(methodDeclaration);
        boolean isGetter = CodeUtils.isGetter(methodDeclaration);
        boolean isIs = CodeUtils.isIs(methodDeclaration);

        String methodName = methodDeclaration.getNameAsString();

        if (isSetter || isGetter || isIs) {
            element = new JavadocSnippet("");
        } else if (CodeUtils.isCamelCase(methodName)) {
            element = new JavadocSnippet(StringUtils.capitalize(StringUtils.lowerCase(CodeUtils.unCamelCase(methodName, " "))));
        } else {
            element = new JavadocSnippet(PropertiesHandler.readFromProperties(GeneratorConstantes.TODO_METHOD_TEXT));
        }
        javadocDescription.addElement(element);
        for (Parameter parameter : parameters) {

            if (parameter.getType().isPrimitiveType()) {
                paramFormat = "%s %s ";
            }
            String paramName = parameter.getName().asString();
            String content;
            if (isSetter) {
                content = String.format(setterCommentFormat, paramName,
                        CodeUtils.toLowerCaseFirstLetter(CodeUtils.getMethodeConcreteName(methodName, 3)));
            } else {
                content = String.format(paramFormat, paramName,
                        parameter.getType().asString());
            }
            javadoc.addBlockTag(new FormattedJavadocBlockTag(returnType, content));
        }
        if (!methodDeclaration.getType().isVoidType()) {
            handleVoidType(methodDeclaration, javadoc);
        }
        ExceptionJavaDoc.addExceptionsToJavaDoc(methodDeclaration.getThrownExceptions(), javadoc);
        methodDeclaration.setJavadocComment(javadoc);
    }

    private static void handleVoidType(MethodDeclaration methodDeclaration, Javadoc javadoc) {
        final String attributeCommentFormat = PropertiesHandler.readFromProperties(GeneratorConstantes.ATTRIBUT_COMMENT_FORMAT);
        final String content;
        String methodeConcreteName;
        if (CodeUtils.isGetter(methodDeclaration)) {
            methodeConcreteName = CodeUtils.getMethodeConcreteName(methodDeclaration.getNameAsString(), 3);
        } else if (CodeUtils.isIs(methodDeclaration)) {
            methodeConcreteName = CodeUtils.getMethodeConcreteName(methodDeclaration.getNameAsString(), 2);
        } else {
            methodeConcreteName = null;
        }
        if (methodeConcreteName != null) {
            content = String.format(attributeCommentFormat, CodeUtils.toLowerCaseFirstLetter(methodeConcreteName));
        } else {
            content = methodDeclaration.getType().asString();
        }
        javadoc.addBlockTag(new FormattedJavadocBlockTag(JavadocBlockTag.Type.RETURN, content));
    }

    /**
     * Add return tag to javadoc
     *
     * @param methodDeclaration {@link MethodDeclaration}
     * @param methodName        {@link String}
     * @param isGetter          boolean
     * @param isIs              boolean
     * @param methodReturnType  String
     * @param oldJavaDoc        Javadoc
     */
    private static void addReturnTagToJavadoc(MethodDeclaration methodDeclaration, String methodName, boolean isGetter, boolean isIs, String methodReturnType, Javadoc oldJavaDoc) {
        boolean returnExists = oldJavaDoc.getBlockTags().stream().anyMatch(block -> block.getType().equals(JavadocBlockTag.Type.RETURN));
        if (!methodDeclaration.getType().isVoidType() && !returnExists) {
            int nbSubstringChars = 0;
            String returnType = "";
            if (isGetter) {
                nbSubstringChars = 3;
            } else if (isIs) {
                nbSubstringChars = 2;
            } else {
                returnType = methodReturnType;
            }
            if (nbSubstringChars != 0) {
                returnType = String.format(PropertiesHandler.readFromProperties(GeneratorConstantes.ATTRIBUT_COMMENT_FORMAT), CodeUtils.toLowerCaseFirstLetter(CodeUtils.getMethodeConcreteName(methodName, nbSubstringChars)));
            }
            oldJavaDoc.addBlockTag(new FormattedJavadocBlockTag(JavadocBlockTag.Type.RETURN, returnType));
        }
    }

    /**
     * @param methodDeclaration Value to be assigned to the {@link #upOverriddenMethods} attribute.
     */
    private static void setupOverriddenMethods(MethodDeclaration methodDeclaration) {
        JavadocDescription javadocDescription = new JavadocDescription();
        Javadoc javadoc = new Javadoc(javadocDescription);
        JavadocSnippet inheritDocSnippet = new JavadocSnippet(PropertiesHandler.readFromProperties(GeneratorConstantes.INHERIT_DOC));
        methodDeclaration.removeJavaDocComment();
        javadocDescription.addElement(inheritDocSnippet);
        methodDeclaration.setJavadocComment(javadoc);
    }
}
