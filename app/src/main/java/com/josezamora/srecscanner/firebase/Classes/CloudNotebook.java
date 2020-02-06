package com.josezamora.srecscanner.firebase.Classes;


import java.io.Serializable;

/**
 * The type Cloud notebook.
 */
public class CloudNotebook implements Serializable {

    private String id;
    private String name;
    private String owner;
    private int numImages;
    private String language;
    /**
     * The constant NUM_IMAGES_KEY.
     */
    public static final String NUM_IMAGES_KEY = "numImages";
    /**
     * The constant LANGUAGE_KEY.
     */
    public static final String LANGUAGE_KEY = "language";
    /**
     * The constant CONTENT_KEY.
     */
    public static final String CONTENT_KEY = "content";
    /**
     * The constant DIRTY_KEY.
     */
    public static final String DIRTY_KEY = "dirty";
    private String content;
    private boolean dirty;

    /**
     * Instantiates a new Cloud notebook.
     *
     * @param id    the id
     * @param name  the name
     * @param owner the owner
     */
    public CloudNotebook(String id, String name, String owner) {
        this.id = id;
        this.name = name;
        this.owner = owner;
        this.numImages = 0;
        this.language = "none";
        this.content = "";
        this.dirty = false;
    }

    /**
     * Instantiates a new Cloud notebook.
     */
    @SuppressWarnings("unused")
    public CloudNotebook() {
    }

    /**
     * Gets id.
     *
     * @return the id
     */
    public String getId() {
        return id;
    }

    /**
     * Gets name.
     *
     * @return the name
     */
    public String getName() {
        return name;
    }

    /**
     * Gets owner.
     *
     * @return the owner
     */
    public String getOwner() {
        return owner;
    }

    /**
     * Gets num images.
     *
     * @return the num images
     */
    public int getNumImages() {
        return numImages;
    }

    /**
     * Sets num images.
     *
     * @param numImages the num images
     */
    public void setNumImages(int numImages) {
        this.numImages = numImages;
    }

    /**
     * Gets language.
     *
     * @return the language
     */
    public String getLanguage() {
        return language;
    }

    /**
     * Sets language.
     *
     * @param language the language
     */
    public void setLanguage(String language) {
        this.language = language;
    }

    /**
     * Gets content.
     *
     * @return the content
     */
    public String getContent() {
        return content;
    }

    /**
     * Sets content.
     *
     * @param content the content
     */
    public void setContent(String content) {
        this.content = content;
    }

    /**
     * Is dirty boolean.
     *
     * @return the boolean
     */
    public boolean isDirty() {
        return dirty;
    }

    /**
     * Sets dirty.
     *
     * @param dirty the dirty
     */
    public void setDirty(boolean dirty) {
        this.dirty = dirty;
    }
}
