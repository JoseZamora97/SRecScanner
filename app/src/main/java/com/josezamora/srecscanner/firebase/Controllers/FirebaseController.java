package com.josezamora.srecscanner.firebase.Controllers;

import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.josezamora.srecscanner.firebase.Classes.CloudImage;
import com.josezamora.srecscanner.firebase.Classes.CloudNotebook;
import com.josezamora.srecscanner.firebase.Classes.CloudUser;
import com.josezamora.srecscanner.firebase.Classes.Report;

import java.io.InputStream;
import java.util.Objects;


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

    public FirestoreRecyclerOptions<CloudNotebook> getRecyclerOptions(CloudUser user) {
        return databaseController.createRecyclerOptions(user);
    }

    public FirestoreRecyclerOptions<CloudNotebook> getRecyclerOptions(CloudUser user, String newText) {
        return databaseController.createFilterOptions(user, newText);
    }

    public FirestoreRecyclerOptions<CloudImage> getRecyclerOptions(CloudUser user, CloudNotebook notebook) {
        return databaseController.createRecyclerOptions(user, notebook);
    }

    public void addNotebook(CloudNotebook notebook) {
        this.databaseController.addNotebook(notebook);
    }

    public void deleteNotebook(CloudNotebook notebook, boolean definitive) {
        if (!definitive) databaseController.deleteNotebook(notebook);
        else
            databaseController.deleteNotebookDefinitive(notebook);
            // TODO: clean storage too.
    }

    public UploadTask uploadImage(final CloudUser user, final CloudNotebook notebook, InputStream stream) {

        final String imageId = String.valueOf(System.currentTimeMillis());

        UploadTask uploadTask = storageController.upload(imageId, notebook, stream);
        StorageReference imgRef = storageController.getReference(imageId, notebook);

        uploadTask.continueWithTask(task -> imgRef.getDownloadUrl())
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        notebook.setNumImages(notebook.getNumImages() + 1);
                        CloudImage image = new CloudImage(
                                imageId,
                                user.getuId(),
                                notebook.getId(),
                                imgRef.getPath(),
                                Objects.requireNonNull(task.getResult()).toString(),
                                notebook.getNumImages());

                        databaseController.addImage(image);
                        databaseController.updateNotebook(notebook, CloudNotebook.NUM_IMAGES_KEY);
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

    public void update(CloudNotebook notebook, String field) {
        databaseController.updateNotebook(notebook, field);
    }

    public void sendReport(Report report) {
        databaseController.sendReport(report);
    }
}
