package musta.belmo.javacodegenerator.util;

import com.github.javaparser.ast.NodeList;
import com.github.javaparser.ast.body.FieldDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.body.Parameter;
import com.github.javaparser.ast.type.ReferenceType;
import com.github.javaparser.javadoc.Javadoc;
import com.github.javaparser.javadoc.JavadocBlockTag;
import musta.belmo.javacodegenerator.service.FormattedJavadocBlockTag;
import musta.belmo.javacodegenerator.service.GeneratorConstantes;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import java.io.*;
import java.net.URI;
import java.util.Comparator;
import java.util.Deque;
import java.util.LinkedList;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

/**
 * TODO: Complete the description of this class
 *
 * @author default author
 * @since 0.0.0.SNAPSHOT
 * @version 0.0.0
 */
public class CodeUtils {

    /**
     * @param methodDeclaration {@link MethodDeclaration}
     * @param prefix {@link String}
     * @return Attribut {@link #methodStartsWith}
     */
    private static boolean isMethodStartsWith(MethodDeclaration methodDeclaration, String prefix) {
        return methodDeclaration != null && methodDeclaration.getName().asString().startsWith(prefix);
    }

    /**
     * @param methodDeclaration {@link MethodDeclaration}
     * @return Attribut {@link #setter}
     */
    public static boolean isSetter(MethodDeclaration methodDeclaration) {
        return isMethodStartsWith(methodDeclaration, "set");
    }

    /**
     * @param methodDeclaration {@link MethodDeclaration}
     * @return Attribut {@link #getter}
     */
    public static boolean isGetter(MethodDeclaration methodDeclaration) {
        return isMethodStartsWith(methodDeclaration, "get");
    }

    /**
     * @param methodDeclaration {@link MethodDeclaration}
     * @return Attribut {@link #is}
     */
    public static boolean isIs(MethodDeclaration methodDeclaration) {
        return isMethodStartsWith(methodDeclaration, "is") && methodDeclaration.getType().toString().equalsIgnoreCase("boolean");
    }

    /**
     * @return Attribut {@link #fieldComparator}
     */
    public static Comparator<FieldDeclaration> getFieldComparator() {
        return (o1, o2) -> {
            int compare = getFieldLevel(o2) - getFieldLevel(o1);
            if (compare == 0)
                compare = o1.getVariables().get(0).getName().asString().compareTo(o2.getVariables().get(0).getName().asString());
            return compare;
        };
    }

    /**
     * Clone field declaration
     *
     * @param from {@link FieldDeclaration}
     * @param to {@link FieldDeclaration}
     */
    public static void cloneFieldDeclaration(FieldDeclaration from, final FieldDeclaration to) {
        to.setModifiers(from.getModifiers());
        to.setVariables(from.getVariables());
        from.getComment().ifPresent((str) -> to.setBlockComment(str.getContent()));
        to.setAnnotations(from.getAnnotations());
    }

    /**
     * @param fieldDeclaration {@link FieldDeclaration}
     * @return Attribut {@link #fieldLevel}
     */
    public static int getFieldLevel(FieldDeclaration fieldDeclaration) {
        int level = 0;
        if (fieldDeclaration.isPublic() && fieldDeclaration.isStatic()) {
            level += 100000;
        } else if (fieldDeclaration.isPublic()) {
            level += 20;
        }
        if (fieldDeclaration.isStatic()) {
            level += 10000;
        }
        if (fieldDeclaration.isFinal()) {
            level += 1000;
        }
        if (fieldDeclaration.isProtected()) {
            level += 100;
        }
        if (fieldDeclaration.isPrivate()) {
            level += 10;
        }
        if (fieldDeclaration.isTransient()) {
            level += 1;
        }
        return level;
    }

    /**
     * The {@link #CAMELCASE_REGEX} Constant of type {@link String} holding the value {@value #CAMELCASE_REGEX}.
     */
    private static final String CAMELCASE_REGEX = "(?<!(^|[A-Z\\d]))((?=[A-Z\\d])|[A-Z](?=[\\d]))|(?<!^)(?=[A-Z\\d][a-z])";

    /**
     * To lower case first letter
     *
     * @param input {@link String}
     * @return String
     */
    public static String toLowerCaseFirstLetter(String input) {
        String retValue;
        if (StringUtils.isBlank(input)) {
            retValue = input;
        } else
            retValue = Character.toLowerCase(input.charAt(0)) + input.substring(1);
        return retValue;
    }

    /**
     * Un camel case
     *
     * @param input {@link String}
     * @param delimeter {@link String}
     * @return String
     */
    public static String unCamelCase(String input, String delimeter) {
        return String.join(delimeter, input.split(CAMELCASE_REGEX));
    }

