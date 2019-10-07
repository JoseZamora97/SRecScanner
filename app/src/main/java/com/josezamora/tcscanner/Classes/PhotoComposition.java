package com.josezamora.tcscanner.Classes;

import android.net.Uri;

import java.io.Serializable;


public class PhotoComposition implements Serializable {

    private Uri photoUri;

    public PhotoComposition(Uri photoUri){
        this.photoUri = photoUri;
    }

    public Uri getPhotoUri() {
        return photoUri;
    }

}
