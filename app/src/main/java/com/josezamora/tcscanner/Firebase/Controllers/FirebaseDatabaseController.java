package com.josezamora.tcscanner.Firebase.Controllers;

import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.josezamora.tcscanner.Classes.Composition;
import com.josezamora.tcscanner.Firebase.Classes.CloudComposition;
import com.josezamora.tcscanner.Firebase.Classes.CloudImage;
import com.josezamora.tcscanner.Firebase.Classes.CloudUser;

import java.util.List;
import java.util.Objects;

import androidx.annotation.NonNull;

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

    private DocumentReference getReference(CloudComposition composition) {
        return database.collection(USERS)
                .document(composition.getOwner())
                .collection(COMPOSITIONS)
                .document(composition.getId());
    }

    public void addComposition(CloudComposition composition) {
        DocumentReference docRef = getReference(composition);
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

    public FirestoreRecyclerOptions<CloudComposition> recyclerOptions(CloudUser user) {
        Query query = FirebaseFirestore.getInstance()
                .collection(USERS)
                .document(user.getuId())
                .collection(COMPOSITIONS);

        return new FirestoreRecyclerOptions.Builder<CloudComposition>()
                .setQuery(query, CloudComposition.class)
                .build();
    }

    public void deleteComposition(CloudComposition composition) {
        DocumentReference docRef = getReference(composition);
        final CollectionReference colRef = docRef.collection(IMAGES);

        colRef.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                for(QueryDocumentSnapshot documentSnapshot :
                        Objects.requireNonNull(task.getResult())) {
                    colRef.document(documentSnapshot.getId()).delete();
                }
            }
        });

        docRef.delete();
    }
}
