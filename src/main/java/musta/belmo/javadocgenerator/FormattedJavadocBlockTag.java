package musta.belmo.javadocgenerator;

import com.github.javaparser.javadoc.JavadocBlockTag;

public class FormattedJavadocBlockTag extends JavadocBlockTag {
    public FormattedJavadocBlockTag(Type type, String content) {
        super(type, content);
    }

    @Override
    public String toText() {
        String lRet = super.toText();
        if (lRet == null) lRet = "";
        if (lRet.contains(" @link")) {
            lRet = lRet.replace("@link", "{@link");
        }
        lRet = lRet.replaceAll("<\\w+>", "");
        lRet = lRet.replaceAll("[\\[\\]]", "");
        return lRet;
    }
}
