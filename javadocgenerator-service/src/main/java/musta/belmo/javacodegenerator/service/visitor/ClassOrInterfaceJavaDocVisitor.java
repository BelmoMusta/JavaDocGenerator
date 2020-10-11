package musta.belmo.javacodegenerator.service.visitor;

import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.ConstructorDeclaration;
import com.github.javaparser.ast.body.FieldDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.expr.Name;
import com.github.javaparser.ast.type.TypeParameter;
import com.github.javaparser.ast.visitor.VoidVisitorAdapter;
import musta.belmo.javacodegenerator.service.JavaTypeJavaDoc;

public class ClassOrInterfaceJavaDocVisitor extends AbstractCommonVisitor<ClassOrInterfaceDeclaration> {
    private static final ClassOrInterfaceJavaDocVisitor INSTANCE = new ClassOrInterfaceJavaDocVisitor();
    private FieldJavaDocVisitor fieldJavaDocVisitor = FieldJavaDocVisitor.getInstance();
    private MethodJavaDocVisitor methodJavaDocVisitor = MethodJavaDocVisitor.getInstance();
    private InheritedMethodJavaDocVisitor inheritedMethodVisitor = InheritedMethodJavaDocVisitor.getInstance();
    ClassTypeParameterVisitor typeParameterVisitor = ClassTypeParameterVisitor.getInstance();

    public static ClassOrInterfaceJavaDocVisitor getInstance() {
        return INSTANCE;
    }

    @Override
    public void visit(ClassOrInterfaceDeclaration n, ClassOrInterfaceDeclaration arg) {
        JavaTypeJavaDoc.generateJavaDocForTypeDeclaration(n);
        super.visit(n, arg);
    }

    @Override
    public void visit(ConstructorDeclaration n, ClassOrInterfaceDeclaration arg) {
        super.visit(n, arg);
    }

    @Override
    public void visit(TypeParameter n, ClassOrInterfaceDeclaration arg) {
        n.accept(typeParameterVisitor,arg);
        //super.visit(n, arg);
    }

    @Override
    public void visit(FieldDeclaration fieldDeclaration, ClassOrInterfaceDeclaration compilationUnit) {
        fieldDeclaration.accept(fieldJavaDocVisitor, fieldDeclaration);
        super.visit(fieldDeclaration, compilationUnit);
    }

    @Override
    public void visit(MethodDeclaration methodDeclaration, ClassOrInterfaceDeclaration compilationUnit) {
        VoidVisitorAdapter<MethodDeclaration> methodVisitor = this.methodJavaDocVisitor;
        if (isInheritedMethod(methodDeclaration)) {
            methodVisitor = inheritedMethodVisitor;
        }
        methodDeclaration.accept(methodVisitor, methodDeclaration);
        super.visit(methodDeclaration, compilationUnit);
    }

}
