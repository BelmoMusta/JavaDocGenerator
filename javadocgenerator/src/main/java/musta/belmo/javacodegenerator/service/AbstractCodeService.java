package musta.belmo.javacodegenerator.service;

import com.github.javaparser.JavaParser;
import com.github.javaparser.ParseProblemException;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.FieldDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.javadoc.Javadoc;
import com.github.javaparser.javadoc.description.JavadocDescription;
import com.github.javaparser.javadoc.description.JavadocSnippet;
import musta.belmo.javacodecore.CodeUtils;
import musta.belmo.javacodecore.Utils;
import musta.belmo.javacodecore.logger.Level;
import musta.belmo.javacodecore.logger.MustaLogger;
import musta.belmo.javacodegenerator.service.exception.CompilationException;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;
import java.util.Properties;

public abstract class AbstractCodeService implements GeneratorConstantes {
    /**
     * L'attribut {@link #properties}.
     */
    private Properties properties;

    /**
     * L'attribut {@link #propertiesPath}.
     */
    private String propertiesPath;

    /**
     * L'attribut {@link #logger}.
     */
    protected MustaLogger logger;

    protected CompilationUnit getCompilationUnit(File srcFile) throws FileNotFoundException, CompilationException {
        CompilationUnit compilationUnit;
        try {
            compilationUnit = JavaParser.parse(srcFile);
        } catch (ParseProblemException parseProleme) {
            throw new CompilationException(parseProleme);
        }
        return compilationUnit;
    }

    protected CompilationUnit getCompilationUnit(String src) throws CompilationException {
        CompilationUnit compilationUnit;
        try {
            compilationUnit = JavaParser.parse(src);
        } catch (ParseProblemException parseException) {
            throw new CompilationException(parseException);
        }
        return compilationUnit;
    }

    /**
     * Read from properties
     *
     * @param key @link String}
     * @return String
     */
    public String readFromProperties(String key) {
        logger.logCurrentMethod(Level.DEBUG, key);
        return properties.getProperty(key);
    }

    /**
     * Load properties
     *
     * @param propertiesPath @link String}
     */
    public void loadProperties(String propertiesPath) {
        logger.logCurrentMethod(Level.DEBUG, propertiesPath);
        URL resource = null;
        if (propertiesPath == null) {
            resource = JavaDocGenerator.class.getClassLoader().getResource(APPLICATION_PROPERTIES);
        } else {
            File file = new File(propertiesPath);
            try {
                resource = file.toURI().toURL();
            } catch (MalformedURLException e) {
                logger.error(propertiesPath, e);
            }
            if (resource == null) {
                resource = JavaDocGenerator.class.getClassLoader().getResource(APPLICATION_PROPERTIES);
            }
        }
        if (resource != null) {
            properties = new Properties();
            try {
                InputStream resourceAsStream = resource.openStream();
                properties.load(resourceAsStream);
            } catch (IOException e) {
                logger.error("readFromProperties", e);
            }
        }
        this.propertiesPath = propertiesPath;
        if (resource != null) {
            this.propertiesPath = resource.getPath();
        }
    }

    /**
     * @return Attribut {@link #propertiesPath}
     */
    public String getPropertiesPath() {
        return propertiesPath;
    }

    /**
     * @param propertiesPath Valeur à affecter à l'attribut {@link #propertiesPath}
     */
    public void setPropertiesPath(String propertiesPath) {
        this.propertiesPath = propertiesPath;
    }

    /**
     * @param code String
     * @return String
     */
    public String reorganize(String code) throws CompilationException {
        CompilationUnit compilationUnit = getCompilationUnit(code);
        CompilationUnit retCompilationUnit = new CompilationUnit();
        List<ClassOrInterfaceDeclaration> classContained = compilationUnit.findAll(ClassOrInterfaceDeclaration.class);
        classContained.stream().forEach(cls -> {
            ClassOrInterfaceDeclaration classOrInterfaceDeclaration = retCompilationUnit.addClass(cls.getName().asString());
            classOrInterfaceDeclaration.setModifiers(cls.getModifiers());
            List<FieldDeclaration> publicStaticFields = cls.findAll(FieldDeclaration.class);
            publicStaticFields.sort(musta.belmo.javacodecore.CodeUtils.getFieldComparator());
            for (FieldDeclaration fieldDeclaration : publicStaticFields) {
                FieldDeclaration fieldDec = classOrInterfaceDeclaration.addField(fieldDeclaration.getCommonType(), "temp");
                CodeUtils.cloneFieldDeclaration(fieldDeclaration, fieldDec);
            }
            // TODO : search for static block first
        });
        return retCompilationUnit.toString();
    }

    /**
     * @param compilationUnit CompilationUnit
     * @return String
     */
    public String indentCode(CompilationUnit compilationUnit) throws CompilationException {
        return indentCode(compilationUnit.toString());
    }

    /**
     * @param code String
     * @return String
     */
    public String indentCode(String code) throws CompilationException {
        CompilationUnit compilationUnit = getCompilationUnit(code);
        return compilationUnit.toString();
    }

    /**
     * @param destinationFile @link String}
     * @param srcFile         @link File}
     * @param compilationUnit @link CompilationUnit}
     * @return File
     */
    public File getDestination(String destinationFile, File srcFile, CompilationUnit compilationUnit) {
        return compilationUnit.getPackageDeclaration().map(packageDeclaration -> new File(destinationFile, Utils.convertPackageDeclarationToPath(packageDeclaration.getName().asString()) + File.separator + srcFile.getName())).orElseGet(() -> new File(destinationFile));
    }

    /**
     *
     * @param methodDeclaration
     */
    public void addDocForOverriddenMethods(MethodDeclaration methodDeclaration) {
        JavadocDescription javadocDescription = new JavadocDescription();
        Javadoc javadoc = new Javadoc(javadocDescription);
        JavadocSnippet inheritDocSnippet = new JavadocSnippet(readFromProperties(INHERIT_DOC));
        methodDeclaration.removeJavaDocComment();
        javadocDescription.addElement(inheritDocSnippet);
        methodDeclaration.setJavadocComment(javadoc);

    }
}
