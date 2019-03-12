package musta.belmo.javacodegenerator.service;

import com.github.javaparser.JavaParser;
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
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

public class CodeUtils {


    public static void main_2(String[] args) throws Exception {
        File f = new File("C:\\Users\\mbelmokhtar\\Desktop\\Nouveau dossier\\draft\\src\\main\\java\\calculus\\Calculus.java");
        CompilationUnit parse = JavaParser.parse(f);

        System.out.println(musta.belmo.javacodecore.CodeUtils.removeUnusedFields(parse));

    }



    public static void main__(String[] args) throws Exception {
        File f = new File("C:\\Users\\mbelmokhtar\\Desktop\\Nouveau dossier\\draft\\src\\main\\java\\calculus\\Calculus.java");
        CompilationUnit parse = JavaParser.parse(f);

        System.out.println(musta.belmo.javacodecore.CodeUtils.removeModifierForFields(parse, Modifier.TRANSIENT));

    }

    public static void main_(String[] args) throws Exception {
        Collection<File> javaFiles = FileUtils.listFiles(new File("D:\\platformsg2_R_64\\workspace\\movalnsa_2_1_DEFAUT\\movalnsa-metier"), new String[]{"java"}, true);
        for (File javaFile : javaFiles) {
            CompilationUnit compilationUnit = JavaParser.parse(javaFile);
            String data = musta.belmo.javacodecore.CodeUtils.removeModifierForFields(compilationUnit, Modifier.TRANSIENT);
            if (data != null) {
                FileUtils.write(javaFile, data, "UTF-8");
            }
        }

    }
}
