package com.josezamora.tcscanner.Firebase.Controllers;

import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.josezamora.tcscanner.Firebase.Classes.CloudImage;
import com.josezamora.tcscanner.Firebase.Classes.CloudNotebook;
import com.josezamora.tcscanner.Firebase.Classes.CloudUser;
import com.josezamora.tcscanner.Firebase.Classes.Report;

import java.util.Objects;

@SuppressWarnings("SpellCheckingInspection")
class FirebaseDatabaseController {

    private FirebaseFirestore database;

    private static final String USERS = "users";
    private static final String NOTEBOOKS = "notebooks";
    private static final String IMAGES = "photos";
    private static final String REPORTS = "reports";

    private static final String FIELD_NAME = "name";

    FirebaseDatabaseController() {
        database = FirebaseFirestore.getInstance();
    }

    void createUser(CloudUser user) {
        database.collection(USERS).document(user.getuId()).set(user);
    }

    private DocumentReference getReference(CloudNotebook notebook) {
        return database.collection(USERS)
                .document(notebook.getOwner())
                .collection(NOTEBOOKS)
                .document(notebook.getId());
    }

    private DocumentReference getReference(CloudImage image) {
        return database.collection(USERS)
                .document(image.getOwner())
                .collection(NOTEBOOKS)
                .document(image.getNotebook())
                .collection(IMAGES)
                .document(image.getId());
    }

    void addNotebook(CloudNotebook notebook) {
        DocumentReference docRef = getReference(notebook);
        docRef.set(notebook);
    }

    void addImage(CloudImage image) {
        DocumentReference docRef = getReference(image);
        docRef.set(image);
    }

    void deleteNotebook(CloudNotebook notebook) {
        DocumentReference docRef = getReference(notebook);
        docRef.delete();
    }

    void deleteNotebookDefinitive(CloudNotebook notebook) {
        DocumentReference docRef = getReference(notebook);
        final CollectionReference colRef = docRef.collection(IMAGES);

        colRef.get().addOnCompleteListener(task -> {
            for (QueryDocumentSnapshot documentSnapshot :
                    Objects.requireNonNull(task.getResult()))
                colRef.document(documentSnapshot.getId()).delete();
        });

        docRef.delete();
    }

    void deleteImage(CloudImage image) {
        DocumentReference docRef = getReference(image);
        docRef.delete();
    }

    FirestoreRecyclerOptions<CloudNotebook> createFilterOptions(CloudUser user, String newText) {
        Query query = FirebaseFirestore.getInstance()
                .collection(USERS)
                .document(user.getuId())
                .collection(NOTEBOOKS)
                .whereGreaterThanOrEqualTo(FIELD_NAME, newText)
                .whereLessThanOrEqualTo(FIELD_NAME, newText+"z");

        return setQuery(query);
    }

    FirestoreRecyclerOptions<CloudNotebook> createRecyclerOptions(CloudUser user) {
        Query query = FirebaseFirestore.getInstance()
                .collection(USERS)
                .document(user.getuId())
                .collection(NOTEBOOKS);

        return setQuery(query);
    }

    private FirestoreRecyclerOptions<CloudNotebook> setQuery(Query query) {
        return new FirestoreRecyclerOptions.Builder<CloudNotebook>()
                .setQuery(query, CloudNotebook.class)
                .build();
    }

    FirestoreRecyclerOptions<CloudImage> createRecyclerOptions(CloudUser user,
                                                               CloudNotebook notebook) {
        Query query = FirebaseFirestore.getInstance()
                .collection(USERS)
                .document(user.getuId())
                .collection(NOTEBOOKS)
                .document(notebook.getId())
                .collection(IMAGES).orderBy("order");

        return new FirestoreRecyclerOptions.Builder<CloudImage>()
                .setQuery(query, CloudImage.class)
                .build();
    }

    void updateNotebook(CloudNotebook notebook, String field) {
        DocumentReference docRef = getReference(notebook);
        switch (field) {
            case CloudNotebook.NUM_IMAGES_KEY:
                docRef.update(field, notebook.getNumImages());
                break;
            case CloudNotebook.LANGUAGE_KEY:
                docRef.update(field, notebook.getLanguage());
                break;
            case CloudNotebook.CONTENT_KEY:
                docRef.update(field, notebook.getContent());
                break;
            case CloudNotebook.DIRTY_KEY:
                docRef.update(field, notebook.isDirty());
                break;
        }
    }

    void updateImage(CloudImage image) {
        DocumentReference docRef = getReference(image);
        docRef.update("order", image.getOrder());
    }

    void sendReport(Report report) {
        database.collection(REPORTS).document(report.getId()).set(report);
    }
}
