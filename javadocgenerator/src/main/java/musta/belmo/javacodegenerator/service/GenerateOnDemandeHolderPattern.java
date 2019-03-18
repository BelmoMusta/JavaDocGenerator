package musta.belmo.javacodegenerator.service;

import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.Modifier;
import com.github.javaparser.ast.NodeList;
import com.github.javaparser.ast.body.*;
import com.github.javaparser.ast.comments.LineComment;
import com.github.javaparser.ast.expr.NameExpr;
import com.github.javaparser.ast.expr.ObjectCreationExpr;
import com.github.javaparser.ast.stmt.BlockStmt;
import com.github.javaparser.ast.stmt.ReturnStmt;
import com.github.javaparser.ast.type.TypeParameter;
import com.github.javaparser.javadoc.Javadoc;
import com.github.javaparser.javadoc.description.JavadocDescription;
import com.github.javaparser.javadoc.description.JavadocDescriptionElement;
import com.github.javaparser.javadoc.description.JavadocSnippet;

import java.util.Iterator;
import java.util.List;

public class GenerateOnDemandeHolderPattern extends AbstractJavaCodeGenerator {

    public CompilationUnit generate(CompilationUnit compilationUnitSrc) {
        CompilationUnit compilationUnit = compilationUnitSrc.clone();
        List<ClassOrInterfaceDeclaration> all = compilationUnit.findAll(ClassOrInterfaceDeclaration.class);
        boolean onDemandAdded = false;
        for (ClassOrInterfaceDeclaration classOrInterfaceDeclaration : all) {
            List<MethodDeclaration> methods = classOrInterfaceDeclaration.getMethods();
            Iterator<MethodDeclaration> iterator = methods.iterator();

            String className = classOrInterfaceDeclaration.getNameAsString();
            while (iterator.hasNext() && !onDemandAdded) {
                MethodDeclaration methodDeclaration = iterator.next();
                if ("getInstance".equals(methodDeclaration.getNameAsString()) && methodDeclaration.getParameters().isEmpty()) {
                    iterator.remove();
                }
                MethodDeclaration getInstanceMethod = classOrInterfaceDeclaration.addMethod("getInstance", Modifier.PUBLIC, Modifier.STATIC);
                getInstanceMethod.setType(new TypeParameter(classOrInterfaceDeclaration.getNameAsString()));
                BlockStmt blockStmt = new BlockStmt();
                ReturnStmt returnStmt = new ReturnStmt();
                NameExpr nameExpr = new NameExpr();
                nameExpr.setName(className + "Holder.INSTANCE");
                returnStmt.setExpression(nameExpr);
                blockStmt.addStatement(returnStmt);
                getInstanceMethod.setBody(blockStmt);
                onDemandAdded = true;

            }

            if (onDemandAdded) {
                ClassOrInterfaceDeclaration staticInnerClass = new ClassOrInterfaceDeclaration();
                staticInnerClass.setName(className + "Holder");
                staticInnerClass.setStatic(true);
                staticInnerClass.setPrivate(true);

                addJavaDoc(staticInnerClass);

                ObjectCreationExpr objectCreationExpr = new ObjectCreationExpr();
                objectCreationExpr.setType(className);
                FieldDeclaration fieldDeclaration = staticInnerClass.addFieldWithInitializer(new TypeParameter(className), "INSTANCE", objectCreationExpr, Modifier.STATIC, Modifier.FINAL, Modifier.PUBLIC);

                JavaDocGenerator generator = JavaDocGenerator.getInstance();
                generator.generateFieldJavaDoc(fieldDeclaration, "L'unique instance de la classe {@link " + className + "}.");

                ConstructorDeclaration constructorDeclaration = staticInnerClass.addConstructor(Modifier.PRIVATE);
                constructorDeclaration.getBody().addOrphanComment(new LineComment("Constructeur par défaut"));
                JavadocDescription jDocdescription = new JavadocDescription();
                JavadocDescriptionElement javadocDescriptionElement = new JavadocSnippet("Constructeur par défaut de la classe {@link " + constructorDeclaration.getName() + "}.");
                jDocdescription.addElement(javadocDescriptionElement);
                Javadoc javadocO = new Javadoc(jDocdescription);
                constructorDeclaration.setJavadocComment(javadocO);

                NodeList<BodyDeclaration<?>> list = new NodeList<>();
                list.add(staticInnerClass);
                list.addAll(classOrInterfaceDeclaration.getMembers());
                classOrInterfaceDeclaration.getMembers().clear();
                classOrInterfaceDeclaration.getMembers().addAll(list);
            }
        }
        if (onDemandAdded)
            return compilationUnit;
        return null;
    }

    private void addJavaDoc(ClassOrInterfaceDeclaration staticInnerClass) {
        String className = staticInnerClass.getNameAsString();
        JavadocDescription javadocDescription = new JavadocDescription();
        Javadoc javadoc = new Javadoc(javadocDescription);
        String commentText = "Classe pour l'initialisation à la demande de la classe {@link " + className + "}.";

        JavadocSnippet javadocSnippet = new JavadocSnippet(commentText);
        javadocDescription.addElement(javadocSnippet);
        staticInnerClass.setJavadocComment(javadoc);
    }
}
