package musta.belmo.javacodecore;

import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.type.Type;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Arrays;

/**
 * TODO : Compléter la description de cette classe
 */
public class Utils {

    /**
     * La constante {@link #CAMELCASE_REGEX} de type {@link String} ayant la valeur {@value #CAMELCASE_REGEX}.
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
     * Add full stop
     *
     * @param input {@link String}
     * @return String
     */
    public static String addFullStop(String input) {
        String retValue;
        if (StringUtils.isBlank(input)) {
            retValue = input;
        } else
            retValue = StringUtils.appendIfMissing(input, ".", ".");
        return retValue;
    }

    /**
     * Un camel case
     *
     * @param input     {@link String}
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
     * @return boolean
     */
    public static boolean isCamelCase(String s) {
        return s != null && s.matches("[a-z]+[A-Z\\d]+\\w+");
    }

    public static void saveFile(java.io.File src, java.io.File dest) throws IOException {
        FileUtils.copyFile(src, dest);
    }

    public static void saveFile(String src, String dest) throws IOException {
        FileUtils.copyFile(new java.io.File(src), new java.io.File(dest));
    }

    public static void saveToFile(byte[] bytes, java.io.File dest) throws IOException {
        FileUtils.writeByteArrayToFile(dest, bytes, false);
    }

    public static void saveToFile(byte[] bytes, String dest) throws IOException {
        saveToFile(bytes, new java.io.File(dest));
    }

    private static boolean isMethodStartsWith(MethodDeclaration methodDeclaration, String prefix) {
        return methodDeclaration != null && methodDeclaration.getName().asString().startsWith(prefix);
    }

    public static boolean isSetter(MethodDeclaration methodDeclaration) {
        return isMethodStartsWith(methodDeclaration, "set");
    }

    public static boolean isGetter(MethodDeclaration methodDeclaration) {
        return isMethodStartsWith(methodDeclaration, "get");
    }

    public static boolean isIs(MethodDeclaration methodDeclaration) {
        return isMethodStartsWith(methodDeclaration, "is") && methodDeclaration.getType()
                .toString()
                .equalsIgnoreCase("boolean");
    }

    public static boolean isImutable(Type type) {
        return type.isPrimitiveType() || isBoxedType(type);
    }

    public static boolean isBoxedType(Type type) {
        return type != null &&
                Arrays.asList("String", "Integer", "Boolean", "Double", "Float", "Short", "Character", "Long", "Byte").contains(type.asString());
    }

    public static String getSimpleClassName(String fullClassName) {
        String lRet = fullClassName;
        if (fullClassName != null && fullClassName.contains(".")) {
            lRet = fullClassName.substring(fullClassName.lastIndexOf('.') + 1);
        }
        return lRet;
    }
}
