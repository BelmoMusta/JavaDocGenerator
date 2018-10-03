package musta.belmo.javadocgenerator;

import musta.belmo.javadocgenerator.logger.MustaLogger;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.logging.Level;

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
    static String convertPackageDeclarationToPath(String string) {
        String retValue = "";
        if (string != null)
            retValue = string.replaceAll("\\.", "\\\\");
        retValue = retValue.replace(";", "");
        return retValue;
    }

    /**
     * TODO: Compléter la description de cette méthode
     *
     * @param args {@link String}
     */
    public static void main(String[] args) {
        MustaLogger logger = new MustaLogger(Utils.class);
        logger.logCurrentMethod(musta.belmo.javadocgenerator.logger.Level.DEBUG);
    }
}
