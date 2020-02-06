package com.josezamora.srecscanner.editor.Languajes;

import android.graphics.Color;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

/**
 * The type Lang.
 */
public abstract class Lang {

    /** COLORS FOR PATTERNS **/
    private static final String COLOR_NUM = "#8BE2E8";
    private static final String NUMBERS = "\\b(\\d*[.]?\\d+)\\b";
    private static final String COLOR_SYM = "#44B38B";
    /**
     * KEYS
     */
    @SuppressWarnings("WeakerAccess")
    final String NUM_KEY = "num";
    /**
     * The Keywords key.
     */
    final String KEYWORDS_KEY = "key";
    private static final String COLOR_KEYS = "#CE00FF";
    /**
     * The Symbols key.
     */
    final String SYMBOLS_KEY = "sym";

    /**
     * The Color map.
     */
    Map<String, Integer> colorMap;
    /**
     * The Pattern map.
     */
    Map<String, Pattern> patternMap;

    /**
     * Instantiates a new Lang.
     */
    Lang() {
        patternMap = new HashMap<>();
        patternMap.put(NUM_KEY, Pattern.compile(NUMBERS));

        colorMap = new HashMap<>();
        colorMap.put(NUM_KEY, Color.parseColor(COLOR_NUM));
        colorMap.put(KEYWORDS_KEY, Color.parseColor(COLOR_KEYS));
        colorMap.put(SYMBOLS_KEY, Color.parseColor(COLOR_SYM));
    }

    /**
     * Get colors map.
     *
     * @return the map
     */
    public Map<String, Integer> getColors(){
        return colorMap;
    }

    /**
     * Get patterns map.
     *
     * @return the map
     */
    public Map<String, Pattern> getPatterns(){
        return patternMap;
    }
}
