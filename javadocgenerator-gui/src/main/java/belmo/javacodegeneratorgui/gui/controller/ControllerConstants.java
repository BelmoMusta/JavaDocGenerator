package belmo.javacodegeneratorgui.gui.controller;

import javafx.scene.input.KeyCharacterCombination;
import javafx.scene.input.KeyCombination;
import org.fxmisc.richtext.model.StyleSpans;
import org.fxmisc.richtext.model.StyleSpansBuilder;

import java.util.Collection;
import java.util.Collections;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
/**
 * TODO: Compléter la description de cette classe
 *
 * @author toBeSpecified
 * @since 1.0.0.SNAPSHOT
 */
public interface ControllerConstants {

    /**
     * L'attribut {@link #KEYWORDS}.
     */
    String[] KEYWORDS = new String[]{"abstract", "assert", "boolean", "break", "byte", "case", "catch", "char", "class", "const", "continue", "default", "do", "double", "else", "enum", "extends", "final", "finally", "float", "for", "goto", "if", "implements", "import", "instanceof", "int", "interface", "long", "native", "new", "package", "private", "protected", "public", "return", "short", "static", "strictfp", "super", "switch", "synchronized", "this", "throw", "throws", "transient", "try", "void", "volatile", "while"};

    /**
     * L'attribut {@link #KEYWORD_PATTERN}.
     */
    String KEYWORD_PATTERN = "\\b(" + String.join("|", KEYWORDS) + ")\\b";

    /**
     * L'attribut {@link #PAREN_PATTERN}.
     */
    String PAREN_PATTERN = "[()]";

    /**
     * L'attribut {@link #BRACE_PATTERN}.
     */
    String BRACE_PATTERN = "[{}]";

    /**
     * L'attribut {@link #BRACKET_PATTERN}.
     */
    String BRACKET_PATTERN = "[\\[]]";

    /**
     * L'attribut {@link #SEMICOLON_PATTERN}.
     */
    String SEMICOLON_PATTERN = "\\;";

    /**
     * L'attribut {@link #STRING_PATTERN}.
     */
    String STRING_PATTERN = "\"([^\"\\\\]|\\\\.)*\"";

    /**
     * L'attribut {@link #COMMENT_PATTERN}.
     */
    String COMMENT_PATTERN = "//[^\n]*" + "|" + "/\\*(.|\\R)*?\\*/";

    /**
     * L'attribut {@link #PATTERN}.
     */
    Pattern PATTERN = Pattern.compile("(?<KEYWORD>" + KEYWORD_PATTERN + ")" + "|(?<PAREN>" + PAREN_PATTERN + ")" + "|(?<BRACE>" + BRACE_PATTERN + ")" + "|(?<BRACKET>" + BRACKET_PATTERN + ")" + "|(?<SEMICOLON>" + SEMICOLON_PATTERN + ")" + "|(?<STRING>" + STRING_PATTERN + ")" + "|(?<COMMENT>" + COMMENT_PATTERN + ")");

    String FA_SAVE = "fa-save";

    KeyCombination CTRL_I = ctrl("I");
    KeyCombination CTRL_J = ctrl("J");
    KeyCombination CTRL_R = ctrl("R");
    KeyCombination CTRL_SHIFT_J = ctrlShift("J");
    KeyCombination CTRL_N = ctrl("N");
    KeyCombination CTRL_O = ctrl("O");
    KeyCombination CTRL_S = ctrl("S");
    KeyCombination CTRL_SHIFT_S = ctrlShift("S");
    KeyCombination CTRL_SHIFT_O = ctrlShift("O");

    /**
     * Compute highlighting
     *
     * @param text {@link String}
     * @return StyleSpans<Collection>
     */
    default StyleSpans<Collection<String>> computeHighlighting(String text) {
        Matcher matcher = PATTERN.matcher(text);
        int lastKwEnd = 0;
        StyleSpansBuilder<Collection<String>> spansBuilder = new StyleSpansBuilder<>();
        while (matcher.find()) {
            String styleClass = matcher.group("KEYWORD") != null ? "keyword" : matcher.group("PAREN") != null ? "paren" : matcher.group("BRACE") != null ? "brace" : matcher.group("BRACKET") != null ? "bracket" : matcher.group("SEMICOLON") != null ? "semicolon" : matcher.group("STRING") != null ? "string" : matcher.group("COMMENT") != null ? "comment" : null;
            /* never happens */
            assert styleClass != null;
            spansBuilder.add(Collections.emptyList(), matcher.start() - lastKwEnd);
            spansBuilder.add(Collections.singleton(styleClass), matcher.end() - matcher.start());
            lastKwEnd = matcher.end();
        }
        spansBuilder.add(Collections.emptyList(), text.length() - lastKwEnd);
        return spansBuilder.create();
    }

    static KeyCharacterCombination createFromString(String combination) {
        String[] split = combination.split("[\\+\\- ]");

        String character = split[split.length - 1];

        KeyCombination.ModifierValue up = KeyCombination.ModifierValue.UP;
        KeyCombination.ModifierValue down = KeyCombination.ModifierValue.DOWN;
        KeyCombination.ModifierValue shift = up;
        KeyCombination.ModifierValue control = up;
        KeyCombination.ModifierValue alt = up;

        for (int i = 0; i < split.length - 1; i++) {
            String key = split[i].trim();

            if ("CTRL".equalsIgnoreCase(key) || "CTR".equalsIgnoreCase(key)) {
                control = down;
            } else if ("SHIFT".equalsIgnoreCase(key)) {
                shift = down;
            } else if ("ALT".equalsIgnoreCase(key)) {
                alt = down;
            }
        }
        return new KeyCharacterCombination(character,
                shift,
                control,
                alt,
                up,
                up);
    }


    static KeyCharacterCombination ctrlShift(String key) {
        return createFromString("CTRL+SHIFT+" + key);
    }

    static KeyCharacterCombination ctrl(String key) {
        return createFromString("CTRL+" + key);
    }

    static KeyCharacterCombination ctrlAlt(String key) {
        return createFromString("CTRL+ALT" + key);
    }

    static KeyCharacterCombination altShift(String key) {
        return createFromString("CTRL+SHIFT" + key);
    }

    static KeyCharacterCombination alt(String key) {
        return createFromString("ALT+" + key);
    }
}
