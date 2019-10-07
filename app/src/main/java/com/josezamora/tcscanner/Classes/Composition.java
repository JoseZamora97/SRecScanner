package com.josezamora.tcscanner.Classes;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Composition implements Serializable {

    private long id;
    private String name;

    private List<PhotoComposition> listUris;
    private String absolutePath;

    public Composition(String name) {
        this.name = name;
        this.listUris = new ArrayList<>();
        this.id = System.currentTimeMillis();
    }

    public long getId() {
        return id;
    }

    public String getName () {
        return this.name;
    }

    public int getNumImages () {
        return this.listUris.size();
    }

    public List<PhotoComposition> getListUris() {
        return listUris;
    }

    public void addPhoto(PhotoComposition newPhoto) {
        this.listUris.add(newPhoto);
    }

    public PhotoComposition removePhoto(int position) {
        return listUris.remove(position);
    }

    public void addPhotoAt(int position, PhotoComposition uriDeleted) {
        listUris.add(position, uriDeleted);
    }

    public void setAbsolutePath(String absolutePath) {
        this.absolutePath = absolutePath;
    }

    public String getAbsolutePath() {
        return absolutePath;
    }
}
