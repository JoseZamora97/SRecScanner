package com.josezamora.tcscanner.Firebase.Controllers;

import android.net.Uri;

import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.josezamora.tcscanner.Firebase.Classes.CloudComposition;
import com.josezamora.tcscanner.Firebase.Classes.CloudImage;
import com.josezamora.tcscanner.Firebase.Classes.CloudUser;

import java.io.InputStream;
import java.util.Objects;

import androidx.annotation.NonNull;

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

    public UploadTask uploadImage(final CloudUser user, final CloudComposition composition, InputStream stream) {
        final String imageId = String.valueOf(System.currentTimeMillis());

        final StorageReference imgRef = storageController.getStorage().getReference()
                .child(user.getuId())
                .child(composition.getId())
                .child(imageId)
                .child(imageId + ".jpg");

        UploadTask uploadTask = imgRef.putStream(stream);
        uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                    @Override
                    public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                        return imgRef.getDownloadUrl();
                    }
                })
                .addOnCompleteListener(new OnCompleteListener<Uri>() {
                    @Override
                    public void onComplete(@NonNull Task<Uri> task) {
                        if(task.isSuccessful()) {
                            composition.setNumImages(composition.getNumImages() + 1);
                            CloudImage image = new CloudImage(imageId, user.getuId(), composition.getId(),
                                    imgRef.getPath(), Objects.requireNonNull(task.getResult()).toString(), composition.getNumImages());
                            databaseController.addImage(image);
                            databaseController.updateComposition(composition);
                        }
                    }
                });
        return uploadTask;
    }

    public void deleteImage(CloudImage image, boolean definitive) {
        databaseController.deleteImage(image);
        if(definitive) storageController.delete(image);
    }

    public void addImage(CloudImage image) {
        databaseController.addImage(image);
    }

    public void update(CloudImage image) {
        databaseController.updateImage(image);
    }

    public void update(CloudComposition composition) {
        databaseController.updateComposition(composition);
    }
}
