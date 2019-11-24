package com.josezamora.tcscanner.Firebase.Classes;

import java.io.Serializable;

public class CloudComposition implements Serializable {

    private String id;
    private String name;
    private String owner;

    private int numImages;

    public CloudComposition(String id, String name, String owner) {
        this.id = id;
        this.name = name;
        this.owner = owner;
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

}
