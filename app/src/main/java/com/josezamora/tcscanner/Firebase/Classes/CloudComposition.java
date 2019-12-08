package com.josezamora.tcscanner.Firebase.Classes;


import java.io.Serializable;
import java.util.HashMap;

public class CloudComposition implements Serializable {

    private String id;
    private String name;
    private String owner;

    private HashMap<String, Integer> idPhotos;

    public CloudComposition(String id, String name, String owner) {
        this.id = id;
        this.name = name;
        this.owner = owner;
        this.idPhotos = new HashMap<>();
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

    public HashMap<String, Integer> getIdPhotos() {
        return idPhotos;
    }

    public void setIdPhotos(HashMap<String, Integer> idPhotos){
        this.idPhotos = idPhotos;
    }
}
