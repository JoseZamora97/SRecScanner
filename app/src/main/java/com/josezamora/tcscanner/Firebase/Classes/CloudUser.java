package com.josezamora.tcscanner.Firebase.Classes;

import com.google.firebase.auth.FirebaseUser;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class CloudUser implements Serializable {

    private String uId;
    private String name;
    private String email;
    private String phoneNumber;

    private String photoUrl;

    public CloudUser () {}

    public CloudUser(String uId, String name, String email, String phoneNumber, String photoUrl) {
        this.uId = uId;
        this.name = name;
        this.email = email;
        this.phoneNumber = phoneNumber;
        this.photoUrl = photoUrl;
    }

    public String getuId() {
        return uId;
    }

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public String getPhotoUrl() {
        return photoUrl;
    }

    public static CloudUser userFromFirebase(FirebaseUser user) {
        return new CloudUser(user.getUid(),
                user.getDisplayName(),
                user.getEmail(),
                user.getPhoneNumber(),
                Objects.requireNonNull(user.getPhotoUrl()).toString());
    }
}
