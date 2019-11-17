package musta.belmo.javacodegenerator.service;

import com.github.javaparser.ast.NodeList;
import com.github.javaparser.ast.type.ReferenceType;
import com.github.javaparser.javadoc.Javadoc;
import com.github.javaparser.javadoc.JavadocBlockTag;

public class ExceptionJavaDoc {
    /**
     * Add exceptions to java doc
     *
     * @param thrownExceptions {@link NodeList}
     * @param javadoc          {@link Javadoc}
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
}
