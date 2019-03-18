package musta.belmo.javacodegenerator.service;

import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.NodeList;
import com.github.javaparser.ast.body.*;
import com.github.javaparser.ast.comments.LineComment;
import com.github.javaparser.ast.expr.Expression;
import com.github.javaparser.ast.expr.SimpleName;
import com.github.javaparser.ast.stmt.BlockStmt;
import com.github.javaparser.ast.type.ReferenceType;
import com.github.javaparser.ast.type.Type;
import com.github.javaparser.javadoc.Javadoc;
import com.github.javaparser.javadoc.JavadocBlockTag;
import com.github.javaparser.javadoc.description.JavadocDescription;
import com.github.javaparser.javadoc.description.JavadocSnippet;
import musta.belmo.javacodecore.CodeUtils;
import musta.belmo.javacodecore.Utils;
import musta.belmo.javacodecore.ZipUtils;
import musta.belmo.javacodecore.logger.Level;
import musta.belmo.javacodecore.logger.MustaLogger;
import musta.belmo.javacodegenerator.FormattedJavadocBlockTag;
import musta.belmo.javacodegenerator.service.exception.CompilationException;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;

import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * TODO: Compléter la description de cette classe
 *
 * @author toBeSpecified
 * @version 1.0
 * @since 1.0.0.SNAPSHOT
 */
public class JavaDocGenerator extends AbstractCodeService {

    private static final String ALL_CLASSES_DOC = "generateJavaDocForAllClasses : directory {}\n destination {}";
    private static final String GENERATION_DONE = "generateJavaDocForAllClasses : done";

    /**
     * Classe pour l'initialisation à la demande de la classe {@link JavaDocGenerator}.
     */
    private static class JavaDocGeneratorHolder {
        /**
         * L'unique instance de la classe {@link JavaDocGenerator}.
         */
        static final JavaDocGenerator INSTANCE = new JavaDocGenerator();

        /**
         * Constructeur par défaut de la classe {@link JavaDocGeneratorHolder}.
         */
        private JavaDocGeneratorHolder() {
            // Constructeur par défaut
        }
    }

    private JavaDocDeleter deleter;


    /**
     * Constructeur de la classe JavaDocGenerator
     */
    private JavaDocGenerator() {
        deleter = new JavaDocDeleter();
        logger = new MustaLogger(getClass());
        loadProperties(null);
        // Constructeur par défaut
    }

