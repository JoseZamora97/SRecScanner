package com.josezamora.srecscanner.editor.Languages;

import android.graphics.Color;

/**
 * The type Text lang.
 */
public class TextLang extends Lang {

    private static final String COLOR = "#ffffff";

    /**
     * Instantiates a new Text lang.
     */
    public TextLang() {
        super();

        colorMap.put(NUM_KEY, Color.parseColor(COLOR));
        colorMap.put(KEYWORDS_KEY, Color.parseColor(COLOR));
        colorMap.put(SYMBOLS_KEY, Color.parseColor(COLOR));
    }
}
