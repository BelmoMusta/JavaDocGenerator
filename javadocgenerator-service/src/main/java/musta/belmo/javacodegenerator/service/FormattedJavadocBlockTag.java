package musta.belmo.javacodegenerator.service;

import com.github.javaparser.javadoc.JavadocBlockTag;

/**
 * TODO: Complete the description of this class
 *
 * @author default author
 * @since 0.0.0.SNAPSHOT
 * @version 0.0.0
 */
public class FormattedJavadocBlockTag extends JavadocBlockTag {

    /**
     * The FormattedJavadocBlockTag class constructor.
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
