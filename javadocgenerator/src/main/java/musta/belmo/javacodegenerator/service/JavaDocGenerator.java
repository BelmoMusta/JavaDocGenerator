package musta.belmo.javacodegenerator.service;

import com.github.javaparser.JavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.Modifier;
import com.github.javaparser.ast.NodeList;
import com.github.javaparser.ast.body.*;
import com.github.javaparser.ast.comments.LineComment;
import com.github.javaparser.ast.expr.Expression;
import com.github.javaparser.ast.expr.SimpleName;
import com.github.javaparser.ast.stmt.BlockStmt;
import com.github.javaparser.ast.stmt.Statement;
import com.github.javaparser.ast.type.ReferenceType;
import com.github.javaparser.ast.type.Type;
import com.github.javaparser.javadoc.Javadoc;
import com.github.javaparser.javadoc.JavadocBlockTag;
import com.github.javaparser.javadoc.description.JavadocDescription;
import com.github.javaparser.javadoc.description.JavadocSnippet;
import musta.belmo.javacodecore.logger.Level;
import musta.belmo.javacodecore.logger.MustaLogger;
import musta.belmo.javacodegenerator.FormattedJavadocBlockTag;
import musta.belmo.javacodecore.Utils;
import musta.belmo.javacodecore.ZipUtils;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Collection;
import java.util.EnumSet;
import java.util.Optional;
import java.util.Properties;

/**
 * TODO: Compléter la description de cette classe
 *
 * @author toBeSpecified
 * @since 1.0.0.SNAPSHOT
 */
public class JavaDocGenerator implements GeneratorConstantes {

    /**
     * L'attribut {@link #propertiesPath}.
     */
    private String propertiesPath;

    /**
     * L'attribut {@link #properties}.
     */
    private Properties properties;

    /**
     * L'attribut {@link #logger}.
     */
    private final MustaLogger logger;

    /**
     * Constructeur de la classe JavaDocGenerator
     */
    public JavaDocGenerator() {
        logger = new MustaLogger(getClass());
        loadProperties(null);
        // Constructeur par défaut
    }

    /**
     * Read from properties
     *
     * @param key {@link String}
     * @return String
     */
    private String readFromProperties(String key) {
        logger.logCurrentMethod(Level.DEBUG, key);
        return properties.getProperty(key);
    }

    /**
     * Load properties
     *
     * @param propertiesPath {@link String}
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
    }

    /**
     * Generate java doc for all classes
     *
     * @param directory        {@link File}
     * @param dest             {@link File}
     * @param toZip            boolean
     * @param deleteOldJavadoc boolean
     * @throws IOException Exception levée si erreur.
     */
    public void generateJavaDocForAllClasses(File directory, File dest, boolean toZip, boolean deleteOldJavadoc) throws IOException {
        logger.logCurrentMethod(Level.DEBUG, directory, dest);
        logger.info("generateJavaDocForAllClasses : directory {}\n destination {}", directory, dest);
        File destinationZip = new File(dest.getAbsolutePath());
        boolean isDirectory = directory.isDirectory();
        if (isDirectory) {
            Collection<File> files = FileUtils.listFiles(directory, new String[]{JAVA_EXTENSION}, true);
            for (File file : files) {
                generateJavaDoc(file.getAbsolutePath(), dest.getAbsolutePath(), deleteOldJavadoc);
            }
        } else {
            generateJavaDoc(directory.getAbsolutePath(), dest.getAbsolutePath(), deleteOldJavadoc);
        }
        if (toZip) {
            ZipUtils.zip(destinationZip, new File(destinationZip.getParent(),
                    destinationZip.getName().concat(".zip")));
            logger.info("file add to zip file {}", destinationZip.getAbsolutePath());
        }
        logger.info("generateJavaDocForAllClasses : done");
    }

    /**
     * Generate java doc for all classes
     *
     * @param directory        {@link String}
     * @param dest             {@link String}
     * @param toZip            boolean
     * @param deleteOldJavadoc boolean
     * @throws IOException Exception levée si erreur.
     */
    public void generateJavaDocForAllClasses(String directory,
                                             String dest,
                                             boolean toZip,
                                             boolean deleteOldJavadoc) throws IOException {
        logger.info("generateJavaDocForAllClasses : directory {}\n destination {}", directory, dest);
        File dir = new File(directory);
        File destinationZip = new File(dest);
        generateJavaDocForAllClasses(dir, destinationZip, toZip, deleteOldJavadoc);
        logger.info("generateJavaDocForAllClasses : done");
    }

