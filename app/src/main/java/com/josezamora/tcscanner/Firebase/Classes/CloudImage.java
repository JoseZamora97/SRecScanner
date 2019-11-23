package com.josezamora.tcscanner.Firebase.Classes;

public class CloudImage {

    private String id;
    private String owner;
    private String composition;
    private String firebaseStoragePath;

    public CloudImage() {}

    public CloudImage(String id, String owner, String composition, String firebaseStoragePath) {
        this.id = id;
        this.owner = owner;
        this.composition = composition;
        this.firebaseStoragePath = firebaseStoragePath;
    }

    public String getId() {
        return id;
    }

    public String getOwner() {
        return owner;
    }

    public String getComposition() {
        return composition;
    }

    public String getFirebaseStoragePath() {
        return firebaseStoragePath;
    }
}
