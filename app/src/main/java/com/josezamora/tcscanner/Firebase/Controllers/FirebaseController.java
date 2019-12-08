package com.josezamora.tcscanner.Firebase.Controllers;

import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.storage.StorageReference;
import com.josezamora.tcscanner.Firebase.Classes.CloudComposition;
import com.josezamora.tcscanner.Firebase.Classes.CloudImage;
import com.josezamora.tcscanner.Firebase.Classes.CloudUser;

import java.io.InputStream;

@SuppressWarnings("SpellCheckingInspection")
public class FirebaseController {

    private FirebaseStorageController storageController;
    private FirebaseDatabaseController databaseController;

    public FirebaseController() {
        this.storageController = new FirebaseStorageController();
        this.databaseController = new FirebaseDatabaseController();
    }

    public void createUser(CloudUser user) {
        databaseController.createUser(user);
    }

    public FirestoreRecyclerOptions<CloudComposition>  getRecyclerOptions(CloudUser user) {
        return databaseController.createRecyclerOptions(user);
    }

    public FirestoreRecyclerOptions<CloudComposition> getRecyclerOptions(CloudUser user, String newText) {
        return databaseController.createFilterOptions(user, newText);
    }

    public FirestoreRecyclerOptions<CloudImage> getRecyclerOptions(CloudUser user, CloudComposition composition) {
        return databaseController.createRecyclerOptions(user, composition);
    }

    public void addComposition(CloudComposition composition) {
        this.databaseController.addComposition(composition);
    }

    public void deleteComposition(CloudComposition composition, boolean definitive) {
        if(!definitive) databaseController.deleteComposition(composition);
        else
            databaseController.deleteCompositionDefinitive(composition);
            // Todo: clean storage too.
    }

    public StorageReference getReference(CloudImage image) {
        return storageController.getReference(image);
    }

    public void uploadImage(CloudUser user, CloudComposition composition, InputStream stream) {
        storageController.uploadImage(user, composition, stream);
    }

    public void deleteImage(CloudImage image, boolean definitive) {
        databaseController.deleteImage(image);
        if(definitive) storageController.delete(image);
    }

    public void addImage(CloudImage image) {
        databaseController.addImage(image);
    }
}
