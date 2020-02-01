package com.josezamora.srecscanner.firebase.Classes;


import java.io.Serializable;

public class CloudNotebook implements Serializable {

    private String id;
    private String name;
    private String owner;
    private int numImages;
    private String language;
    public static final String NUM_IMAGES_KEY = "numImages";
    public static final String LANGUAGE_KEY = "language";
    public static final String CONTENT_KEY = "content";
    public static final String DIRTY_KEY = "dirty";
    private String content;
    private boolean dirty;

    public CloudNotebook(String id, String name, String owner) {
        this.id = id;
        this.name = name;
        this.owner = owner;
        this.numImages = 0;
        this.language = "none";
        this.content = "";
        this.dirty = false;
    }

    @SuppressWarnings("unused")
    public CloudNotebook() {
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getOwner() {
        return owner;
    }

    public int getNumImages() {
        return numImages;
    }

    public void setNumImages(int numImages) {
        this.numImages = numImages;
    }

    public String getLanguage() {
        return language;
    }

    public String getContent() {
        return content;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public boolean isDirty() {
        return dirty;
    }

    public void setDirty(boolean dirty) {
        this.dirty = dirty;
    }
}
