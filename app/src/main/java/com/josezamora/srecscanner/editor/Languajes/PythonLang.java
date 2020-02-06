package com.josezamora.srecscanner.editor.Languajes;

import android.graphics.Color;

import java.util.regex.Pattern;

/**
 * The type Python lang.
 */
public class PythonLang extends Lang {

    /** KEYS **/
    private static final String DEF_FUNCTION_KEY = "dfu";

    /** PATTERNS **/
    private static final String SYMBOLS = "[-!%&*()+|=<>{}\\[\\]:]";
    private static final String KEYWORDS =
            "\\b(break|continue|do|else|for|if|return|while|"
                    + "and|as|assert|class|def|del|"
                    + "elif|except|exec|finally|from|global|import|in|is|lambda|"
                    + "nonlocal|not|or|pass|raise|try|with|yield|"
                    + "False|True|None)\\b";

    private static final String DEF_FUNCTIONS =
            "\\b(print|len|join|split|replace|upper|lower|range|str|int|float|max|"
                    + "min|sum|list|tuple|open|ord|round|type|input)\\b";

    /** COLORS FOR PATTERNS **/
    private static final String COLOR_DEF_FUN = "#9000FF";

    /**
     * Instantiates a new Python lang.
     */
    public PythonLang() {
        super();
        patternMap.put(KEYWORDS_KEY, Pattern.compile(KEYWORDS));
        patternMap.put(SYMBOLS_KEY, Pattern.compile(SYMBOLS));
        patternMap.put(DEF_FUNCTION_KEY, Pattern.compile(DEF_FUNCTIONS));

        colorMap.put(DEF_FUNCTION_KEY, Color.parseColor(COLOR_DEF_FUN));
    }
}
