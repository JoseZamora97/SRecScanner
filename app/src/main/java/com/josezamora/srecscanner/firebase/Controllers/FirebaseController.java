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


/**
 * The type Firebase controller.
 */
public class FirebaseController {

    private FirebaseStorageController storageController;
    private FirebaseDatabaseController databaseController;

    /**
     * Instantiates a new Firebase controller.
     */
    public FirebaseController() {
        this.storageController = new FirebaseStorageController();
        this.databaseController = new FirebaseDatabaseController();
    }

    /**
     * Create user.
     *
     * @param user the user
     */
    public void createUser(CloudUser user) {
        databaseController.createUser(user);
    }

    /**
     * Gets recycler options.
     *
     * @param user the user
     * @return the recycler options
     */
    public FirestoreRecyclerOptions<CloudNotebook> getRecyclerOptions(CloudUser user) {
        return databaseController.createRecyclerOptions(user);
    }

    /**
     * Gets recycler options.
     *
     * @param user    the user
     * @param newText the new text
     * @return the recycler options
     */
    public FirestoreRecyclerOptions<CloudNotebook> getRecyclerOptions(CloudUser user, String newText) {
        return databaseController.createFilterOptions(user, newText);
    }

    /**
     * Gets recycler options.
     *
     * @param user     the user
     * @param notebook the notebook
     * @return the recycler options
     */
    public FirestoreRecyclerOptions<CloudImage> getRecyclerOptions(CloudUser user, CloudNotebook notebook) {
        return databaseController.createRecyclerOptions(user, notebook);
    }

    /**
     * Add notebook.
     *
     * @param notebook the notebook
     */
    public void addNotebook(CloudNotebook notebook) {
        this.databaseController.addNotebook(notebook);
    }

    /**
     * Delete notebook.
     *
     * @param notebook   the notebook
     * @param definitive the definitive
     */
    public void deleteNotebook(CloudNotebook notebook, boolean definitive) {
        if (!definitive) databaseController.deleteNotebook(notebook);
        else
            databaseController.deleteNotebookDefinitive(notebook);
        // TODO: clean storage too.
    }

    /**
     * Upload image upload task.
     *
     * @param user     the user
     * @param notebook the notebook
     * @param stream   the stream
     * @return the upload task
     */
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

    /**
     * Delete image.
     *
     * @param image      the image
     * @param definitive the definitive
     */
    public void deleteImage(CloudImage image, boolean definitive) {
        databaseController.deleteImage(image);
        if(definitive) storageController.delete(image);
    }

    /**
     * Add image.
     *
     * @param image the image
     */
    public void addImage(CloudImage image) {
        databaseController.addImage(image);
    }

    /**
     * Update.
     *
     * @param image the image
     */
    public void update(CloudImage image) {
        databaseController.updateImage(image);
    }

    /**
     * Update.
     *
     * @param notebook the notebook
     * @param field    the field
     */
    public void update(CloudNotebook notebook, String field) {
        databaseController.updateNotebook(notebook, field);
    }

    /**
     * Send report.
     *
     * @param report the report
     */
    public void sendReport(Report report) {
        databaseController.sendReport(report);
    }
}
