package com.josezamora.srecscanner.editor;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.os.Handler;
import android.text.Editable;
import android.text.Spannable;
import android.text.TextWatcher;
import android.text.style.ForegroundColorSpan;
import android.text.style.ReplacementSpan;
import android.util.AttributeSet;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatEditText;

import java.util.Map;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * The type Code editor.
 */
public class CodeEditor extends AppCompatEditText {

    /**
     * The Provider.
     */
    LanguageProvider provider;

    /**
     * Instantiates a new Code editor.
     *
     * @param context the context
     */
    public CodeEditor(Context context) {
        super(context);
        init(LanguageProvider.Languages.JAVA);
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
    private int updateDelay = 500;
    private boolean dirty = false;
    private boolean modified = true;

    /**
     * Instantiates a new Code editor.
     *
     * @param context the context
     * @param attrs   the attrs
     */
    public CodeEditor(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(LanguageProvider.Languages.JAVA);
    }

    /**
     * Remove non ascii string.
     *
     * @param text the text
     * @return the string
     */
    public static String removeNonAscii(String text) {
        return text.replaceAll("[^\\x0A\\x09\\x20-\\x7E]", "");
    }

    /**
     * Sets on text changed listener.
     *
     * @param onTextChangedListener the on text changed listener
     */
    public void setOnTextChangedListener(OnTextChangedListener onTextChangedListener) {
        this.onTextChangedListener = onTextChangedListener;
    }

    /**
     * Sets language.
     *
     * @param selectedItem the selected item
     */
    public void setLanguage(LanguageProvider.Languages selectedItem) {
        provider = new LanguageProvider(selectedItem);
        init(selectedItem);
        updateHighlighting();
    }

    /**
     * Update highlighting.
     */
    public void updateHighlighting() {
        highlightWithoutChange(getText());
    }

    /**
     * Is modified boolean.
     *
     * @return the boolean
     */
    public boolean isModified() {
        return dirty;
    }

    /**
     * Sets modified.
     *
     * @param b the b
     */
    public void setModified(boolean b) {
        dirty = b;
    }

    /**
     * Insert tab.
     */
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

    private void convertTabs(Editable e, int start, int count) {
        int tabWidth = 4;

        String s = e.toString();

        for (int stop = start + count;
             (start = s.indexOf("\t", start)) > -1 && start < stop;
             ++start) {
            e.setSpan(new TabWidthSpan(tabWidth),
                    start,
                    start + 1,
                    Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        }
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

    @SuppressWarnings("ConstantConditions")
    private void highlight(Editable e) {

        int length = e.length();

        if (length == 0)
            return;

        Map<String, Pattern> patternMap = provider.getLanguage().getPatterns();
        Map<String, Integer> colorMap = provider.getLanguage().getColors();

        for(Map.Entry<String, Pattern> entry : patternMap.entrySet())
            for(Matcher m = entry.getValue().matcher(e); m.find();)
                e.setSpan(new ForegroundColorSpan(colorMap.get(entry.getKey())), m.start(), m.end(),
                        Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
    }

    /**
     * The interface On text changed listener.
     */
    public interface OnTextChangedListener {
        /**
         * On text changed.
         *
         * @param text the text
         */
        void onTextChanged(String text);
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
