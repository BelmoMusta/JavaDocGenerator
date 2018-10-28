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
 * Utilities for generating the javadoc of java classes.
 *
 * @since lot 1.2
 */
public class JavaDocGenerator {

    /**
     * La constante {@link #ATTRIBUT_COMMENT_FORMAT} de type {@link String} ayant la valeur {@value #ATTRIBUT_COMMENT_FORMAT}.
     */
    private static final String ATTRIBUT_COMMENT_FORMAT = "ATTRIBUT_COMMENT_FORMAT";

    /**
     * La constante {@link #UTF_8} de type {@link String} ayant la valeur {@value #UTF_8}.
     */
    private static final String UTF_8 = "UTF-8";

    /**
     * La constante {@link #TODO_METHOD_TEXT} de type {@link String} ayant la valeur {@value #TODO_METHOD_TEXT}.
     */
    private static final String TODO_METHOD_TEXT = "TODO_METHOD_TEXT";

    /**
     * La constante {@link #INHERIT_DOC} de type {@link String} ayant la valeur {@value #INHERIT_DOC}.
     */
    private static final String INHERIT_DOC = "INHERIT_DOC";

    /**
     * La constante {@link #SETTER_COMMENT} de type {@link String} ayant la valeur {@value #SETTER_COMMENT}.
     */
    private static final String SETTER_COMMENT = "SETTER_COMMENT";

    /**
     * La constante {@link #FIELD_COMMENT} de type {@link String} ayant la valeur {@value #FIELD_COMMENT}.
     */
    private static final String FIELD_COMMENT = "FIELD_COMMENT";

    /**
     * La constante {@link #CONSTANT_COMMENT} de type {@link String} ayant la valeur {@value #CONSTANT_COMMENT}.
     */
    private static final String CONSTANT_COMMENT = "CONSTANT_COMMENT";

    /**
     * La constante {@link #LINK_COMMENT} de type {@link String} ayant la valeur {@value #LINK_COMMENT}.
     */
    private static final String LINK_COMMENT = "LINK_COMMENT";

    /**
     * La constante {@link #STRING} de type {@link String} ayant la valeur {@value #STRING}.
     */
    private static final String STRING = "String";

    /**
     * La constante {@link #FIELD_VALUE_COMMENT} de type {@link String} ayant la valeur {@value #FIELD_VALUE_COMMENT}.
     */
    private static final String FIELD_VALUE_COMMENT = "FIELD_VALUE_COMMENT";

    /**
     * La constante {@link #SINGLE_STRING_FORMAT} de type {@link String} ayant la valeur {@value #SINGLE_STRING_FORMAT}.
     */
    private static final String SINGLE_STRING_FORMAT = "%s";

    /**
     * La constante {@link #CONSTR_COMMENT} de type {@link String} ayant la valeur {@value #CONSTR_COMMENT}.
     */
    private static final String CONSTR_COMMENT = "CONSTR_COMMENT";

    /**
     * La constante {@link #DEFAULT_CONSTR_COMMENT} de type {@link String} ayant la valeur {@value #DEFAULT_CONSTR_COMMENT}.
     */
    private static final String DEFAULT_CONSTR_COMMENT = "DEFAULT_CONSTR_COMMENT";

    /**
     * La constante {@link #EXCEPTION_COMMENT} de type {@link String} ayant la valeur {@value #EXCEPTION_COMMENT}.
     */
    private static final String EXCEPTION_COMMENT = "EXCEPTION_COMMENT";

    /**
     * La constante {@link #JAVA_EXTENSION} de type {@link String} ayant la valeur {@value #JAVA_EXTENSION}.
     */
    private static final String JAVA_EXTENSION = "java";

    /**
     * La constante {@link #ZIP_EXTENSION} de type {@link String} ayant la valeur {@value #ZIP_EXTENSION}.
     */
    private static final String ZIP_EXTENSION = ".zip";

    /**
     * La constante {@link #SINCE_VERSION} de type {@link String} ayant la valeur {@value #SINCE_VERSION}.
     */
    private static final String SINCE_VERSION = "SINCE_VERSION";
    /**
     * La constante {@link #AUTHOR} de type {@link String} ayant la valeur {@value #AUTHOR}.
     */
    private static final String AUTHOR = "AUTHOR";
    private static final String APPLICATION_PROPERTIES = "application.properties";

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
    final MustaLogger logger;