    /**
     * Delete java doc for all classes
     *
     * @param directory {@link String}
     * @param dest      {@link String}
     * @param toZip     boolean
     * @throws Exception Exception levée si erreur.
     */
    public void deleteJavaDocForAllClasses(String directory,
                                            String dest,
                                            boolean toZip) throws Exception {
        File dir = new File(directory);
        if (dir.isDirectory()) {
            Collection<File> files = FileUtils.listFiles(dir, new String[]{JAVA_EXTENSION}, toZip);
            for (File file : files) {
                deleteJavaDoc(file.getAbsolutePath(), dest);
            }
        } else {
            deleteJavaDoc(dir.getAbsolutePath(), dest);
        }
        if (toZip) {
            File destinationZip = new File(dest);
            ZipUtils.zip(destinationZip, new File(destinationZip.getParent(),
                    destinationZip.getName().concat(ZIP_EXTENSION)));
        }
    }

    /**
     * Generate java doc
     *
     * @param srcPath          {@link String}
     * @param destinationFile  {@link String}
     * @param deleteOldJavaDoc boolean
     * @throws IOException Exception levée si erreur.
     */
    public void generateJavaDoc(String srcPath, String destinationFile, boolean deleteOldJavaDoc) throws IOException {
        File srcFile = new File(srcPath);
        CompilationUnit compilationUnit = JavaParser.parse(srcFile);
        String javaDocAsString = generateJavaDocAsString(compilationUnit.toString(), deleteOldJavaDoc);
        File destFile = getDestination(destinationFile, srcFile, compilationUnit);
        FileUtils.write(destFile, javaDocAsString, UTF_8);
        logger.info("generated javadoc for  file {}", srcPath);
    }

    /**
     * Generate java doc for type declaration
     *
     * @param enumDeclaration {@link TypeDeclaration}
     */
    @SuppressWarnings("unchecked")
    private void generateJavaDocForTypeDeclaration(TypeDeclaration enumDeclaration) {
        if (!enumDeclaration.hasJavaDocComment()) {
            JavadocDescription javadocDescription = new JavadocDescription();
            Javadoc javadoc = new Javadoc(javadocDescription);
            String text = readFromProperties(TODO_CLASS_TEXT);
            if (enumDeclaration.isEnumDeclaration()) {
                text = readFromProperties(TODO_ENUM_TEXT);
            }
            JavadocSnippet element = new JavadocSnippet(text);
            javadocDescription.addElement(element);
            String author = readFromProperties(AUTHOR);
            String since = readFromProperties(SINCE_VERSION);
            if (author != null)
                javadoc.addBlockTag(new FormattedJavadocBlockTag(JavadocBlockTag.Type.AUTHOR, author));
            if (since != null)
                javadoc.addBlockTag(new FormattedJavadocBlockTag(JavadocBlockTag.Type.SINCE, since));
            enumDeclaration.setJavadocComment(javadoc);
        } else {
            Optional<Javadoc> optionalJavaDoc = enumDeclaration.getJavadoc();
            optionalJavaDoc.ifPresent(javadoc -> {
                javadoc.getBlockTags().removeIf(blockTag ->
                        JavadocBlockTag.Type.SINCE.equals(blockTag.getType()));
                javadoc.addBlockTag(new FormattedJavadocBlockTag(JavadocBlockTag.Type.SINCE,
                        readFromProperties(SINCE_VERSION)));
                javadoc.addBlockTag(new FormattedJavadocBlockTag(JavadocBlockTag.Type.AUTHOR,
                        readFromProperties(AUTHOR)));
                enumDeclaration.setJavadocComment(javadoc);
            });
        }
    }

    /**
     * Generate java doc as string
     *
     * @param src              {@link String}
     * @param deleteOldJavaDoc boolean
     * @return String
     * @throws IOException Exception levée si erreur.
     */
    public String generateJavaDocAsString(String src, boolean deleteOldJavaDoc) throws IOException {
        return generateJavaDocAsString(JavaParser.parse(src), deleteOldJavaDoc);
    }

