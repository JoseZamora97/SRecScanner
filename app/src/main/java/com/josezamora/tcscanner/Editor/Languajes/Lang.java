package com.josezamora.tcscanner.Editor.Languajes;

import android.graphics.Color;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

public abstract class Lang {

    /** KEYS **/
    private String NUM_KEY = "num";
    private static final String NUMBERS = "\\b(\\d*[.]?\\d+)\\b";

    String KEYWORDS_KEY = "key";
    String SYMBOLS_KEY = "sym";

    /** COLORS FOR PATTERNS **/
    private static final String COLOR_NUMS = "#44B38B";
    private static final String COLOR_KEYS = "#CE00FF";
    private static final String COLOR_SYMS = "#44B38B";

    Map<String, Integer> colorMap;
    Map<String, Pattern> patternMap;

    Lang() {
        patternMap = new HashMap<>();
        patternMap.put(NUM_KEY, Pattern.compile(NUMBERS));

        colorMap = new HashMap<>();
        colorMap.put(NUM_KEY, Color.parseColor(COLOR_NUMS));
        colorMap.put(KEYWORDS_KEY, Color.parseColor(COLOR_KEYS));
        colorMap.put(SYMBOLS_KEY, Color.parseColor(COLOR_SYMS));
    }

    public Map<String, Integer> getColors(){
        return colorMap;
    }

    public Map<String, Pattern> getPatterns(){
        return patternMap;
    }
}
