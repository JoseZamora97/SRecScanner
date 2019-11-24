package com.josezamora.tcscanner.Firebase.Classes;

public class CloudImage {

    private String id;
    private String owner;
    private String composition;
    private String firebaseStoragePath;
    private String downloadLink;

    public CloudImage() {}

    public CloudImage(String id, String owner, String composition, String firebaseStoragePath, String downloadLink) {
        this.id = id;
        this.owner = owner;
        this.composition = composition;
        this.firebaseStoragePath = firebaseStoragePath;
        this.downloadLink = downloadLink;
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

    public String getDownloadLink() {
        return downloadLink;
    }
}