    /**
     * Generate java doc as string
     *
     * @param compilationUnit  {@link CompilationUnit}
     * @param deleteOldJavaDoc boolean
     * @return String
     * @throws IOException Exception levée si erreur.
     */
    public String generateJavaDocAsString(CompilationUnit compilationUnit, boolean deleteOldJavaDoc) throws IOException {
        if (deleteOldJavaDoc) {
            deleteOldJavaDoc(compilationUnit);
            logger.info("deleted javadoc for  source code {}", compilationUnit.toString());
        }
        compilationUnit.findAll(TypeDeclaration.class).forEach(this::generateJavaDocForTypeDeclaration);
        compilationUnit.findAll(ConstructorDeclaration.class).forEach(this::generateConstructorJavaDoc);
        compilationUnit.findAll(FieldDeclaration.class).forEach(this::generateFieldJavaDoc);
        compilationUnit.findAll(MethodDeclaration.class).forEach(this::generateMethodJavaDoc);
        logger.info("generated javadoc for  source code");
        return compilationUnit.toString();
    }

    /**
     * @param destinationFile {@link String}
     * @param srcFile         {@link File}
     * @param compilationUnit {@link CompilationUnit}
     * @return File
     */
    private File getDestination(String destinationFile, File srcFile, CompilationUnit compilationUnit) {
        return compilationUnit.getPackageDeclaration().map(packageDeclaration ->
                new File(destinationFile,
                        Utils.convertPackageDeclarationToPath(packageDeclaration.getName()
                                .asString()) + File.separator + srcFile.getName())).orElseGet(()
                -> new File(destinationFile));
    }

    /**
     * Delete java doc
     *
     * @param srcPath         {@link String}
     * @param destinationFile {@link String}
     * @throws IOException Exception levée si erreur.
     */
    public void deleteJavaDoc(String srcPath, String destinationFile) throws IOException {
        File srcFile = new File(srcPath);
        CompilationUnit compilationUnit = JavaParser.parse(srcFile);
        File destFile = getDestination(destinationFile, srcFile, compilationUnit);
        deleteOldJavaDoc(compilationUnit);
        FileUtils.write(destFile, compilationUnit.toString(), UTF_8);
        logger.info("deleted javadoc for  file {}", srcPath);
    }

    /**
     * Delete old java doc
     *
     * @param compilationUnit {@link CompilationUnit}
     */
    private void deleteOldJavaDoc(CompilationUnit compilationUnit) {
        compilationUnit.findAll(TypeDeclaration.class).forEach(TypeDeclaration::removeJavaDocComment);
        compilationUnit.findAll(ConstructorDeclaration.class).forEach(ConstructorDeclaration::removeJavaDocComment);
        compilationUnit.findAll(FieldDeclaration.class).forEach(FieldDeclaration::removeJavaDocComment);
        compilationUnit.findAll(MethodDeclaration.class).forEach(MethodDeclaration::removeJavaDocComment);
    }

    /**
     * Delete java doc
     *
     * @param src {@link String}
     * @return String
     */
    public String deleteJavaDoc(String src) {
        CompilationUnit compilationUnit = JavaParser.parse(src);
        deleteOldJavaDoc(compilationUnit);
        return compilationUnit.toString();
    }

    /**
     * Generate constructor java doc
     *
     * @param constructorDeclaration {@link ConstructorDeclaration}
     */
    private void generateConstructorJavaDoc(ConstructorDeclaration constructorDeclaration) {
        JavadocDescription javadocDescription = new JavadocDescription();
        Javadoc javadoc = new Javadoc(javadocDescription);
        NodeList<Parameter> constructParams = constructorDeclaration.getParameters();
        String leadingComment;
        if (constructParams.isEmpty() && (constructorDeclaration.getBody().getStatements().isEmpty())) {
            leadingComment = readFromProperties(DEFAULT_CONSTR_COMMENT);
            constructorDeclaration.getBody()
                    .addOrphanComment(new LineComment(leadingComment));
        } else {
            leadingComment = String.format(readFromProperties(CONSTR_COMMENT),
                    constructorDeclaration.getName().asString());
        }
        JavadocSnippet element = new JavadocSnippet(leadingComment);
        javadocDescription.addElement(element);
        if (!constructorDeclaration.getJavadoc().isPresent()) {
            addParamsToJavaDoc(constructParams, javadoc);
            addExceptionsToJavaDoc(constructorDeclaration.getThrownExceptions(), javadoc);
            constructorDeclaration.setJavadocComment(javadoc);
        }
    }

