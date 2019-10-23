package com.josezamora.tcscanner.Classes;

import java.io.Serializable;


public class PhotoComposition implements Serializable {

    private String photoUri;
    private String thumbnailUri;

    public PhotoComposition(String photoUri, String thumbnailUri){
        this.photoUri = photoUri;
        this.thumbnailUri = thumbnailUri;
    }

    public String getPhotoUri() {
        return photoUri;
    }

    public String getThumbnailUri() {
        return thumbnailUri;
    }
}
