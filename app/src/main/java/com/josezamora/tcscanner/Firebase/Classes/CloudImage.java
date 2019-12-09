package com.josezamora.tcscanner.Firebase.Classes;

public class CloudImage {

    private String id;
    private String owner;
    private String composition;
    private String firebaseStoragePath;
    private String downloadLink;
    private int order;

    public CloudImage() {}

    public CloudImage(String id, String owner, String composition, String firebaseStoragePath,
                      String downloadLink, int order ){
        this.id = id;
        this.owner = owner;
        this.composition = composition;
        this.firebaseStoragePath = firebaseStoragePath;
        this.downloadLink = downloadLink;
        this.order = order;
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

    public int getOrder() {
        return order;
    }

    public void setOrder(int order) {
        this.order = order;
    }
}
