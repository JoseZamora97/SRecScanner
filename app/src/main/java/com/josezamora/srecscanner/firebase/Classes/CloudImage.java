package com.josezamora.srecscanner.firebase.Classes;

import java.io.Serializable;

public class CloudImage implements Serializable {

    private String id;
    private String owner;
    private String notebook;
    private String firebaseStoragePath;
    private String downloadLink;
    private int order;

    @SuppressWarnings("unused")
    public CloudImage() {}

    public CloudImage(String id, String owner, String notebook, String firebaseStoragePath,
                      String downloadLink, int order ){
        this.id = id;
        this.owner = owner;
        this.notebook = notebook;
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

    public String getNotebook() {
        return notebook;
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