    /**
     * Convert package declaration to path
     *
     * @param string {@link String}
     * @return String
     */
    public static String convertPackageDeclarationToPath(String string) {
        String retValue = "";
        if (string != null)
            retValue = string.replaceAll("\\.", "\\\\");
        retValue = retValue.replace(";", "");
        return retValue;
    }

    /**
     * @param s {@link String}
     * @return Attribut {@link #camelCase}
     */
    public static boolean isCamelCase(String s) {
        return s != null && s.matches("[a-z]+[A-Z\\d]+\\w+");
    }

    /**
     * Save to file
     *
     * @param bytes {@link byte}
     * @param dest {@link File}
     * @throws IOException the raised exception if error.
     */
    public static void saveToFile(byte[] bytes, File dest) throws IOException {
        FileUtils.writeByteArrayToFile(dest, bytes, false);
    }

    /**
     * Save to file
     *
     * @param bytes {@link byte}
     * @param dest {@link String}
     * @throws IOException the raised exception if error.
     */
    public static void saveToFile(byte[] bytes, String dest) throws IOException {
        saveToFile(bytes, new File(dest));
    }

    /**
     * Cast to
     *
     * @param a {@link Object}
     * @return T
     */
    public static <T> T castTo(Object a) {
        return (T) a;
    }

    /**
     * TODO: Complete the description of this method
     *
     * @param in {@link InputStream}
     * @param out {@link OutputStream}
     * @throws IOException the raised exception if error.
     */
    private static void copy(InputStream in, OutputStream out) throws IOException {
        byte[] buffer = new byte[1024];
        while (true) {
            int readCount = in.read(buffer);
            if (readCount < 0) {
                break;
            }
            out.write(buffer, 0, readCount);
        }
    }

    /**
     * TODO: Complete the description of this method
     *
     * @param file {@link File}
     * @param out {@link OutputStream}
     * @throws IOException the raised exception if error.
     */
    private static void copy(File file, OutputStream out) throws IOException {
        InputStream in = new FileInputStream(file);
        try {
            copy(in, out);
        } finally {
            in.close();
        }
    }

    /**
     * TODO: Complete the description of this method
     *
     * @param directory {@link File}
     * @param zipfile {@link File}
     * @throws IOException the raised exception if error.
     */
    public static void zip(File directory, File zipfile) throws IOException {
        URI base = directory.toURI();
        Deque<File> queue = new LinkedList<>();
        queue.push(directory);
        OutputStream out = new FileOutputStream(zipfile);
        Closeable res = out;
        try {
            ZipOutputStream zout = new ZipOutputStream(out);
            res = zout;
            while (!queue.isEmpty()) {
                directory = queue.pop();
                if (directory != null && directory.listFiles() != null)
                    for (File kid : directory.listFiles()) {
                        String name = base.relativize(kid.toURI()).getPath();
                        if (kid.isDirectory()) {
                            queue.push(kid);
                            name = name.endsWith("/") ? name : name + "/";
                            zout.putNextEntry(new ZipEntry(name));
                        } else {
                            zout.putNextEntry(new ZipEntry(name));
                            if (!zipfile.getName().equals(kid.getName()))
                                copy(kid, zout);
                            zout.closeEntry();
                        }
                    }
            }
        } finally {
            res.close();
        }
    }

    /**
     * @param methodName {@link String}
     * @param i int
     * @return Attribut {@link #methodeConcreteName}
     */
    public static String getMethodeConcreteName(String methodName, int i) {
        if (i <= methodName.length())
            return methodName.substring(i);
        return methodName;
    }

    /**
     * Add block tag to class java doc
     *
     * @param type {@link JavadocBlockTag.Type}
     * @param javadoc {@link Javadoc}
     * @param value {@link String}
     */
    public static void addBlockTagToClassJavaDoc(JavadocBlockTag.Type type, Javadoc javadoc, String value) {
        if (value != null) {
            javadoc.addBlockTag(new FormattedJavadocBlockTag(type, value));
        }
    }

    /**
     * Add params to java doc
     *
     * @param constructParams {@link NodeList}
     * @param javadoc {@link Javadoc}
     */
    public static void addParamsToJavaDoc(NodeList<Parameter> constructParams, Javadoc javadoc) {
        for (Parameter parameter : constructParams) {
            FormattedJavadocBlockTag javadocBlockTag = new FormattedJavadocBlockTag(JavadocBlockTag.Type.PARAM, String.format("%s{@link %s}", parameter.getName().asString(), parameter.getType().asString()));
            javadoc.addBlockTag(javadocBlockTag);
        }
    }
}
