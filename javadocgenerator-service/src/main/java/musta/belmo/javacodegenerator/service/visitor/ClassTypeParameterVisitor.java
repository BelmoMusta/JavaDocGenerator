package musta.belmo.javacodegenerator.service.visitor;

import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.type.TypeParameter;
import com.github.javaparser.javadoc.Javadoc;
import com.github.javaparser.javadoc.JavadocBlockTag;

public class ClassTypeParameterVisitor extends AbstractCommonVisitor<ClassOrInterfaceDeclaration> {
    private static final ClassTypeParameterVisitor INSTANCE = new ClassTypeParameterVisitor();

    public static ClassTypeParameterVisitor getInstance() {
        return INSTANCE;
    }

    @Override
    public void visit(TypeParameter src, ClassOrInterfaceDeclaration destination) {
        if (src.getParentNode().isPresent() && (destination.equals(src.getParentNode().get()))) {

            final Javadoc javadoc = getOrCreateJavadoc(destination);
            addBlockTag(JavadocBlockTag.Type.PARAM, String.format("<%1$s> The generic type annotated by %1$s",
                    src.getNameAsString()), javadoc);
            destination.setJavadocComment(javadoc);
        }
        super.visit(src, destination);
    }
}
