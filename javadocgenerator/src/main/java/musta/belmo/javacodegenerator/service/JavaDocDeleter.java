package musta.belmo.javacodegenerator.service;

import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.ConstructorDeclaration;
import com.github.javaparser.ast.body.FieldDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.body.TypeDeclaration;
import musta.belmo.javacodecore.logger.Level;
import musta.belmo.javacodegenerator.files.FileTransformer;
import musta.belmo.javacodegenerator.service.exception.CompilationException;
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

    public void deleteJavaDocForAllClassesInPlace(File directory) throws IOException, CompilationException {
        deleteJavaDocForAllClasses(directory, directory);
    }

    public void deleteJavaDocForAllClasses(File directory, File destDirectory) throws IOException, CompilationException {
        logger.logCurrentMethod(Level.DEBUG, directory);
        logger.info("generateJavaDocForAllClasses : directory {}\n destination {}", directory, destDirectory);
        if (directory.isDirectory()) {
            Collection<File> files = FileUtils.listFiles(directory, new String[]{JAVA_EXTENSION}, true);
            for (File file : files) {
                deleteJavaDocInPlace(new File(destDirectory, file.getName()));
            }
        } else {
            deleteJavaDocInPlace(new File(destDirectory, directory.getName()));
        }
        logger.info("deleteJavaDocForAllClassesInPlace : done");
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

    public void deleteJavaDoc(final String srcPath, final String destPath) throws IOException, CompilationException {
        File srcFile = new File(srcPath);

       /* FileTransformer.<IOException> transformInPlace(srcFile, f -> {
            File destFile = new File(destPath);
            CompilationUnit compilationUnit = getCompilationUnit(srcFile);
            String javaDocAsString = deleteJavaDoc(compilationUnit.toString());
            FileUtils.write(destFile, javaDocAsString, UTF_8);
            logger.info("deleted javadoc for  file {}", srcPath);
        });*/


    }

    public void deleteJavaDocInPlace(String srcPath) throws IOException, CompilationException {
        deleteJavaDoc(srcPath, srcPath);
    }

    public void deleteJavaDocInPlace(File srcPath) throws IOException, CompilationException {
        final String path = srcPath.getAbsolutePath();
        deleteJavaDoc(path, path);
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
