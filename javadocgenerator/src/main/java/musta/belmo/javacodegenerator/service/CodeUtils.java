package musta.belmo.javacodegenerator.service;

import com.github.javaparser.JavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.Modifier;
import com.github.javaparser.ast.NodeList;
import com.github.javaparser.ast.body.*;
import com.github.javaparser.ast.comments.BlockComment;
import com.github.javaparser.ast.comments.JavadocComment;
import com.github.javaparser.ast.comments.LineComment;
import com.github.javaparser.ast.expr.*;
import com.github.javaparser.ast.stmt.BlockStmt;
import com.github.javaparser.ast.stmt.ReturnStmt;
import com.github.javaparser.ast.type.TypeParameter;
import com.github.javaparser.javadoc.Javadoc;
import com.github.javaparser.javadoc.JavadocBlockTag;
import com.github.javaparser.javadoc.description.JavadocDescription;
import com.github.javaparser.javadoc.description.JavadocDescriptionElement;
import com.github.javaparser.javadoc.description.JavadocSnippet;
import com.github.javaparser.printer.YamlPrinter;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.util.*;

public class CodeUtils {

    public static Comparator<FieldDeclaration> getFieldComparator() {
        return (o1, o2) -> {
            int compare = getFieldLevel(o2) -
                    getFieldLevel(o1);

            if (compare == 0)
                compare = o1.getVariables().get(0).getName().asString().compareTo(
                        o2.getVariables().get(0).getName().asString());
            return compare;
        };
    }

    public static void cloneFieldDeclaration(FieldDeclaration from, final FieldDeclaration to) {
        to.setModifiers(from.getModifiers());
        to.setVariables(from.getVariables());
        from.getComment().ifPresent((str) -> to.setBlockComment(str.getContent()));
        to.setAnnotations(from.getAnnotations());
    }

    public static int getFieldLevel(FieldDeclaration fieldDeclaration) {

        int level = 0;

        if (fieldDeclaration.isPublic() && fieldDeclaration.isStatic()) {
            level += 100000;
        } else if (fieldDeclaration.isPublic()) {
            level += 20;
        }
        if (fieldDeclaration.isStatic()) {
            level += 10000;
        }
        if (fieldDeclaration.isFinal()) {
            level += 1000;
        }
        if (fieldDeclaration.isProtected()) {
            level += 100;
        }
        if (fieldDeclaration.isPrivate()) {
            level += 10;
        }
        if (fieldDeclaration.isTransient()) {
            level += 1;
        }
        return level;

    }

    public static String replaceSingletonWithOnDemand(CompilationUnit compilationUnit) {
        List<ClassOrInterfaceDeclaration> all = compilationUnit.findAll(ClassOrInterfaceDeclaration.class);
        boolean addOnDemand = false;
        for (ClassOrInterfaceDeclaration classOrInterfaceDeclaration : all) {
            List<MethodDeclaration> methods = classOrInterfaceDeclaration.getMethods();
            Iterator<MethodDeclaration> iterator = methods.iterator();

            String className = classOrInterfaceDeclaration.getName().asString();
            while (iterator.hasNext() && !addOnDemand) {
                MethodDeclaration methodDeclaration = iterator.next();
                if ("getInstance".equals(methodDeclaration.getName().asString())) {
                    BlockStmt blockStmt = new BlockStmt();
                    //return OrchestrationEtape2LazyHolder.INSTANCE;
                    ReturnStmt returnStmt = new ReturnStmt();
                    NameExpr nameExpr = new NameExpr();
                    nameExpr.setName(className + "Holder.INSTANCE");
                    returnStmt.setExpression(nameExpr);
                    blockStmt.addStatement(returnStmt);
                    methodDeclaration.setBody(blockStmt);
                    addOnDemand = true;
                }
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


                //      static final Something INSTANCE = new Something();
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
                Javadoc javadoc_ = new Javadoc(jDocdescription);
                constructorDeclaration.setJavadocComment(javadoc_);
                //  classOrInterfaceDeclaration.addMember(decl);

                NodeList<BodyDeclaration<?>> list = new NodeList<>();
                list.add(decl);
                list.addAll(classOrInterfaceDeclaration.getMembers());
                classOrInterfaceDeclaration.getMembers().clear();
                classOrInterfaceDeclaration.getMembers().addAll(list);
            }
        }
        if (addOnDemand)
            return compilationUnit.toString();
        return null;
    }

    public static String removeUnusedFields(CompilationUnit compilationUnit) {
        List<ClassOrInterfaceDeclaration> all = compilationUnit.findAll(ClassOrInterfaceDeclaration.class);

        for (ClassOrInterfaceDeclaration classOrInterfaceDeclaration : all) {
            Optional<FieldDeclaration> aInstance = classOrInterfaceDeclaration.getFieldByName("aInstance");


            if (!aInstance.isPresent()) {
                aInstance = classOrInterfaceDeclaration.getFieldByName("instance");
            }

            if (aInstance.isPresent()) {
                FieldDeclaration fieldDeclaration = aInstance.get();
                classOrInterfaceDeclaration.remove(fieldDeclaration);
            }

        }
        return compilationUnit.toString();

    }

    public static String removeModifierForFields(CompilationUnit compilationUnit, Modifier modifier) {
        boolean changed = false;
        List<ClassOrInterfaceDeclaration> all = compilationUnit.findAll(ClassOrInterfaceDeclaration.class);

        for (ClassOrInterfaceDeclaration classOrInterfaceDeclaration : all) {
            List<FieldDeclaration> fields = classOrInterfaceDeclaration.findAll(FieldDeclaration.class);

            for (FieldDeclaration field : fields) {
                if (field.getModifiers().contains(modifier)) {
                    changed = true;
                    field.getModifiers().remove(modifier);
                }
            }

        }
        if (changed) {
            return compilationUnit.toString();
        }
        return null;

    }


    public static void main_2(String[] args) throws Exception {
        File f = new File("C:\\Users\\mbelmokhtar\\Desktop\\Nouveau dossier\\draft\\src\\main\\java\\calculus\\Calculus.java");
        CompilationUnit parse = JavaParser.parse(f);

        System.out.println(removeUnusedFields(parse));

    }

    public static void main(String[] args) throws Exception {
        File f = new File("D:\\platformsg2_R_64\\workspace\\gk1geskrier_1_4_RC\\gk1geskrier-metier\\src\\main\\java\\fr\\msa\\agora\\gk1geskrier\\metier\\realisation\\transformationpivot\\utils\\AlimentCSE1.java");
        CompilationUnit parse = JavaParser.parse(f);

        System.out.println(replaceSingletonWithOnDemand(parse));

    }

    public static void main__(String[] args) throws Exception {
        File f = new File("C:\\Users\\mbelmokhtar\\Desktop\\Nouveau dossier\\draft\\src\\main\\java\\calculus\\Calculus.java");
        CompilationUnit parse = JavaParser.parse(f);

        System.out.println(removeModifierForFields(parse,Modifier.TRANSIENT));

    }

    public static void main_(String[] args) throws Exception {
        Collection<File> javaFiles = FileUtils.listFiles(new File("D:\\platformsg2_R_64\\workspace\\movalnsa_2_1_DEFAUT\\movalnsa-metier"), new String[]{"java"}, true);
        for (File javaFile : javaFiles) {
            CompilationUnit compilationUnit = JavaParser.parse(javaFile);
            String data = removeModifierForFields(compilationUnit,Modifier.TRANSIENT);
            if (data != null) {
                FileUtils.write(javaFile, data, "UTF-8");
            }
        }

    }
}
