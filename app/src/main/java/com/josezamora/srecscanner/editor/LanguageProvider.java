package com.josezamora.srecscanner.editor;


import com.josezamora.srecscanner.editor.Languajes.JavaLang;
import com.josezamora.srecscanner.editor.Languajes.Lang;
import com.josezamora.srecscanner.editor.Languajes.PythonLang;

/**
 * The type Language provider.
 */
public class LanguageProvider {

    /**
     * Instantiates a new Language provider.
     *
     * @param choice the choice
     */
    LanguageProvider(Languages choice) {
        switch (choice) {
            case JAVA:
                language = new JavaLang();
                break;

            case PYTHON:
                language = new PythonLang();
                break;

            /* add new cases for new languages adding */
        }
    }

    private Lang language;

    /**
     * Gets extension.
     *
     * @param language the language
     * @return the extension
     */
    public static String getExtension(Languages language) {
        switch (language){
            case JAVA:
                return ".java";
            case PYTHON:
                return ".py";
            default:
                throw new RuntimeException("Unsupported lang");
        }
    }

    /**
     * Gets language.
     *
     * @return the language
     */
    Lang getLanguage() {
        return language;
    }

    /**
     * The enum Languages.
     */
    public enum Languages {
        /**
         * Java language.
         */
        JAVA,
        /**
         * Python language.
         */
        PYTHON,

        // add more languages here!
    }
}
