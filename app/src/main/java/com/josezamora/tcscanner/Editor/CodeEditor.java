package com.josezamora.tcscanner.Editor;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.os.Handler;
import android.text.Editable;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.TextWatcher;
import android.text.style.ForegroundColorSpan;
import android.text.style.ReplacementSpan;
import android.util.AttributeSet;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatEditText;

public class CodeEditor extends AppCompatEditText {

    public interface OnTextChangedListener {
        void onTextChanged(String text);
    }

    private final Handler updateHandler = new Handler();
    private final Runnable updateRunnable = new Runnable() {
        @Override
        public void run() {
            Editable e = getText();

            if (onTextChangedListener != null) {
                assert e != null;
                onTextChangedListener.onTextChanged(
                        removeNonAscii(e.toString()));
            }

            highlightWithoutChange(e);
        }
    };

    private OnTextChangedListener onTextChangedListener;
    private int updateDelay = 1000;
    private boolean dirty = false;
    private boolean modified = true;

    Map<String, Integer> colors = new HashMap<>();

    private int tabWidthInCharacters = 0;
    private int tabWidth = 0;

    LanguageProvider provider;

    public void setLanguage(LanguageProvider.Languages selectedItem) {
        provider = new LanguageProvider(selectedItem);
        init(selectedItem);
        updateHighlighting();
    }

    public static String removeNonAscii(String text) {
        return text.replaceAll("[^\\x0A\\x09\\x20-\\x7E]", "");
    }

    public CodeEditor(Context context) {
        super(context);
        init(LanguageProvider.Languages.JAVA);
    }

    public CodeEditor(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(LanguageProvider.Languages.JAVA);
    }

    public void setOnTextChangedListener(OnTextChangedListener listener) {
        onTextChangedListener = listener;
    }

    public void setUpdateDelay(int ms) {
        updateDelay = ms;
    }

    public void setTabWidth(int characters) {
        if (tabWidthInCharacters == characters) {
            return;
        }

        tabWidthInCharacters = characters;
        tabWidth = Math.round(getPaint().measureText("m") * characters);
    }

    public void updateHighlighting() {
        highlightWithoutChange(getText());
    }

    public boolean isModified() {
        return dirty;
    }

    public void setTextHighlighted(CharSequence text) {
        if (text == null)
            text = "";

        cancelUpdate();

        dirty = false;

        modified = false;
        String src = removeNonAscii(text.toString());
        setText(highlight(new SpannableStringBuilder(src)));
        modified = true;

        if (onTextChangedListener != null) {
            onTextChangedListener.onTextChanged(src);
        }
    }

    public void insertTab() {
        int start = getSelectionStart();
        int end = getSelectionEnd();

        Objects.requireNonNull(getText()).replace(
                Math.min(start, end),
                Math.max(start, end),
                "\t",
                0,
                1);
    }

    private void init(LanguageProvider.Languages lenguage) {
        provider = new LanguageProvider(lenguage);

        setHorizontallyScrolling(false);

        addTextChangedListener(new TextWatcher() {
            private int start = 0;
            private int count = 0;

            @Override
            public void onTextChanged(
                    CharSequence s,
                    int start,
                    int before,
                    int count) {
                this.start = start;
                this.count = count;
            }

            @Override
            public void beforeTextChanged(
                    CharSequence s,
                    int start,
                    int count,
                    int after) {
            }

            @Override
            public void afterTextChanged(Editable e) {
                cancelUpdate();
                convertTabs(e, start, count);

                if (!modified)
                    return;

                dirty = true;
                updateHandler.postDelayed(updateRunnable, updateDelay);
            }
        });
    }

    private void cancelUpdate() {
        updateHandler.removeCallbacks(updateRunnable);
    }

    private void highlightWithoutChange(Editable e) {
        modified = false;
        highlight(e);
        modified = true;
    }

    private Editable highlight(Editable e) {

        int length = e.length();

        if (length == 0)
            return e;

        Map<String, Pattern> patternMap = provider.getLanguage().getPatterns();
        Map<String, Integer> colorMap = provider.getLanguage().getColors();

        for(Map.Entry<String, Pattern> entry : patternMap.entrySet())
            for(Matcher m = entry.getValue().matcher(e); m.find();)
                e.setSpan(new ForegroundColorSpan(colorMap.get(entry.getKey())), m.start(), m.end(),
                        Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

        return e;
    }

    private void convertTabs(Editable e, int start, int count) {
        if (tabWidth < 1) {
            return;
        }

        String s = e.toString();

        for (int stop = start + count;
             (start = s.indexOf("\t", start)) > -1 && start < stop;
             ++start) {
            e.setSpan(
                    new TabWidthSpan(tabWidth),
                    start,
                    start + 1,
                    Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        }
    }

    private static class TabWidthSpan extends ReplacementSpan {
        private int width;

        private TabWidthSpan(int width) {
            this.width = width;
        }

        @Override
        public int getSize(
                @NonNull Paint paint,
                CharSequence text,
                int start,
                int end,
                Paint.FontMetricsInt fm) {
            return width;
        }

        @Override
        public void draw(
                @NonNull Canvas canvas,
                CharSequence text,
                int start,
                int end,
                float x,
                int top,
                int y,
                int bottom,
                @NonNull Paint paint) {
        }
    }
}
