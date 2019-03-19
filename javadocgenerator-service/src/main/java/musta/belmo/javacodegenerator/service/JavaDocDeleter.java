package musta.belmo.javacodegenerator.service;

import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.ConstructorDeclaration;
import com.github.javaparser.ast.body.FieldDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.body.TypeDeclaration;
import musta.belmo.javacodegenerator.exception.CompilationException;
import musta.belmo.javacodegenerator.logger.Level;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.util.Collection;

public class JavaDocDeleter extends AbstractCodeService {

    private static class JavaDocDeleterHolder {
        static final JavaDocDeleter DELETER = new JavaDocDeleter();
    }

    public static JavaDocDeleter getInstance() {
        return JavaDocDeleterHolder.DELETER;
    }

    public void deleteJavaDocForAllClasses(File directory) throws IOException, CompilationException {
        logger.logCurrentMethod(Level.DEBUG, directory, directory);
        logger.info("generateJavaDocForAllClasses : directory {}\n destination {}", directory, directory);
        if (directory.isDirectory()) {
            Collection<File> files = FileUtils.listFiles(directory, new String[]{JAVA_EXTENSION}, true);
            for (File file : files) {
                deleteJavaDocInPlace(file.getAbsolutePath());
            }
        } else {
            deleteJavaDocInPlace(directory.getAbsolutePath());
        }

        logger.info("deleteJavaDocForAllClasses : done");
    }

    /**
     * Delete old java doc
     *
     * @param compilationUnit @link CompilationUnit}
     */
    public void deleteOldJavaDoc(CompilationUnit compilationUnit) {
        compilationUnit.findAll(TypeDeclaration.class).forEach(TypeDeclaration::removeJavaDocComment);
        compilationUnit.findAll(ConstructorDeclaration.class).forEach(ConstructorDeclaration::removeJavaDocComment);
        compilationUnit.findAll(FieldDeclaration.class).forEach(FieldDeclaration::removeJavaDocComment);
        compilationUnit.findAll(MethodDeclaration.class).forEach(MethodDeclaration::removeJavaDocComment);
    }

    public void deleteJavaDocInPlace(String srcPath) throws IOException, CompilationException {
        File srcFile = new File(srcPath);
        CompilationUnit compilationUnit = getCompilationUnit(srcFile);
        String javaDocAsString = deleteJavaDoc(compilationUnit.toString());
        FileUtils.write(srcFile, javaDocAsString, UTF_8);
        logger.info("deleted javadoc for  file {}", srcPath);
    }

    /**
     * Delete java doc
     *
     * @param src @link String}
     * @return String
     */
    public String deleteJavaDoc(String src) throws CompilationException {
        CompilationUnit compilationUnit = getCompilationUnit(src);
        deleteOldJavaDoc(compilationUnit);
        return compilationUnit.toString();
    }
}
