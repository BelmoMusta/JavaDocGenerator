package musta.belmo.javacodegenerator.service.visitor;

import com.github.javaparser.ast.NodeList;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.body.Parameter;
import com.github.javaparser.ast.type.ReferenceType;
import com.github.javaparser.ast.visitor.VoidVisitorAdapter;
import com.github.javaparser.javadoc.Javadoc;
import com.github.javaparser.javadoc.JavadocBlockTag;
import com.github.javaparser.javadoc.JavadocBlockTag.Type;
import com.github.javaparser.javadoc.description.JavadocDescription;
import com.github.javaparser.javadoc.description.JavadocDescriptionElement;
import com.github.javaparser.javadoc.description.JavadocSnippet;

import java.io.IOException;
import java.util.Comparator;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public abstract class AbstractCommonVisitor<T> extends VoidVisitorAdapter<T> {

    protected Javadoc getOrCreateJavadoc(MethodDeclaration destinationMethod) {
        final Optional<Javadoc> optionalJavadoc = destinationMethod.getJavadoc();
        return optionalJavadoc.orElseGet(() -> new Javadoc(new JavadocDescription()));
    }

    protected Javadoc getOrCreateJavadoc(ClassOrInterfaceDeclaration destinationMethod) {
        final Optional<Javadoc> optionalJavadoc = destinationMethod.getJavadoc();
        return optionalJavadoc.orElseGet(() -> new Javadoc(new JavadocDescription()));
    }

    protected void addThrowBlockTags(MethodDeclaration src, MethodDeclaration methodDeclaration) {
        final NodeList<ReferenceType> thrownExceptions = src.getThrownExceptions();
        if (thrownExceptions.isNonEmpty()) {
            final Javadoc javadoc = getOrCreateJavadoc(methodDeclaration);
            thrownExceptions.forEach(thrownException -> addBlockTag(Type.THROWS,
                    thrownException + " This method is expected to throw {@link " + thrownException + "}",
                    javadoc));
            methodDeclaration.setJavadocComment(javadoc);
        }
    }


    protected void addBlockTag(Parameter parameter, Javadoc javadoc) {
        final JavadocDescriptionElement inlineTag = createJavaDocSnippetFor(parameter);
        refactored(Type.PARAM, javadoc, inlineTag);
    }

    protected void addBlockTag(com.github.javaparser.ast.type.Type type, Javadoc javadoc) {
        addBlockTag("", type, javadoc);
    }

    protected void addBlockTag(String name, com.github.javaparser.ast.type.Type type, Javadoc javadoc) {
        final JavadocDescriptionElement inlineTag = createJavaDocSnippetFor(name, type);
        refactored(Type.RETURN, javadoc, inlineTag);
    }

    private void refactored(Type type, Javadoc javadoc, JavadocDescriptionElement inlineTag) {
        final List<JavadocBlockTag> blockTags = javadoc.getBlockTags();
        final JavadocBlockTag blockTag = new JavadocBlockTag(type, "");
        blockTag.getContent().getElements().add(inlineTag);
        blockTags.add(blockTag);
        blockTags.sort(compareBlockTagTypes());
    }

    protected void addBlockTag(Type type, String content, Javadoc javadoc) {
        final JavadocBlockTag blockTag = new JavadocBlockTag(type, content);
        final List<JavadocBlockTag> blockTags = javadoc.getBlockTags();
        blockTags.add(blockTag);
        blockTags.sort(compareBlockTagTypes());
    }

    protected Comparator<JavadocBlockTag> compareBlockTagTypes() {
        final Map<Type, Integer> orderMap = new EnumMap<>(Type.class);
        orderMap.put(Type.AUTHOR, 0);
        orderMap.put(Type.PARAM, 1);
        orderMap.put(Type.THROWS, 2);
        orderMap.put(Type.VERSION, 3);
        orderMap.put(Type.SINCE, 4);
        orderMap.put(Type.DEPRECATED, 5);
        orderMap.put(Type.SEE, 6);
        orderMap.put(Type.RETURN, 10);
        return Comparator.comparing(blocTag -> orderMap.getOrDefault(blocTag.getType(), Integer.MAX_VALUE));
    }

    protected JavadocDescriptionElement createJavaDocSnippetFor(Parameter parameter) {
        String paramFormat = "%s of type {@link %s}";
        com.github.javaparser.ast.type.Type parameterType = parameter.getType();
        if (parameterType.isPrimitiveType()) {
            paramFormat = "%s of type %s ";
        }
        final String snippetText = String.format(paramFormat, parameter.getNameAsString(),
                parameterType.asString());
        return new JavadocSnippet(snippetText);
    }

    protected JavadocDescriptionElement createJavaDocSnippetFor(String name, com.github.javaparser.ast.type.Type type) {
        String paramFormat = "%s of type " + createLinkSnippet(type);
        final String snippetText = String.format(paramFormat, name,
                type.asString());
        return new JavadocSnippet(snippetText);
    }

    public String createLinkSnippet(com.github.javaparser.ast.type.Type node) {

        String paramFormat = "{@link %s}";
        if (node.isPrimitiveType()) {
            paramFormat = "%s ";
        }

        return String.format(paramFormat, node.asString());
    }

    protected boolean isInheritedMethod(MethodDeclaration methodDeclaration) {
        return methodDeclaration.isAnnotationPresent(Override.class);
    }


    /**
     * setInt TODO: Complete the description of this method
     *
     * @param u   of type {@link String}
     * @param h   of type int
     * @param <T> The generic type annotated by T
     * @param <R> The generic type annotated by R
     * @return a value of type {@link Integer}
     * @throws Exception   This method is expected to throw {@link Exception}
     * @throws IOException This method is expected to throw {@link IOException}
     */
    public <T, R> Integer setInt(String u, int h) throws Exception, IOException {
        if (true)
            return 0;
        else
            return 1;
    }
}