    /**
     *
     * @param constructParams
     * @param javadoc
     */
    private void addParamsToJavaDoc(NodeList<Parameter> constructParams, Javadoc javadoc) {
        for (Parameter parameter : constructParams) {
            FormattedJavadocBlockTag javadocBlockTag = new FormattedJavadocBlockTag(JavadocBlockTag.Type.PARAM,
                    String.format("%s{@link %s}", parameter.getName().asString(),
                            parameter.getType().asString()));
            javadoc.addBlockTag(javadocBlockTag);
        }
    }

    private void addExceptionsToJavaDoc(NodeList<ReferenceType> thrownExceptions, Javadoc javadoc) {
        for (ReferenceType thrownException : thrownExceptions) {
            FormattedJavadocBlockTag javadocBlockTag = new FormattedJavadocBlockTag(JavadocBlockTag.Type.THROWS,
                    String.format(readFromProperties(EXCEPTION_COMMENT),
                            thrownException.asReferenceType()));
            javadoc.addBlockTag(javadocBlockTag);
        }
    }

    /**
     * Generate field java doc
     *
     * @param fieldDeclaration {@link FieldDeclaration}
     */
    private void generateFieldJavaDoc(FieldDeclaration fieldDeclaration) {
        if (fieldDeclaration.getVariables().isNonEmpty()) {
            JavadocDescription javadocDescription = new JavadocDescription();
            Javadoc javadoc = new Javadoc(javadocDescription);
            String javaDocText;
            JavadocSnippet javadocSnippet;
            VariableDeclarator variableDeclarator = fieldDeclaration.getVariables().get(0);
            SimpleName fieldName = variableDeclarator.getName();
            VariableDeclarator variable = fieldDeclaration.getVariable(0);
            Type type = variable.getType();
            String valueText;
            Object assignedValue;
            if (fieldDeclaration.isStatic() && fieldDeclaration.isFinal() && variable.getInitializer().isPresent()) {
                javaDocText = readFromProperties(CONSTANT_COMMENT);
                String typeText = SINGLE_STRING_FORMAT;
                if (!type.isPrimitiveType()) {
                    typeText = readFromProperties(LINK_COMMENT);
                }
                if (type.isPrimitiveType() || STRING.equals(type.asString())) {
                    valueText = readFromProperties(FIELD_VALUE_COMMENT);
                    assignedValue = fieldName;
                } else {
                    assignedValue = StringUtils.EMPTY;
                    Optional<Expression> initializer = variable.getInitializer();
                    if (initializer.isPresent()) {
                        assignedValue = initializer.get().toString();
                    }
                    valueText = SINGLE_STRING_FORMAT;
                }
                javadocSnippet = new JavadocSnippet(String.format(javaDocText, fieldName, String.format(typeText, type.asString()), String.format(valueText, assignedValue)));
            } else {
                javaDocText = readFromProperties(FIELD_COMMENT);
                javadocSnippet = new JavadocSnippet(String.format(javaDocText, fieldName));
            }
            javadocDescription.addElement(javadocSnippet);
            fieldDeclaration.setJavadocComment(javadoc);
        }
    }

