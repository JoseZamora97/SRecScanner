package com.josezamora.srecscanner.editor.Languages;

import java.util.regex.Pattern;

/**
 * The type Java lang.
 */
public class JavaLang extends Lang {
    /** PATTERNS **/
    private static final String KEYWORDS =
            "\\b(break|continue|do|else|for|if|return|while"
                    + "|enum|num|void|case|char|default|double|float|int|long|short"
                    + "abstract|assert|boolean|byte|extends|final|finally|implements|import|"
                    + "instanceof|interface|null|native|package|strictfp|super|synchronized|"
                    + "throws|transient|static|private|public|protected)\\b";

    private static final String SYMBOLS = "[-!%&*()+|=<>{}\\[\\]:]";

    /**
     * Instantiates a new Java lang.
     */
    public JavaLang() {
        super();
        patternMap.put(KEYWORDS_KEY, Pattern.compile(KEYWORDS));
        patternMap.put(SYMBOLS_KEY, Pattern.compile(SYMBOLS));
    }
}
