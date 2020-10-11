    package musta.belmo.javacodegenerator.service.visitor;

    import com.github.javaparser.ast.body.MethodDeclaration;
    import com.github.javaparser.javadoc.Javadoc;
    import com.github.javaparser.javadoc.description.JavadocDescription;
    import com.github.javaparser.javadoc.description.JavadocSnippet;
    import musta.belmo.javacodegenerator.service.GeneratorConstantes;
    import musta.belmo.javacodegenerator.service.PropertiesHandler;

    public class ThrownExceptionsVisitor extends AbstractCommonVisitor<MethodDeclaration> {
        private static final ThrownExceptionsVisitor INSTANCE = new ThrownExceptionsVisitor();

        public static ThrownExceptionsVisitor getInstance() {
            return INSTANCE;
        }

        @Override
        public void visit(MethodDeclaration src, MethodDeclaration methodDeclaration) {
            addMethodJavaDocDescription(methodDeclaration);
            addThrowBlockTags(src, methodDeclaration);
            super.visit(src, methodDeclaration);
        }

        public void addMethodJavaDocDescription(MethodDeclaration methodDeclaration) {
            final JavadocDescription javadocDescription = new JavadocDescription();
            final Javadoc javadoc = new Javadoc(javadocDescription);
            final String methodName = methodDeclaration.getName().asString();
            final JavadocSnippet inheritDocSnippet = new JavadocSnippet(methodName + " " + PropertiesHandler.readFromProperties(GeneratorConstantes.TODO_METHOD_TEXT));
            javadocDescription.addElement(inheritDocSnippet);
            methodDeclaration.setJavadocComment(javadoc);

        }
    }
