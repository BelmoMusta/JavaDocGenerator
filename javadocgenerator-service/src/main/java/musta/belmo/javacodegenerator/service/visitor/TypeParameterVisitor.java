package musta.belmo.javacodegenerator.service.visitor;

import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.type.TypeParameter;
import com.github.javaparser.javadoc.Javadoc;
import com.github.javaparser.javadoc.JavadocBlockTag;

public class TypeParameterVisitor extends AbstractCommonVisitor<MethodDeclaration> {
    private static final TypeParameterVisitor INSTANCE = new TypeParameterVisitor();

    public static TypeParameterVisitor getInstance() {
        return INSTANCE;
    }

    @Override
    public void visit(TypeParameter src, MethodDeclaration destination) {
        final Javadoc javadoc = getOrCreateJavadoc(destination);
        addBlockTag(JavadocBlockTag.Type.PARAM, String.format("<%1$s> The generic type annotated by %1$s",
                src.getNameAsString()), javadoc);
        destination.setJavadocComment(javadoc);
        super.visit(src, destination);
    }
}
