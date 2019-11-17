package musta.belmo.javacodegenerator.service;

import com.github.javaparser.ast.CompilationUnit;
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
 * @version 0.0.0
 * @since 0.0.0.SNAPSHOT
 */
public class JavaDocGenerator extends AbstractJavaDocService {

    /**
     * TODO: Complete the description of this class
     *
     * @author default author
     * @version 0.0.0
     * @since 0.0.0.SNAPSHOT
     */
    private static class JavaDocGeneratorHolder {

        /**
         * The {@link #INSTANCE} Constant of type {@link JavaDocGenerator} holding the value new JavaDocGenerator().
         */
        static final JavaDocGenerator INSTANCE = new JavaDocGenerator();

        /**
         * The JavaDocGeneratorHolder class constructor.
         */
        private JavaDocGeneratorHolder() {
            // Constructeur par défaut
        }
    }

    /**
     * The JavaDocGenerator class constructor.
     */
    private JavaDocGenerator() {
        logger = new MustaLogger(getClass());
        PropertiesHandler.loadProperties(null);
    }

    /**
     * @return Attribut {@link #instance}
     */
    public static JavaDocGenerator getInstance() {
        return JavaDocGeneratorHolder.INSTANCE;
    }

    /**
     * Generate java doc for all classes
     *
     * @param directory {@link File}
     * @throws IOException          the raised exception if error.
     * @throws CompilationException the raised exception if error.
     */
    public void generateJavaDocForAllClasses(File directory) throws IOException, CompilationException {
        logger.logCurrentMethod(Level.DEBUG, directory, directory);
        logger.info(ALL_CLASSES_DOC, directory, directory);
        if (directory.isDirectory()) {
            Collection<File> files = FileUtils.listFiles(directory, new String[]{JAVA_EXTENSION}, true);
            for (File file : files) {
                generateJavaDoc(file);
            }
        } else {
            generateJavaDoc(directory);
        }
        logger.info(GENERATION_DONE);
    }

    /**
     * Generate java doc
     *
     * @param srcFile         {@link File}
     * @param destinationFile {@link File}
     * @throws IOException          the raised exception if error.
     * @throws CompilationException the raised exception if error.
     */
    private void generateJavaDoc(File srcFile, File destinationFile) throws IOException, CompilationException {
        CompilationUnit compilationUnit = CompilationUnitJavaDoc.getCompilationUnit(srcFile);
        String javaDocAsString = CompilationUnitJavaDoc.generateJavaDocAsString(compilationUnit.toString());
        File destFile = getDestination(srcFile, destinationFile);
        FileUtils.write(destFile, javaDocAsString, UTF_8);
        logger.info("generated javadoc for  file {}", srcFile.getAbsolutePath());
    }

    /**
     * Generate java doc
     *
     * @param currentFile {@link File}
     * @throws IOException          the raised exception if error.
     * @throws CompilationException the raised exception if error.
     */
    private void generateJavaDoc(File currentFile) throws IOException, CompilationException {
        generateJavaDoc(currentFile, currentFile.getParentFile());
    }
}
