package musta.belmo.javacodegenerator.service;

import com.github.javaparser.JavaParser;
import com.github.javaparser.ParseProblemException;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.ConstructorDeclaration;
import com.github.javaparser.ast.body.FieldDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.body.TypeDeclaration;
import musta.belmo.javacodegenerator.exception.CompilationException;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

/**
 * TODO: Complete the description of this class
 *
 * @author default author
 * @since 0.0.0.SNAPSHOT
 * @version 0.0.0
 */
public class CompilationUnitJavaDoc {

    /**
     * Generate java doc as string
     *
     * @param compilationUnit {@link CompilationUnit}
     * @return String
     */
    public static String generateJavaDocAsString(CompilationUnit compilationUnit) {
        compilationUnit.findAll(TypeDeclaration.class).forEach(JavaTypeJavaDoc::generateJavaDocForTypeDeclaration);
        compilationUnit.findAll(ConstructorDeclaration.class).forEach(JavaTypeJavaDoc::generateConstructorJavaDoc);
        compilationUnit.findAll(FieldDeclaration.class).forEach(FieldJavaDoc::generateFieldJavaDoc);
        compilationUnit.findAll(MethodDeclaration.class).forEach(MethodJavaDoc::generateMethodJavaDoc);
        return compilationUnit.toString();
    }

    /**
     * Generate java doc as string
     *
     * @param src {@link String}
     * @return String
     * @throws CompilationException the raised exception if error.
     */
    public static String generateJavaDocAsString(String src) throws CompilationException {
        return CompilationUnitJavaDoc.generateJavaDocAsString(getCompilationUnit(src));
    }

    /**
     * @param srcFile {@link File}
     * @return Attribut {@link #compilationUnit}
     * @throws FileNotFoundException the raised exception if error.
     * @throws CompilationException the raised exception if error.
     */
    protected static CompilationUnit getCompilationUnit(File srcFile) throws FileNotFoundException, CompilationException {
        CompilationUnit compilationUnit;
        try {
            compilationUnit = JavaParser.parse(srcFile);
        } catch (ParseProblemException parseProleme) {
            throw new CompilationException(parseProleme);
        }
        return compilationUnit;
    }

    /**
     * @param src {@link String}
     * @return Attribut {@link #compilationUnit}
     * @throws CompilationException the raised exception if error.
     */
    protected static CompilationUnit getCompilationUnit(String src) throws CompilationException {
        CompilationUnit compilationUnit;
        try {
            compilationUnit = JavaParser.parse(src);
        } catch (ParseProblemException parseException) {
            throw new CompilationException(parseException);
        }
        return compilationUnit;
    }
}
