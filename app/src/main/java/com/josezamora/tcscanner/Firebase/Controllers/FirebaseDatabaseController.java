package com.josezamora.tcscanner.Firebase.Controllers;

import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.josezamora.tcscanner.Firebase.Classes.CloudComposition;
import com.josezamora.tcscanner.Firebase.Classes.CloudImage;
import com.josezamora.tcscanner.Firebase.Classes.CloudUser;

import java.util.Objects;

import androidx.annotation.NonNull;

@SuppressWarnings("SpellCheckingInspection")
class FirebaseDatabaseController {

    private FirebaseFirestore database;

    private static final String USERS = "users";
    private static final String COMPOSITIONS = "compositions";
    private static final String IMAGES = "photos";

    private static final String FIELD_NAME = "name";

    FirebaseDatabaseController() {
        database = FirebaseFirestore.getInstance();
    }

    void createUser(CloudUser user) {
        database.collection(USERS).document(user.getuId()).set(user);
    }

    private DocumentReference getReference(CloudComposition composition) {
        return database.collection(USERS)
                .document(composition.getOwner())
                .collection(COMPOSITIONS)
                .document(composition.getId());
    }

    private DocumentReference getReference(CloudImage image) {
        return database.collection(USERS)
                .document(image.getOwner())
                .collection(COMPOSITIONS)
                .document(image.getComposition())
                .collection(IMAGES)
                .document(image.getId());
    }

    void addComposition(CloudComposition composition) {
        DocumentReference docRef = getReference(composition);
        docRef.set(composition);
    }

    void addImage(CloudImage image) {
        DocumentReference docRef = getReference(image);
        docRef.set(image);
    }

    void deleteComposition(CloudComposition composition) {
        DocumentReference docRef = getReference(composition);
        docRef.delete();
    }

    void deleteCompositionDefinitive(CloudComposition composition) {
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

    void deleteImage(CloudImage image) {
        DocumentReference docRef = getReference(image);
        docRef.delete();
    }

    FirestoreRecyclerOptions<CloudComposition> createFilterOptions(CloudUser user, String newText) {
        Query query = FirebaseFirestore.getInstance()
                .collection(USERS)
                .document(user.getuId())
                .collection(COMPOSITIONS)
                .whereGreaterThanOrEqualTo(FIELD_NAME, newText)
                .whereLessThanOrEqualTo(FIELD_NAME, newText+"z");

        return setQuery(query);
    }

    FirestoreRecyclerOptions<CloudComposition> createRecyclerOptions(CloudUser user) {
        Query query = FirebaseFirestore.getInstance()
                .collection(USERS)
                .document(user.getuId())
                .collection(COMPOSITIONS);

        return setQuery(query);
    }

    private FirestoreRecyclerOptions<CloudComposition> setQuery(Query query) {
        return new FirestoreRecyclerOptions.Builder<CloudComposition>()
                .setQuery(query, CloudComposition.class)
                .build();
    }

    FirestoreRecyclerOptions<CloudImage> createRecyclerOptions(CloudUser user,
                                                               CloudComposition composition) {
        Query query = FirebaseFirestore.getInstance()
                .collection(USERS)
                .document(user.getuId())
                .collection(COMPOSITIONS)
                .document(composition.getId())
                .collection(IMAGES);

        return new FirestoreRecyclerOptions.Builder<CloudImage>()
                .setQuery(query, CloudImage.class)
                .build();
    }
}
