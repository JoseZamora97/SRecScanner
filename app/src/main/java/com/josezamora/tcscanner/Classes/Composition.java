package com.josezamora.tcscanner.Classes;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Composition implements Serializable {

    private long id;
    private String name;
    private List<ImageComposition> listPhotos;
    private String absolutePath;

    private String url;

    public Composition(String name) {
        this.id = System.currentTimeMillis();
        this.name = name;
        this.listPhotos = new ArrayList<>();
        this.url = "";
    }

    public long getId() {
        return id;
    }

    public String getName () {
        return this.name;
    }

    public int getNumImages () {
        return this.listPhotos.size();
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public List<ImageComposition> getListPhotos() {
        return listPhotos;
    }

    public void addPhoto(ImageComposition newPhoto) {
        this.listPhotos.add(newPhoto);
    }

    public ImageComposition removePhoto(int position) {
        return listPhotos.remove(position);
    }

    public void addPhotoAt(int position, ImageComposition uriDeleted) {
        listPhotos.add(position, uriDeleted);
    }

    public void setAbsolutePath(String absolutePath) {
        this.absolutePath = absolutePath;
    }

    public String getAbsolutePath() {
        return absolutePath;
    }
}