    public static JavaDocGenerator getInstance() {
        return JavaDocGeneratorHolder.INSTANCE;
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
    public void generateJavaDocForAllClasses(File directory, File dest, boolean toZip, boolean deleteOldJavadoc) throws IOException, CompilationException {
        logger.logCurrentMethod(Level.DEBUG, directory, dest);
        logger.info(ALL_CLASSES_DOC, directory, dest);
        File destinationZip = new File(dest.getAbsolutePath());
        if (directory.isDirectory()) {
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
        logger.info(GENERATION_DONE);
    }

    /**
     * Generate java doc for all classes
     *
     * @param directory @link File}
     * @throws IOException Exception levée si erreur.
     */
    public void generateJavaDocForAllClasses(File directory) throws IOException, CompilationException {
        logger.logCurrentMethod(Level.DEBUG, directory, directory);
        logger.info(ALL_CLASSES_DOC, directory, directory);
        if (directory.isDirectory()) {
            Collection<File> files = FileUtils.listFiles(directory, new String[]{JAVA_EXTENSION}, true);
            for (File file : files) {
                generateJavaDocInPlace(file.getAbsolutePath(), false);
            }
        } else {
            generateJavaDocInPlace(directory.getAbsolutePath(), false);
        }
        logger.info(GENERATION_DONE);
    }

    /**
     * Generate java doc as string
     *
     * @param compilationUnit  @link CompilationUnit}
     * @param deleteOldJavaDoc boolean
     * @return String
     */
    public String generateJavaDocAsString(CompilationUnit compilationUnit,
                                          boolean deleteOldJavaDoc) {
        if (deleteOldJavaDoc) {
            deleter.deleteOldJavaDoc(compilationUnit);
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
     * Generate java doc for all classes
     *
     * @param directory        @link String}
     * @param dest             @link String}
     * @param toZip            boolean
     * @param deleteOldJavadoc boolean
     * @throws IOException Exception levée si erreur.
     */
    public void generateJavaDocForAllClasses(String directory, String dest, boolean toZip, boolean deleteOldJavadoc) throws IOException, CompilationException {
        logger.info(ALL_CLASSES_DOC, directory, dest);
        File dir = new File(directory);
        File destinationZip = new File(dest);
        generateJavaDocForAllClasses(dir, destinationZip, toZip, deleteOldJavadoc);
        logger.info(GENERATION_DONE);
    }

    /**
     * Generate java doc
     *
     * @param srcPath          @link String}
     * @param destinationFile  @link String}
     * @param deleteOldJavaDoc boolean
     * @throws IOException Exception levée si erreur.
     */
    private void generateJavaDoc(String srcPath, String destinationFile, boolean deleteOldJavaDoc) throws IOException, CompilationException {
        File srcFile = new File(srcPath);
        CompilationUnit compilationUnit = getCompilationUnit(srcFile);
        String javaDocAsString = generateJavaDocAsString(compilationUnit.toString(), deleteOldJavaDoc);
        File destFile = getDestination(destinationFile, srcFile, compilationUnit);
        FileUtils.write(destFile, javaDocAsString, UTF_8);
        logger.info("generated javadoc for  file {}", srcPath);
    }

    private void generateJavaDocInPlace(String srcPath, boolean deleteOldJavaDoc) throws IOException, CompilationException {
        generateJavaDoc(srcPath, srcPath, deleteOldJavaDoc);
    }

    /**
     * Generate java doc as string
     *
     * @param src              @link String}
     * @param deleteOldJavaDoc boolean
     * @return String
     * @throws IOException Exception levée si erreur.
     */
    public String generateJavaDocAsString(String src, boolean deleteOldJavaDoc) throws IOException, CompilationException {
        return generateJavaDocAsString(getCompilationUnit(src), deleteOldJavaDoc);
    }

    /**
     * Generate constructor java doc
     *
     * @param constructorDeclaration @link ConstructorDeclaration}
     */
    public void generateConstructorJavaDoc(ConstructorDeclaration constructorDeclaration, String text) {
        JavadocDescription javadocDescription = new JavadocDescription();
        Javadoc javadoc = new Javadoc(javadocDescription);
        NodeList<Parameter> constructParams = constructorDeclaration.getParameters();

        JavadocSnippet element = new JavadocSnippet(text);
        javadocDescription.addElement(element);
        if (!constructorDeclaration.getJavadoc().isPresent()) {
            addParamsToJavaDoc(constructParams, javadoc);
            addExceptionsToJavaDoc(constructorDeclaration.getThrownExceptions(), javadoc);
            constructorDeclaration.setJavadocComment(javadoc);
        }
    }

    /**
     * Generate constructor java doc
     *
     * @param constructorDeclaration @link ConstructorDeclaration}
     */
    private void generateConstructorJavaDoc(ConstructorDeclaration constructorDeclaration) {
        NodeList<Parameter> constructParams = constructorDeclaration.getParameters();
        String leadingComment;
        BlockStmt body = constructorDeclaration.getBody();
        if (constructParams.isEmpty() && body.getOrphanComments().isEmpty()
                && body.getStatements().isEmpty()) {
            leadingComment = readFromProperties(DEFAULT_CONSTR_COMMENT);
            body.addOrphanComment(new LineComment(leadingComment));
        } else {
            leadingComment = String.format(readFromProperties(CONSTR_COMMENT),
                    constructorDeclaration.getName().asString());
        }
        generateConstructorJavaDoc(constructorDeclaration, leadingComment);
    }

    /**
     * Generate java doc for type declaration
     *
     * @param typeDeclaration @link TypeDeclaration}
     */
    @SuppressWarnings("unchecked")
    private void generateJavaDocForTypeDeclaration(TypeDeclaration typeDeclaration) {
        if (typeDeclaration.hasJavaDocComment()) {
            Optional<Javadoc> optionalJavaDoc = typeDeclaration.getJavadoc();
            optionalJavaDoc.ifPresent(javadoc -> {
                List<JavadocBlockTag.Type> blockTypes = javadoc.getBlockTags().stream()
                        .map(JavadocBlockTag::getType).collect(Collectors.toList());
                if (!blockTypes.contains(JavadocBlockTag.Type.SINCE)) {
                    addBlockTagToClassJavaDoc(JavadocBlockTag.Type.SINCE, javadoc, readFromProperties(SINCE_VERSION));
                }
                if (!blockTypes.contains(JavadocBlockTag.Type.AUTHOR)) {
                    addBlockTagToClassJavaDoc(JavadocBlockTag.Type.AUTHOR, javadoc, readFromProperties(AUTHOR));
                }
                if (!blockTypes.contains(JavadocBlockTag.Type.VERSION)) {
                    addBlockTagToClassJavaDoc(JavadocBlockTag.Type.VERSION, javadoc, readFromProperties(VERSION));
                }
                typeDeclaration.setJavadocComment(javadoc);
            });
        } else {
            JavadocDescription javadocDescription = new JavadocDescription();
            Javadoc javadoc = new Javadoc(javadocDescription);
            String text = readFromProperties(TODO_CLASS_TEXT);
            if (typeDeclaration.isEnumDeclaration()) {
                text = readFromProperties(TODO_ENUM_TEXT);
            }
            JavadocSnippet element = new JavadocSnippet(text);
            javadocDescription.addElement(element);
            addBlockTagToClassJavaDoc(JavadocBlockTag.Type.AUTHOR, javadoc, readFromProperties(AUTHOR));
            addBlockTagToClassJavaDoc(JavadocBlockTag.Type.SINCE, javadoc, readFromProperties(SINCE_VERSION));
            addBlockTagToClassJavaDoc(JavadocBlockTag.Type.VERSION, javadoc, readFromProperties(VERSION));
            typeDeclaration.setJavadocComment(javadoc);
        }
    }

    /**
     * Generate java doc for type declaration
     *
     * @param typeDeclaration @link TypeDeclaration}
     */
    @SuppressWarnings("unchecked")
    private void generateJavaDocForTypeDeclaration(TypeDeclaration typeDeclaration , Map<String,String> javaDocElements) {
        Javadoc javadoc;
        if (typeDeclaration.hasJavaDocComment()) {
            Optional<Javadoc> optionalJavaDoc = typeDeclaration.getJavadoc();
           {
                javadoc = optionalJavaDoc.get();
                List<JavadocBlockTag.Type> blockTypes = javadoc.getBlockTags().stream()
                        .map(JavadocBlockTag::getType).collect(Collectors.toList());

                javaDocElements.forEach((k,v)->{
                    JavadocBlockTag.Type type  = JavadocBlockTag.Type.valueOf(k);

                    if (!blockTypes.contains(type)) {
                        addBlockTagToClassJavaDoc(type, javadoc, v);
                    }
                });
                typeDeclaration.setJavadocComment(javadoc);
            }
        }
        else {
            JavadocDescription javadocDescription = new JavadocDescription();
              javadoc = new Javadoc(javadocDescription);
            String text = readFromProperties(TODO_CLASS_TEXT);
            if (typeDeclaration.isEnumDeclaration()) {
                text = readFromProperties(TODO_ENUM_TEXT);
            }
            JavadocSnippet element = new JavadocSnippet(text);
            javadocDescription.addElement(element);
            addBlockTagToClassJavaDoc(JavadocBlockTag.Type.AUTHOR, javadoc, readFromProperties(AUTHOR));
            addBlockTagToClassJavaDoc(JavadocBlockTag.Type.SINCE, javadoc, readFromProperties(SINCE_VERSION));
            addBlockTagToClassJavaDoc(JavadocBlockTag.Type.VERSION, javadoc, readFromProperties(VERSION));

        }
        typeDeclaration.setJavadocComment(javadoc);
    }

    /**
     * Generate method java doc
     *
     * @param methodDeclaration @link MethodDeclaration}
     */
    private void generateMethodJavaDoc(MethodDeclaration methodDeclaration) {
        JavadocDescription javadocDescription = new JavadocDescription();
        Javadoc javadoc = new Javadoc(javadocDescription);
        JavadocSnippet element;
        String methodName = methodDeclaration.getName().asString();

        boolean isSetter = CodeUtils.isSetter(methodDeclaration);
        boolean isGetter = CodeUtils.isGetter(methodDeclaration);
        boolean isIs = CodeUtils.isIs(methodDeclaration);

        String paramFormat = "%s {@link %s}";
        JavadocSnippet inheritDocSnippet = new JavadocSnippet(readFromProperties(INHERIT_DOC));
        String methodReturnType = methodDeclaration.getType().asString();
        NodeList<Parameter> parameters = methodDeclaration.getParameters();

        if (!methodDeclaration.hasJavaDocComment()) {
            if (methodDeclaration.isAnnotationPresent(Override.class)) {
                javadocDescription.addElement(inheritDocSnippet);
                methodDeclaration.setJavadocComment(javadoc);
            } else {
                if (isSetter || isGetter || isIs) {
                    element = new JavadocSnippet("");
                } else if (Utils.isCamelCase(methodName)) {
                    element = new JavadocSnippet(StringUtils
                            .capitalize(StringUtils.
                                    lowerCase(Utils.
                                            unCamelCase(methodName, " "))));
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
                                String.format(readFromProperties(SETTER_COMMENT), paramName,
                                        Utils.toLowerCaseFirstLetter(getMethodeConcreteName(methodName, 3))));
                    } else {
                        blockTag = new FormattedJavadocBlockTag(JavadocBlockTag.Type.PARAM,
                                String.format(paramFormat, paramName, parameter.getType().asString()));
                    }
                    javadoc.addBlockTag(blockTag);
                }
                if (!methodDeclaration.getType().isVoidType()) {
                    JavadocBlockTag javadocBlockTag;
                    if (isGetter) {
                        javadocBlockTag = new FormattedJavadocBlockTag(JavadocBlockTag.Type.RETURN,
                                String.format(readFromProperties(ATTRIBUT_COMMENT_FORMAT),
                                        Utils.toLowerCaseFirstLetter(getMethodeConcreteName(methodName, 3))));
                    } else if (isIs) {
                        javadocBlockTag = new FormattedJavadocBlockTag(JavadocBlockTag.Type.RETURN,
                                String.format(readFromProperties(ATTRIBUT_COMMENT_FORMAT),
                                        Utils.toLowerCaseFirstLetter(getMethodeConcreteName(methodName, 2))));
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
            setupOverriddenMethods(methodDeclaration);
        } else {
            Javadoc oldJavaDoc = javadoc;
            Optional<Javadoc> optionalJavaDoc = methodDeclaration.getJavadoc();
            if (optionalJavaDoc.isPresent()) {
                oldJavaDoc = optionalJavaDoc.get();
            }
            List<String> paramTypes = oldJavaDoc.getBlockTags()
                    .stream()
                    .filter(block ->
                            block.getType().equals(JavadocBlockTag.Type.PARAM))
                    .filter(block -> block.getName().isPresent())
                    .map(block -> block.getName().get()).collect(Collectors.toList());
            for (Parameter parameter : parameters) {
                if (!paramTypes.contains(parameter.getNameAsString())) {

                    Type paramType = parameter.getType();
                    SimpleName paramName = parameter.getName();

                    if (paramType.isPrimitiveType()) {
                        paramFormat = "%s %s ";
                    }
                    if (isSetter) {
                        oldJavaDoc.addBlockTag(new FormattedJavadocBlockTag(JavadocBlockTag.Type.PARAM,
                                String.format(readFromProperties(SETTER_COMMENT),
                                        paramName.asString(),
                                        Utils.toLowerCaseFirstLetter(getMethodeConcreteName(methodName, 3)))));
                    } else {
                        JavadocBlockTag blockTag = new FormattedJavadocBlockTag(JavadocBlockTag.Type.PARAM,
                                String.format(paramFormat, paramName.asString(),
                                        paramType.asString()));
                        oldJavaDoc.addBlockTag(blockTag);
                    }
                }
            }
            addExceptionsToJavaDoc(methodDeclaration.getThrownExceptions(), oldJavaDoc);
            addReturnTagToJavadoc(methodDeclaration, methodName, isGetter, isIs,
                    methodReturnType, oldJavaDoc);
            methodDeclaration.setJavadocComment(oldJavaDoc);
        }
    }

    private String getMethodeConcreteName(String methodName, int i) {
        if (i <= methodName.length())
            return methodName.substring(i);
        return methodName;
    }


    /**
     * Add block tag to class javadoc
     *
     * @param type    {@link JavadocBlockTag.Type}
     * @param javadoc {@link Javadoc}
     * @param value   {@link String}
     */
    private void addBlockTagToClassJavaDoc(JavadocBlockTag.Type type, Javadoc javadoc, String value) {
        if (value != null) {
            javadoc.addBlockTag(new FormattedJavadocBlockTag(type, value));
        }
    }

    /**
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

    /**
     * Add exceptions to java doc
     *
     * @param thrownExceptions {@link NodeList}
     * @param javadoc          {@link Javadoc}
     */
    private void addExceptionsToJavaDoc(NodeList<ReferenceType> thrownExceptions, Javadoc javadoc) {
        for (ReferenceType thrownException : thrownExceptions) {
            boolean throwsExist = javadoc.getBlockTags().stream().anyMatch(block ->
                    block.getType().equals(JavadocBlockTag.Type.THROWS)
                            && block.getContent().getElements().get(0).toText()
                            .startsWith(thrownException.toString()));
            if (!throwsExist) {
                FormattedJavadocBlockTag javadocBlockTag = new FormattedJavadocBlockTag(JavadocBlockTag.Type.THROWS,
                        String.format(readFromProperties(EXCEPTION_COMMENT),
                                thrownException.asReferenceType()));
                javadoc.addBlockTag(javadocBlockTag);
            }
        }
    }

    /**
     * Generate field java doc
     *
     * @param fieldDeclaration @link FieldDeclaration}
     */
    private void generateFieldJavaDoc(FieldDeclaration fieldDeclaration) {
        generateFieldJavaDoc(fieldDeclaration, readFromProperties(CONSTANT_COMMENT));
    }

    /**
     * Generate field java doc
     *
     * @param fieldDeclaration @link FieldDeclaration}
     */
    public void generateFieldJavaDoc(FieldDeclaration fieldDeclaration, String text) {
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
            if (fieldDeclaration.isStatic() && fieldDeclaration.isFinal()
                    && variable.getInitializer().isPresent()) {
                javaDocText = text;
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
                javadocSnippet = new JavadocSnippet(String.format(javaDocText,
                        fieldName, String.format(typeText, type.asString()),
                        String.format(valueText, assignedValue)));
            } else {
                javaDocText = readFromProperties(FIELD_COMMENT);
                javadocSnippet = new JavadocSnippet(String.format(javaDocText, fieldName));
            }
            javadocDescription.addElement(javadocSnippet);
            fieldDeclaration.setJavadocComment(javadoc);
        }
    }

    private void addReturnTagToJavadoc(MethodDeclaration methodDeclaration,
                                       String methodName,
                                       boolean isGetter, boolean isIs,
                                       String methodReturnType,
                                       Javadoc oldJavaDoc) {
        boolean returnExists = oldJavaDoc.getBlockTags().stream().anyMatch(block ->
                block.getType().equals(JavadocBlockTag.Type.RETURN));
        if (!methodDeclaration.getType().isVoidType() && !returnExists) {
            if (isGetter) {
                oldJavaDoc.addBlockTag(new FormattedJavadocBlockTag(JavadocBlockTag.Type.RETURN,
                        String.format(readFromProperties(ATTRIBUT_COMMENT_FORMAT),
                                Utils.toLowerCaseFirstLetter(getMethodeConcreteName(methodName, 3)))));
            } else if (isIs) {
                oldJavaDoc.addBlockTag(new FormattedJavadocBlockTag(JavadocBlockTag.Type.RETURN,
                        String.format(readFromProperties(ATTRIBUT_COMMENT_FORMAT),
                                Utils.toLowerCaseFirstLetter(getMethodeConcreteName(methodName, 2)))));
            } else
                oldJavaDoc.addBlockTag(new FormattedJavadocBlockTag(JavadocBlockTag.Type.RETURN, methodReturnType));
        }
    }
}

