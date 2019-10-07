package com.josezamora.tcscanner.Classes;

import java.io.Serializable;


public class PhotoComposition implements Serializable {

    private String photoUri;

    public PhotoComposition(String photoUri){
        this.photoUri = photoUri;
    }

    public String getPhotoUri() {
        return photoUri;
    }

}