    /**
     */
    public JavaDocGenerator() {
        logger = new MustaLogger(getClass());
        logger.info("static");
        loadProperties(null);
    }

    /**
     * Read from properties
     *
     * @param key {@link String}
     * @return String
     */
    public String readFromProperties(String key) {
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
            ZipUtils.zip(destinationZip, new File(destinationZip.getParent(), destinationZip.getName().concat(".zip")));
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
     */
    public void generateJavaDocForAllClasses(String directory, String dest, boolean toZip, boolean deleteOldJavadoc) throws IOException {
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
    /**
     * Delete java doc for all classes
     *
     * @param directory {@link String}
     * @param dest      {@link String}
     * @param toZip     boolean
     * @throws Exception Exception levée si erreur.
     */
    private void deleteJavaDocForAllClasses(String directory, String dest, boolean toZip) throws Exception {
        // NOSONAR
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
            ZipUtils.zip(destinationZip, new File(destinationZip.getParent(), destinationZip.getName().concat(ZIP_EXTENSION)));
        }
    }

    /**
     * Generate java doc
     *
     * @param srcPath          {@link String}
     * @param destinationFile  {@link String}
     * @param deleteOldJavaDoc boolean
     */
    public void generateJavaDoc(String srcPath, String destinationFile, boolean deleteOldJavaDoc) throws IOException {
        File srcFile = new File(srcPath);
        CompilationUnit compilationUnit = JavaParser.parse(srcFile);
        File destFile = getDestination(destinationFile, srcFile, compilationUnit);
        if (deleteOldJavaDoc) {
            deleteOldJavaDoc(compilationUnit);
            logger.info("deleted javadoc for  file {}", srcPath);
        }
        compilationUnit.findAll(ClassOrInterfaceDeclaration.class).forEach(this::generateClassJavaDoc);
        compilationUnit.findAll(ConstructorDeclaration.class).forEach(this::generateConstructorJavaDoc);
        compilationUnit.findAll(FieldDeclaration.class).forEach(this::generateFieldJavaDoc);
        compilationUnit.findAll(MethodDeclaration.class).forEach(this::generateMethodJavaDoc);
        FileUtils.write(destFile, compilationUnit.toString(), UTF_8);
        logger.info("generated javadoc for  file {}", srcPath);
    }

    public String generateJavaDocAsString(String src, boolean deleteOldJavaDoc) throws IOException {

        CompilationUnit compilationUnit = JavaParser.parse(src);
        if (deleteOldJavaDoc) {
            deleteOldJavaDoc(compilationUnit);
            logger.info("deleted javadoc for  source code {}", src);
        }
        compilationUnit.findAll(ClassOrInterfaceDeclaration.class).forEach(this::generateClassJavaDoc);
        compilationUnit.findAll(ConstructorDeclaration.class).forEach(this::generateConstructorJavaDoc);
        compilationUnit.findAll(FieldDeclaration.class).forEach(this::generateFieldJavaDoc);
        compilationUnit.findAll(MethodDeclaration.class).forEach(this::generateMethodJavaDoc);
        logger.info("generated javadoc for  source code");
        return compilationUnit.toString();
    }

    /**
     * Delete setters for lists
     *
     * @param srcPath         {@link String}
     * @param destinationFile {@link String}
     */
    public void deleteSettersForLists(String srcPath, String destinationFile) throws IOException {
        File srcFile = new File(srcPath);
        CompilationUnit compilationUnit = JavaParser.parse(srcFile);
        File destFile = getDestination(destinationFile, srcFile, compilationUnit);
        compilationUnit.findAll(MethodDeclaration.class).stream().filter(methodDeclaration -> {
            boolean paramListExists = false;
            if (methodDeclaration.getName().asString().startsWith("set")) {
                for (Parameter parameter : methodDeclaration.getParameters()) {
                    if (parameter.getType().asString().contains("List<")) {
                        paramListExists = true;
                        break;
                    }
                }
            }
            return paramListExists;
        }).forEach(MethodDeclaration::remove);
        FileUtils.write(destFile, compilationUnit.toString(), UTF_8);
        System.out.println(compilationUnit);
    }

    /**
     * Remove volatile modifier
     *
     * @param srcPath         {@link String}
     * @param destinationFile {@link String}
     */
    public void removeVolatileModifier(String srcPath, String destinationFile) throws IOException {
        File srcFile = new File(srcPath);
        CompilationUnit compilationUnit = JavaParser.parse(srcFile);
        File destFile = getDestination(destinationFile, srcFile, compilationUnit);
        compilationUnit.findAll(FieldDeclaration.class).forEach(fieldDeclaration -> {
            EnumSet<Modifier> modifiers = fieldDeclaration.getModifiers();
            modifiers.remove(Modifier.VOLATILE);
        });
        FileUtils.write(destFile, compilationUnit.toString(), UTF_8);
        System.out.println(compilationUnit);
    }

    /**
     * Add nosonar
     *
     * @param srcPath         {@link String}
     * @param destinationFile {@link String}
     * @param justification   {@link String}
     */
    public void addNOSONAR(String srcPath, String destinationFile, String justification) throws IOException {
        File srcFile = new File(srcPath);
        CompilationUnit compilationUnit = JavaParser.parse(srcFile);
        File destFile = getDestination(destinationFile, srcFile, compilationUnit);
        compilationUnit.findAll(MethodDeclaration.class).forEach(methodDeclaration -> {
            if (methodDeclaration.getName().asString().startsWith("get") && methodDeclaration.getType().asString().contains("List<")) {
                BlockStmt blockStmt = methodDeclaration.getBody().get();
                for (Statement statement : blockStmt.getStatements()) {
                    if (statement.isReturnStmt()) {
                        statement.setLineComment(String.format("NOSONAR :%s", justification));
                    }
                }
            }
        });
        FileUtils.write(destFile, compilationUnit.toString(), UTF_8);
        System.out.println(compilationUnit);
    }

    /**
     * @param destinationFile {@link String}
     * @param srcFile         {@link File}
     * @param compilationUnit {@link CompilationUnit}
     * @return File
     */
    private File getDestination(String destinationFile, File srcFile, CompilationUnit compilationUnit) {
        return compilationUnit.getPackageDeclaration().map(packageDeclaration -> new File(destinationFile, Utils.convertPackageDeclarationToPath(packageDeclaration.getName().asString()) + File.separator + srcFile.getName())).orElseGet(() -> new File(destinationFile));
    }

    /**
     * @param s {@link String}
     * @return boolean
     */
    private boolean isCamelCase(String s) {
        return s != null && s.matches("[a-z]+[A-Z\\d]+\\w+");
    }

    /**
     * Delete java doc
     *
     * @param srcPath         {@link String}
     * @param destinationFile {@link String}
     */
    public void deleteJavaDoc(String srcPath, String destinationFile) throws IOException {
        File srcFile = new File(srcPath);
        CompilationUnit compilationUnit = JavaParser.parse(srcFile);
        File destFile = getDestination(destinationFile, srcFile, compilationUnit);
        compilationUnit.findAll(ClassOrInterfaceDeclaration.class).forEach(ClassOrInterfaceDeclaration::removeJavaDocComment);
        compilationUnit.findAll(ConstructorDeclaration.class).forEach(ConstructorDeclaration::removeJavaDocComment);
        compilationUnit.findAll(FieldDeclaration.class).forEach(FieldDeclaration::removeJavaDocComment);
        compilationUnit.findAll(MethodDeclaration.class).forEach(MethodDeclaration::removeJavaDocComment);
        FileUtils.write(destFile, compilationUnit.toString(), UTF_8);
        System.out.println(compilationUnit);
        logger.info("deleted javadoc for  file {}", srcPath);
    }

    /**
     * Delete old java doc
     *
     * @param compilationUnit {@link CompilationUnit}
     * @throws IOException Exception levée si erreur.
     */
    public void deleteOldJavaDoc(CompilationUnit compilationUnit) throws IOException {
        compilationUnit.findAll(ClassOrInterfaceDeclaration.class).forEach(ClassOrInterfaceDeclaration::removeJavaDocComment);
        compilationUnit.findAll(ConstructorDeclaration.class).forEach(ConstructorDeclaration::removeJavaDocComment);
        compilationUnit.findAll(FieldDeclaration.class).forEach(FieldDeclaration::removeJavaDocComment);
        compilationUnit.findAll(MethodDeclaration.class).forEach(MethodDeclaration::removeJavaDocComment);
    }

    /**
     * Generate class java doc
     *
     * @param classDef {@link ClassOrInterfaceDeclaration}
     */
    private void generateClassJavaDoc(ClassOrInterfaceDeclaration classDef) {
        if (!classDef.hasJavaDocComment()) {
            JavadocDescription javadocDescription = new JavadocDescription();
            Javadoc javadoc = new Javadoc(javadocDescription);
            String text = "TODO : Compléter la description de cette classe ";
            JavadocSnippet element = new JavadocSnippet(text);
            javadocDescription.addElement(element);

            String author = readFromProperties(AUTHOR);
            String since = readFromProperties(SINCE_VERSION);

            if (author != null) javadoc.addBlockTag(new FormattedJavadocBlockTag(JavadocBlockTag.Type.AUTHOR, author));
            if (since != null) javadoc.addBlockTag(new FormattedJavadocBlockTag(JavadocBlockTag.Type.SINCE, since));


            classDef.setJavadocComment(javadoc);
        } else {
            classDef.getJavadoc().ifPresent(javadoc -> {
                javadoc.getBlockTags().removeIf(blockTag -> JavadocBlockTag.Type.SINCE.equals(blockTag.getType()));
                javadoc.addBlockTag(new FormattedJavadocBlockTag(JavadocBlockTag.Type.SINCE, readFromProperties(SINCE_VERSION)));
                javadoc.addBlockTag(new FormattedJavadocBlockTag(JavadocBlockTag.Type.AUTHOR, readFromProperties(AUTHOR)));
                classDef.setJavadocComment(javadoc);
            });
        }
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
        if (constructParams.isEmpty() && constructorDeclaration.getBody().getOrphanComments().isEmpty()) {
            leadingComment = readFromProperties(DEFAULT_CONSTR_COMMENT);
            constructorDeclaration.getBody().addOrphanComment(new LineComment(leadingComment));
        } else {
            leadingComment = String.format(readFromProperties(CONSTR_COMMENT), constructorDeclaration.getName().asString());
        }
        JavadocSnippet element = new JavadocSnippet(leadingComment);
        javadocDescription.addElement(element);
        if (!constructorDeclaration.getJavadoc().isPresent()) {
            for (Parameter parameter : constructParams) {
                FormattedJavadocBlockTag javadocBlockTag = new FormattedJavadocBlockTag(JavadocBlockTag.Type.PARAM, parameter.getName().asString() + "{@link " + parameter.getType().asString() + "}");
                javadoc.addBlockTag(javadocBlockTag);
            }
            NodeList<ReferenceType> thrownExceptions = constructorDeclaration.getThrownExceptions();
            for (ReferenceType thrownException : thrownExceptions) {
                FormattedJavadocBlockTag javadocBlockTag = new FormattedJavadocBlockTag(JavadocBlockTag.Type.THROWS, thrownException.asReferenceType() + " exception levée si erreur");
                javadoc.addBlockTag(javadocBlockTag);
            }
            constructorDeclaration.setJavadocComment(javadoc);
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
        boolean isSetter = methodDeclaration.getName().asString().startsWith("set");
        boolean isGetter = methodDeclaration.getName().asString().startsWith("get");
        boolean isIs = methodDeclaration.getName().asString().startsWith("is");
        NodeList<Parameter> parameters = methodDeclaration.getParameters();
        String paramFormat = "%s {@link %s}";
        JavadocSnippet inheritDocSnippet = new JavadocSnippet(readFromProperties(INHERIT_DOC));
        if (!methodDeclaration.hasJavaDocComment()) {
            if (methodDeclaration.isAnnotationPresent(Override.class)) {
                javadocDescription.addElement(inheritDocSnippet);
                methodDeclaration.setJavadocComment(javadoc);
            } else {
                if (isSetter || isGetter || isIs) {
                    element = new JavadocSnippet("");
                } else if (isCamelCase(methodDeclaration.getName().asString())) {
                    element = new JavadocSnippet(StringUtils.capitalize(StringUtils.lowerCase(Utils.unCamelCase(methodDeclaration.getName().asString(), " "))));
                } else {
                    element = new JavadocSnippet(readFromProperties(TODO_METHOD_TEXT));
                }
                javadocDescription.addElement(element);
                for (Parameter parameter : parameters) {
                    JavadocBlockTag blockTag;
                    if (parameter.getType().isPrimitiveType()) {
                        paramFormat = "%s %s ";
                    }
                    if (isSetter) {
                        blockTag = new FormattedJavadocBlockTag(JavadocBlockTag.Type.PARAM, String.format(readFromProperties(SETTER_COMMENT), parameter.getName().asString(), Utils.toLowerCaseFirstLetter(methodDeclaration.getName().asString().substring(3))));
                    } else {
                        blockTag = new FormattedJavadocBlockTag(JavadocBlockTag.Type.PARAM, String.format(paramFormat, parameter.getName().asString(), parameter.getType().asString()));
                    }
                    javadoc.addBlockTag(blockTag);
                }
                if (!methodDeclaration.getType().isVoidType()) {
                    JavadocBlockTag javadocBlockTag;
                    if (isGetter) {
                        javadocBlockTag = new FormattedJavadocBlockTag(JavadocBlockTag.Type.RETURN, String.format(readFromProperties(ATTRIBUT_COMMENT_FORMAT), Utils.toLowerCaseFirstLetter(methodDeclaration.getName().asString().substring(3))));
                    } else if (isIs) {
                        javadocBlockTag = new FormattedJavadocBlockTag(JavadocBlockTag.Type.RETURN, String.format(readFromProperties(ATTRIBUT_COMMENT_FORMAT), Utils.toLowerCaseFirstLetter(methodDeclaration.getName().asString().substring(2))));
                    } else {
                        javadocBlockTag = new FormattedJavadocBlockTag(JavadocBlockTag.Type.RETURN, methodDeclaration.getType().asString());
                    }
                    javadoc.addBlockTag(javadocBlockTag);
                }
                NodeList<ReferenceType> thrownExceptions = methodDeclaration.getThrownExceptions();
                for (ReferenceType thrownException : thrownExceptions) {
                    javadoc.addBlockTag(new FormattedJavadocBlockTag(JavadocBlockTag.Type.THROWS, String.format(readFromProperties(EXCEPTION_COMMENT), thrownException.asReferenceType())));
                }
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
                    oldJavaDoc.addBlockTag(new FormattedJavadocBlockTag(JavadocBlockTag.Type.PARAM, String.format(readFromProperties(SETTER_COMMENT), parameter.getName().asString(), Utils.toLowerCaseFirstLetter(methodDeclaration.getName().asString().substring(3)))));
                } else {
                    JavadocBlockTag blockTag = new FormattedJavadocBlockTag(JavadocBlockTag.Type.PARAM, String.format(paramFormat, parameter.getName().asString(), parameter.getType().asString()));
                    oldJavaDoc.addBlockTag(blockTag);
                }
            }
            if (!methodDeclaration.getType().isVoidType()) {
                if (isGetter) {
                    oldJavaDoc.addBlockTag(new FormattedJavadocBlockTag(JavadocBlockTag.Type.RETURN, String.format(readFromProperties(ATTRIBUT_COMMENT_FORMAT), Utils.toLowerCaseFirstLetter(methodDeclaration.getName().asString().substring(3)))));
                } else if (isIs) {
                    oldJavaDoc.addBlockTag(new FormattedJavadocBlockTag(JavadocBlockTag.Type.RETURN, String.format(readFromProperties(ATTRIBUT_COMMENT_FORMAT), Utils.toLowerCaseFirstLetter(methodDeclaration.getName().asString().substring(2)))));
                } else
                    oldJavaDoc.addBlockTag(new FormattedJavadocBlockTag(JavadocBlockTag.Type.RETURN, methodDeclaration.getType().asString()));
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
