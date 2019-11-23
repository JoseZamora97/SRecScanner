package com.josezamora.tcscanner.Firebase.Controllers;

import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.josezamora.tcscanner.Firebase.Classes.CloudComposition;
import com.josezamora.tcscanner.Firebase.Classes.CloudImage;
import com.josezamora.tcscanner.Firebase.Classes.CloudUser;

public class FirebaseDatabaseController {

    private FirebaseFirestore database;

    private static final String USERS = "users";
    private static final String COMPOSITIONS = "compositions";
    private static final String IMAGES = "photos";

    public FirebaseDatabaseController() {
        database = FirebaseFirestore.getInstance();
    }

    public void createUser(CloudUser user) {
        database.collection(USERS).document(user.getuId()).set(user);
    }

    public void addComposition(CloudComposition composition) {
        DocumentReference docRef = database.collection(USERS)
                .document(composition.getOwner())
                .collection(COMPOSITIONS)
                .document(composition.getId());

        docRef.set(composition);
    }

    public void addImage(CloudImage image) {
        DocumentReference docRef = database.collection(USERS)
                .document(image.getOwner())
                .collection(COMPOSITIONS)
                .document(image.getComposition())
                .collection(IMAGES)
                .document(image.getId());

        docRef.set(image);
    }

}