    /**
     * Generate method java doc
     *
     * @param methodDeclaration {@link MethodDeclaration}
     */
    private void generateMethodJavaDoc(MethodDeclaration methodDeclaration) {
        JavadocDescription javadocDescription = new JavadocDescription();
        Javadoc javadoc = new Javadoc(javadocDescription);
        JavadocSnippet element;
        String methodName = methodDeclaration.getName().asString();
        boolean isSetter = methodName.startsWith("set");
        boolean isGetter = methodName.startsWith("get");
        boolean isIs = methodName.startsWith("is");

        NodeList<Parameter> parameters = methodDeclaration.getParameters();
        String paramFormat = "%s {@link %s}";
        JavadocSnippet inheritDocSnippet = new JavadocSnippet(readFromProperties(INHERIT_DOC));
        String methodReturnType = methodDeclaration.getType().asString();
        if (!methodDeclaration.hasJavaDocComment()) {
            if (methodDeclaration.isAnnotationPresent(Override.class)) {
                javadocDescription.addElement(inheritDocSnippet);
                methodDeclaration.setJavadocComment(javadoc);
            } else {
                if (isSetter || isGetter || isIs) {
                    element = new JavadocSnippet("");
                } else if (Utils.isCamelCase(methodName)) {
                    element = new JavadocSnippet(StringUtils.capitalize(StringUtils.lowerCase(
                            Utils.unCamelCase(methodName, " "))));
                } else {
                    element = new JavadocSnippet(readFromProperties(TODO_METHOD_TEXT));
                }
                javadocDescription.addElement(element);
                for (Parameter parameter : parameters) {
                    JavadocBlockTag blockTag;
                    if (parameter.getType().isPrimitiveType()) {
                        paramFormat = "%s %s ";
                    }
                    String paramName = parameter.getName().asString();
                    if (isSetter) {
                        blockTag = new FormattedJavadocBlockTag(JavadocBlockTag.Type.PARAM,
                                String.format(readFromProperties(SETTER_COMMENT),
                                        paramName,
                                        Utils.toLowerCaseFirstLetter(methodName.substring(3))));
                    } else {
                        blockTag = new FormattedJavadocBlockTag(JavadocBlockTag.Type.PARAM,
                                String.format(paramFormat, paramName,
                                        parameter.getType().asString()));
                    }
                    javadoc.addBlockTag(blockTag);
                }
                if (!methodDeclaration.getType().isVoidType()) {
                    JavadocBlockTag javadocBlockTag;
                    if (isGetter) {
                        javadocBlockTag = new FormattedJavadocBlockTag(JavadocBlockTag.Type.RETURN,
                                String.format(readFromProperties(ATTRIBUT_COMMENT_FORMAT),
                                        Utils.toLowerCaseFirstLetter(methodName.substring(3))));
                    } else if (isIs) {
                        javadocBlockTag = new FormattedJavadocBlockTag(JavadocBlockTag.Type.RETURN,
                                String.format(readFromProperties(ATTRIBUT_COMMENT_FORMAT),
                                        Utils.toLowerCaseFirstLetter(methodName.substring(2))));
                    } else {
                        javadocBlockTag = new FormattedJavadocBlockTag(JavadocBlockTag.Type.RETURN,
                                methodReturnType);
                    }
                    javadoc.addBlockTag(javadocBlockTag);
                }
                addExceptionsToJavaDoc(methodDeclaration.getThrownExceptions(), javadoc);
                methodDeclaration.setJavadocComment(javadoc);
            }
        } else if (methodDeclaration.isAnnotationPresent(Override.class)) {
            methodDeclaration.removeJavaDocComment();
            // if the method has an old javadoc, replace it by this one :
            javadocDescription.addElement(inheritDocSnippet);
            methodDeclaration.setJavadocComment(javadoc);
        } else {
            Javadoc oldJavaDoc = javadoc;
            if (methodDeclaration.getJavadoc().isPresent()) {
                oldJavaDoc = methodDeclaration.getJavadoc().get();
            }
            oldJavaDoc.getBlockTags().clear();
            for (Parameter parameter : parameters) {
                if (parameter.getType().isPrimitiveType()) {
                    paramFormat = "%s %s ";
                }
                if (isSetter) {
                    oldJavaDoc.addBlockTag(new FormattedJavadocBlockTag(JavadocBlockTag.Type.PARAM,
                            String.format(readFromProperties(SETTER_COMMENT),
                                    parameter.getName().asString(),
                                    Utils.toLowerCaseFirstLetter(methodName.substring(3)))));
                } else {
                    JavadocBlockTag blockTag = new FormattedJavadocBlockTag(JavadocBlockTag.Type.PARAM, String.format(paramFormat, parameter.getName().asString(), parameter.getType().asString()));
                    oldJavaDoc.addBlockTag(blockTag);
                }
            }
            if (!methodDeclaration.getType().isVoidType()) {
                if (isGetter) {
                    oldJavaDoc.addBlockTag(new FormattedJavadocBlockTag(JavadocBlockTag.Type.RETURN,
                            String.format(readFromProperties(ATTRIBUT_COMMENT_FORMAT),
                                    Utils.toLowerCaseFirstLetter(methodName.substring(3)))));
                } else if (isIs) {
                    oldJavaDoc.addBlockTag(new FormattedJavadocBlockTag(JavadocBlockTag.Type.RETURN,
                            String.format(readFromProperties(ATTRIBUT_COMMENT_FORMAT),
                                    Utils.toLowerCaseFirstLetter(methodName.substring(2)))));
                } else
                    oldJavaDoc.addBlockTag(new FormattedJavadocBlockTag(JavadocBlockTag.Type.RETURN,
                            methodReturnType));
            }
            methodDeclaration.setJavadocComment(oldJavaDoc);
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
}