package musta.belmo.javacodegenerator.service;

import com.github.javaparser.ast.NodeList;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.body.Parameter;
import com.github.javaparser.ast.expr.SimpleName;
import com.github.javaparser.ast.type.ReferenceType;
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
 * @since 0.0.0.SNAPSHOT
 * @version 0.0.0
 */
public class MethodJavaDoc {

    /**
     * Add exceptions to java doc
     *
     * @param thrownExceptions {@link NodeList}
     * @param javadoc {@link Javadoc}
     */
    public static void addExceptionsToJavaDoc(NodeList<ReferenceType> thrownExceptions, Javadoc javadoc) {
        for (ReferenceType thrownException : thrownExceptions) {
            boolean throwsExist = javadoc.getBlockTags().stream().anyMatch(block -> block.getType().equals(JavadocBlockTag.Type.THROWS) && block.getContent().getElements().get(0).toText().startsWith(thrownException.toString()));
            if (!throwsExist) {
                FormattedJavadocBlockTag javadocBlockTag = new FormattedJavadocBlockTag(JavadocBlockTag.Type.THROWS, String.format(PropertiesHandler.readFromProperties(GeneratorConstantes.EXCEPTION_COMMENT), thrownException.asReferenceType()));
                javadoc.addBlockTag(javadocBlockTag);
            }
        }
    }

    /**
     * Generate method java doc
     *
     * @param methodDeclaration {@link MethodDeclaration}
     */
    static void generateMethodJavaDoc(MethodDeclaration methodDeclaration) {
        JavadocDescription javadocDescription = new JavadocDescription();
        Javadoc javadoc = new Javadoc(javadocDescription);
        JavadocSnippet element;
        String methodName = methodDeclaration.getName().asString();
        boolean isSetter = CodeUtils.isSetter(methodDeclaration);
        boolean isGetter = CodeUtils.isGetter(methodDeclaration);
        boolean isIs = CodeUtils.isIs(methodDeclaration);
        String paramFormat = "%s {@link %s}";
        JavadocSnippet inheritDocSnippet = new JavadocSnippet(PropertiesHandler.readFromProperties(GeneratorConstantes.INHERIT_DOC));
        String methodReturnType = methodDeclaration.getType().asString();
        NodeList<Parameter> parameters = methodDeclaration.getParameters();
        if (!methodDeclaration.hasJavaDocComment()) {
            if (methodDeclaration.isAnnotationPresent(Override.class)) {
                javadocDescription.addElement(inheritDocSnippet);
                methodDeclaration.setJavadocComment(javadoc);
            } else {
                if (isSetter || isGetter || isIs) {
                    element = new JavadocSnippet("");
                } else if (CodeUtils.isCamelCase(methodName)) {
                    element = new JavadocSnippet(StringUtils.capitalize(StringUtils.lowerCase(CodeUtils.unCamelCase(methodName, " "))));
                } else {
                    element = new JavadocSnippet(PropertiesHandler.readFromProperties(GeneratorConstantes.TODO_METHOD_TEXT));
                }
                javadocDescription.addElement(element);
                for (Parameter parameter : parameters) {
                    JavadocBlockTag blockTag;
                    if (parameter.getType().isPrimitiveType()) {
                        paramFormat = "%s %s ";
                    }
                    String paramName = parameter.getName().asString();
                    if (isSetter) {
                        blockTag = new FormattedJavadocBlockTag(JavadocBlockTag.Type.PARAM, String.format(PropertiesHandler.readFromProperties(GeneratorConstantes.SETTER_COMMENT), paramName, CodeUtils.toLowerCaseFirstLetter(CodeUtils.getMethodeConcreteName(methodName, 3))));
                    } else {
                        blockTag = new FormattedJavadocBlockTag(JavadocBlockTag.Type.PARAM, String.format(paramFormat, paramName, parameter.getType().asString()));
                    }
                    javadoc.addBlockTag(blockTag);
                }
                if (!methodDeclaration.getType().isVoidType()) {
                    JavadocBlockTag javadocBlockTag;
                    if (isGetter) {
                        javadocBlockTag = new FormattedJavadocBlockTag(JavadocBlockTag.Type.RETURN, String.format(PropertiesHandler.readFromProperties(GeneratorConstantes.ATTRIBUT_COMMENT_FORMAT), CodeUtils.toLowerCaseFirstLetter(CodeUtils.getMethodeConcreteName(methodName, 3))));
                    } else if (isIs) {
                        javadocBlockTag = new FormattedJavadocBlockTag(JavadocBlockTag.Type.RETURN, String.format(PropertiesHandler.readFromProperties(GeneratorConstantes.ATTRIBUT_COMMENT_FORMAT), CodeUtils.toLowerCaseFirstLetter(CodeUtils.getMethodeConcreteName(methodName, 2))));
                    } else {
                        javadocBlockTag = new FormattedJavadocBlockTag(JavadocBlockTag.Type.RETURN, methodReturnType);
                    }
                    javadoc.addBlockTag(javadocBlockTag);
                }
                addExceptionsToJavaDoc(methodDeclaration.getThrownExceptions(), javadoc);
                methodDeclaration.setJavadocComment(javadoc);
            }
        } else if (methodDeclaration.isAnnotationPresent(Override.class)) {
            AbstractJavaDocService.setupOverriddenMethods(methodDeclaration);
        } else {
            Javadoc oldJavaDoc = javadoc;
            Optional<Javadoc> optionalJavaDoc = methodDeclaration.getJavadoc();
            if (optionalJavaDoc.isPresent()) {
                oldJavaDoc = optionalJavaDoc.get();
            }
            List<String> paramTypes = oldJavaDoc.getBlockTags().stream().filter(block -> block.getType().equals(JavadocBlockTag.Type.PARAM)).filter(block -> block.getName().isPresent()).map(block -> block.getName().get()).collect(Collectors.toList());
            for (Parameter parameter : parameters) {
                if (!paramTypes.contains(parameter.getNameAsString())) {
                    Type paramType = parameter.getType();
                    SimpleName paramName = parameter.getName();
                    if (paramType.isPrimitiveType()) {
                        paramFormat = "%s %s ";
                    }
                    if (isSetter) {
                        oldJavaDoc.addBlockTag(new FormattedJavadocBlockTag(JavadocBlockTag.Type.PARAM, String.format(PropertiesHandler.readFromProperties(GeneratorConstantes.SETTER_COMMENT), paramName.asString(), CodeUtils.toLowerCaseFirstLetter(CodeUtils.getMethodeConcreteName(methodName, 3)))));
                    } else {
                        JavadocBlockTag blockTag = new FormattedJavadocBlockTag(JavadocBlockTag.Type.PARAM, String.format(paramFormat, paramName.asString(), paramType.asString()));
                        oldJavaDoc.addBlockTag(blockTag);
                    }
                }
            }
            addExceptionsToJavaDoc(methodDeclaration.getThrownExceptions(), oldJavaDoc);
            addReturnTagToJavadoc(methodDeclaration, methodName, isGetter, isIs, methodReturnType, oldJavaDoc);
            methodDeclaration.setJavadocComment(oldJavaDoc);
        }
    }

