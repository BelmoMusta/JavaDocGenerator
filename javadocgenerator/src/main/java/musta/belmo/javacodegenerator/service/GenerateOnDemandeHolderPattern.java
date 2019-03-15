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
        boolean addOnDemand = false;
        for (ClassOrInterfaceDeclaration classOrInterfaceDeclaration : all) {
            List<MethodDeclaration> methods = classOrInterfaceDeclaration.getMethods();
            Iterator<MethodDeclaration> iterator = methods.iterator();

            String className = classOrInterfaceDeclaration.getName().asString();
            while (iterator.hasNext() && !addOnDemand) {
                MethodDeclaration methodDeclaration = iterator.next();
                if ("getInstance".equals(methodDeclaration.getName().asString())) {
                    iterator.remove();
                }
                MethodDeclaration getInstance = classOrInterfaceDeclaration.addMethod("getInstance", Modifier.PUBLIC, Modifier.STATIC);
                getInstance.setType(new TypeParameter(classOrInterfaceDeclaration.getNameAsString()));
                BlockStmt blockStmt = new BlockStmt();
                ReturnStmt returnStmt = new ReturnStmt();
                NameExpr nameExpr = new NameExpr();
                nameExpr.setName(className + "Holder.INSTANCE");
                returnStmt.setExpression(nameExpr);
                blockStmt.addStatement(returnStmt);
                getInstance.setBody(blockStmt);
                addOnDemand = true;

            }

            if (addOnDemand) {
                ClassOrInterfaceDeclaration decl = new ClassOrInterfaceDeclaration();
                decl.setName(className + "Holder");
                decl.setStatic(true);
                decl.setPrivate(true);


                JavadocDescription javadocDescription = new JavadocDescription();
                Javadoc javadoc = new Javadoc(javadocDescription);
                String text = "Classe pour l'initialisation à la demande de la classe {@link " + className + "}.";

                JavadocSnippet element = new JavadocSnippet(text);
                javadocDescription.addElement(element);
                decl.setJavadocComment(javadoc);

                ObjectCreationExpr objectCreationExpr = new ObjectCreationExpr();
                objectCreationExpr.setType(className);
                FieldDeclaration fieldDeclaration = decl.addFieldWithInitializer(new TypeParameter(className), "INSTANCE", objectCreationExpr, Modifier.STATIC, Modifier.FINAL, Modifier.PUBLIC);

                JavaDocGenerator generator = JavaDocGenerator.getInstance();
                generator.generateFieldJavaDoc(fieldDeclaration, "L'unique instance de la classe {@link " + className + "}.");

                ConstructorDeclaration constructorDeclaration = decl.addConstructor(Modifier.PRIVATE);
                constructorDeclaration.getBody().addOrphanComment(new LineComment("Constructeur par défaut"));
                JavadocDescription jDocdescription = new JavadocDescription();
                JavadocDescriptionElement javadocDescriptionElement = new JavadocSnippet("Constructeur par défaut de la classe {@link " + constructorDeclaration.getName() + "}.");
                jDocdescription.addElement(javadocDescriptionElement);
                Javadoc javadocO = new Javadoc(jDocdescription);
                constructorDeclaration.setJavadocComment(javadocO);

                NodeList<BodyDeclaration<?>> list = new NodeList<>();
                list.add(decl);
                list.addAll(classOrInterfaceDeclaration.getMembers());
                classOrInterfaceDeclaration.getMembers().clear();
                classOrInterfaceDeclaration.getMembers().addAll(list);
            }
        }
        if (addOnDemand)
            return compilationUnit;
        return null;
    }
}
