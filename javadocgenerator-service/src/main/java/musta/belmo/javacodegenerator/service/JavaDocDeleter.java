package musta.belmo.javacodegenerator.service;

import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.ConstructorDeclaration;
import com.github.javaparser.ast.body.FieldDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.body.TypeDeclaration;
import musta.belmo.javacodegenerator.exception.CompilationException;
import musta.belmo.javacodegenerator.logger.Level;
import musta.belmo.javacodegenerator.logger.MustaLogger;
import org.apache.commons.io.FileUtils;
import java.io.File;
import java.io.IOException;
import java.util.Collection;

/**
 * TODO: Complete the description of this class
 *
 * @author default author
 * @since 0.0.0.SNAPSHOT
 * @version 0.0.0
 */
public class JavaDocDeleter extends AbstractJavaDocService {

    /**
     * The JavaDocDeleter class constructor.
     */
    public JavaDocDeleter() {
        logger = new MustaLogger(getClass());
    }

    /**
     * TODO: Complete the description of this class
     *
     * @author default author
     * @since 0.0.0.SNAPSHOT
     * @version 0.0.0
     */
    private static class JavaDocDeleterHolder {

        /**
         * The {@link #DELETER} Constant of type {@link JavaDocDeleter} holding the value new JavaDocDeleter().
         */
        static final JavaDocDeleter DELETER = new JavaDocDeleter();
    }

    /**
     * @return Attribut {@link #instance}
     */
    public static JavaDocDeleter getInstance() {
        return JavaDocDeleterHolder.DELETER;
    }

    /**
     * Delete java doc for all classes
     *
     * @param directory {@link File}
     * @throws IOException the raised exception if error.
     * @throws CompilationException the raised exception if error.
     */
    public void deleteJavaDocForAllClasses(File directory) throws IOException, CompilationException {
        logger.logCurrentMethod(Level.DEBUG, directory, directory);
        logger.info("generateJavaDocForAllClasses : directory {}\n destination {}", directory, directory);
        if (directory.isDirectory()) {
            Collection<File> files = FileUtils.listFiles(directory, new String[] { JAVA_EXTENSION }, true);
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
     * @param compilationUnit {@link CompilationUnit}
     */
    public void deleteOldJavaDoc(CompilationUnit compilationUnit) {
        compilationUnit.findAll(TypeDeclaration.class).forEach(TypeDeclaration::removeJavaDocComment);
        compilationUnit.findAll(ConstructorDeclaration.class).forEach(ConstructorDeclaration::removeJavaDocComment);
        compilationUnit.findAll(FieldDeclaration.class).forEach(FieldDeclaration::removeJavaDocComment);
        compilationUnit.findAll(MethodDeclaration.class).forEach(MethodDeclaration::removeJavaDocComment);
    }

    /**
     * Delete java doc in place
     *
     * @param srcPath {@link String}
     * @throws IOException the raised exception if error.
     * @throws CompilationException the raised exception if error.
     */
    public void deleteJavaDocInPlace(String srcPath) throws IOException, CompilationException {
        File srcFile = new File(srcPath);
        CompilationUnit compilationUnit = CompilationUnitJavaDoc.getCompilationUnit(srcFile);
        String javaDocAsString = deleteJavaDoc(compilationUnit.toString());
        FileUtils.write(srcFile, javaDocAsString, UTF_8);
        logger.info("deleted javadoc for  file {}", srcPath);
    }

    /**
     * Delete java doc
     *
     * @param src {@link String}
     * @return String
     * @throws CompilationException the raised exception if error.
     */
    public String deleteJavaDoc(String src) throws CompilationException {
        CompilationUnit compilationUnit = CompilationUnitJavaDoc.getCompilationUnit(src);
        deleteOldJavaDoc(compilationUnit);
        return compilationUnit.toString();
    }
}