    /**
     * Add return tag to javadoc
     *
     * @param methodDeclaration {@link MethodDeclaration}
     * @param methodName {@link String}
     * @param isGetter boolean
     * @param isIs boolean
     * @param methodReturnType String
     * @param oldJavaDoc Javadoc
     */
    private static void addReturnTagToJavadoc(MethodDeclaration methodDeclaration, String methodName, boolean isGetter, boolean isIs, String methodReturnType, Javadoc oldJavaDoc) {
        boolean returnExists = oldJavaDoc.getBlockTags().stream().anyMatch(block -> block.getType().equals(JavadocBlockTag.Type.RETURN));
        if (!methodDeclaration.getType().isVoidType() && !returnExists) {
            int nbSubstringChars = 0;
            String returnType = methodReturnType;
            if (isGetter) {
                nbSubstringChars = 3;
            } else if (isIs) {
                nbSubstringChars = 2;
            }
            if (nbSubstringChars != 0) {
                returnType = String.format(PropertiesHandler.readFromProperties(GeneratorConstantes.ATTRIBUT_COMMENT_FORMAT), CodeUtils.toLowerCaseFirstLetter(CodeUtils.getMethodeConcreteName(methodName, nbSubstringChars)));
            }
            oldJavaDoc.addBlockTag(new FormattedJavadocBlockTag(JavadocBlockTag.Type.RETURN, returnType));
        }
    }
}
