package com.josezamora.tcscanner.Editor;


import com.josezamora.tcscanner.Editor.Languajes.JavaLang;
import com.josezamora.tcscanner.Editor.Languajes.Lang;
import com.josezamora.tcscanner.Editor.Languajes.PythonLang;

public class LanguageProvider {

    public enum Languages {JAVA, PYTHON, } // add more languages here!

    private Lang language;

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

    Lang getLanguage() {
        return language;
    }

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
}
