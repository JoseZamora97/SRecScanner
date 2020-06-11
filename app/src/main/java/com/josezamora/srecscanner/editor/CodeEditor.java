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

    private final Runnable updateRunnable = () -> highlight(getText());

    private final Handler updateHandler = new Handler();
    // Update time to run the updateRunnable
    private int updateDelay = 1000;

    /**
     * Instantiates a new Code editor.
     *
     * @param context the context
     */
    public CodeEditor(Context context) {
        super(context);
        init(LanguageProvider.Languages.TEXT);
    }

    /**
     * Instantiates a new Code editor.
     *
     * @param context the context
     * @param attrs   the attrs
     */
    public CodeEditor(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(LanguageProvider.Languages.TEXT);
    }

    /**
     * Sets language.
     *
     * @param selectedItem the selected item
     */
    public void setLanguage(LanguageProvider.Languages selectedItem) {
        init(selectedItem);
        updateHighlighting();
    }

    /**
     * Update highlighting.
     */
    public void updateHighlighting() {
        highlight(getText());
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

    private void init(LanguageProvider.Languages language) {
        provider = new LanguageProvider(language);

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

                updateHandler.postDelayed(updateRunnable, updateDelay);
            }
        });
    }

    private void cancelUpdate() {
        updateHandler.removeCallbacks(updateRunnable);
    }

    private void highlight(Editable e) {

        int length = e.length();

        if (length == 0)
            return;

        Map<String, Pattern> patternMap = provider.getLanguage().getPatterns();
        Map<String, Integer> colorMap = provider.getLanguage().getColors();

        for(Map.Entry<String, Pattern> entry : patternMap.entrySet())
            for(Matcher m = entry.getValue().matcher(e); m.find();)
                e.setSpan(
                        new ForegroundColorSpan(colorMap.get(entry.getKey())),
                        m.start(),
                        m.end(),
                        Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
                );
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
