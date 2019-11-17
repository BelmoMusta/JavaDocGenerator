package musta.belmo.javacodegenerator.service;

import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.FieldDeclaration;
import musta.belmo.javacodegenerator.exception.CompilationException;
import musta.belmo.javacodegenerator.logger.MustaLogger;
import musta.belmo.javacodegenerator.util.CodeUtils;

import java.io.File;
import java.util.List;

/**
 * TODO: Complete the description of this class
 *
 * @author default author
 * @since 0.0.0.SNAPSHOT
 * @version 0.0.0
 */
public abstract class AbstractJavaDocService implements GeneratorConstantes {

    /**
     * The {@link #logger} attribute.
     */
    protected static MustaLogger logger;

    /**
     * TODO: Complete the description of this method
     *
     * @param code {@link String}
     * @return String
     * @throws CompilationException the raised exception if error.
     */
    public String reorganize(String code) throws CompilationException {
        CompilationUnit compilationUnit = CompilationUnitJavaDoc.getCompilationUnit(code);
        CompilationUnit retCompilationUnit = new CompilationUnit();
        List<ClassOrInterfaceDeclaration> classContained = compilationUnit.findAll(ClassOrInterfaceDeclaration.class);
        classContained.stream().forEach(cls -> {
            ClassOrInterfaceDeclaration classOrInterfaceDeclaration = retCompilationUnit.addClass(cls.getName().asString());
            classOrInterfaceDeclaration.setModifiers(cls.getModifiers());
            List<FieldDeclaration> publicStaticFields = cls.findAll(FieldDeclaration.class);
            publicStaticFields.sort(CodeUtils.getFieldComparator());
            for (FieldDeclaration fieldDeclaration : publicStaticFields) {
                FieldDeclaration fieldDec = classOrInterfaceDeclaration.addField(fieldDeclaration.getCommonType(), "temp");
                CodeUtils.cloneFieldDeclaration(fieldDeclaration, fieldDec);
            }
        // TODO : search for static block first
        });
        return retCompilationUnit.toString();
    }

    /**
     * Indent code
     *
     * @param code {@link String}
     * @return String
     * @throws CompilationException the raised exception if error.
     */
    public String indentCode(String code) throws CompilationException {
        CompilationUnit compilationUnit = CompilationUnitJavaDoc.getCompilationUnit(code);
        return compilationUnit.toString();
    }

    /**
     * @param srcFile {@link File}
     * @param destinationFile {@link File}
     * @return Attribut {@link #destination}
     */
    public static File getDestination(File srcFile, File destinationFile) {
        return new File(destinationFile, srcFile.getName());
    }

}
