package com.josezamora.srecscanner.editor.Languajes;

import android.graphics.Color;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

public abstract class Lang {

    /** COLORS FOR PATTERNS **/
    private static final String COLOR_NUM = "#8BE2E8";
    private static final String NUMBERS = "\\b(\\d*[.]?\\d+)\\b";
    private static final String COLOR_SYM = "#44B38B";
    /**
     * KEYS
     **/
    @SuppressWarnings("WeakerAccess")
    final String NUM_KEY = "num";
    final String KEYWORDS_KEY = "key";
    private static final String COLOR_KEYS = "#CE00FF";
    final String SYMBOLS_KEY = "sym";

    Map<String, Integer> colorMap;
    Map<String, Pattern> patternMap;

    Lang() {
        patternMap = new HashMap<>();
        patternMap.put(NUM_KEY, Pattern.compile(NUMBERS));

        colorMap = new HashMap<>();
        colorMap.put(NUM_KEY, Color.parseColor(COLOR_NUM));
        colorMap.put(KEYWORDS_KEY, Color.parseColor(COLOR_KEYS));
        colorMap.put(SYMBOLS_KEY, Color.parseColor(COLOR_SYM));
    }

    public Map<String, Integer> getColors(){
        return colorMap;
    }

    public Map<String, Pattern> getPatterns(){
        return patternMap;
    }
}
