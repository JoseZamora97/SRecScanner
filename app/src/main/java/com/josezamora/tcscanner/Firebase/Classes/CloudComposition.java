package com.josezamora.tcscanner.Firebase.Classes;


import java.io.Serializable;

public class CloudComposition implements Serializable {

    private String id;
    private String name;
    private String owner;
    private int numImages;
    private String language;

    public CloudComposition(String id, String name, String owner) {
        this.id = id;
        this.name = name;
        this.owner = owner;
        this.numImages = 0;
        this.language = "none";
    }

    public CloudComposition() {}

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

    public void setLanguage(String language) {
        this.language = language;
    }
}
