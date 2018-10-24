package musta.belmo.javacodeutils;
import com.github.javaparser.javadoc.JavadocBlockTag;

/**
 * TODO : Compléter la description de cette classe
 */
public class FormattedJavadocBlockTag extends JavadocBlockTag {

    /**
     * Constructeur de la classe FormattedJavadocBlockTag
     *
     * @param type{@link Type}
     * @param content{@link String}
     */
    public FormattedJavadocBlockTag(Type type, String content) {
        super(type, content);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toText() {
        String lRet = super.toText();
        if (lRet == null)
            lRet = "";
        if (lRet.contains(" @link")) {
            lRet = lRet.replace("@link", "{@link");
        }
        lRet = lRet.replaceAll("<\\w+>", "");
        lRet = lRet.replaceAll("[\\[\\]]", "");
        return lRet;
    }
}
