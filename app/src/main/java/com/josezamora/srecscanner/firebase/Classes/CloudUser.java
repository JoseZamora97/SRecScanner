package com.josezamora.srecscanner.firebase.Classes;

import com.google.firebase.auth.FirebaseUser;

import java.io.Serializable;
import java.util.Objects;

/**
 * The type Cloud user.
 */
@SuppressWarnings("unused")
public class CloudUser implements Serializable {

    private String uId;
    private String name;
    private String email;
    private String phoneNumber;

    private String photoUrl;

    /**
     * Instantiates a new Cloud user.
     */
    public CloudUser () {}

    private CloudUser(String uId, String name, String email, String phoneNumber, String photoUrl) {
        this.uId = uId;
        this.name = name;
        this.email = email;
        this.phoneNumber = phoneNumber;
        this.photoUrl = photoUrl;
    }

    /**
     * User from firebase cloud user.
     *
     * @param user the user
     * @return the cloud user
     */
    public static CloudUser userFromFirebase(FirebaseUser user) {
        return new CloudUser(user.getUid(),
                user.getDisplayName(),
                user.getEmail(),
                user.getPhoneNumber(),
                Objects.requireNonNull(user.getPhotoUrl()).toString());
    }

    /**
     * Gets id.
     *
     * @return the id
     */
    public String getuId() {
        return uId;
    }

    /**
     * Gets name.
     *
     * @return the name
     */
    public String getName() {
        return name;
    }

    /**
     * Gets email.
     *
     * @return the email
     */
    public String getEmail() {
        return email;
    }

    /**
     * Gets phone number.
     *
     * @return the phone number
     */
    public String getPhoneNumber() {
        return phoneNumber;
    }

    /**
     * Gets photo url.
     *
     * @return the photo url
     */
    public String getPhotoUrl() {
        return photoUrl;
    }
}
