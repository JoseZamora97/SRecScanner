package com.josezamora.srecscanner.firebase.Controllers;

import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.josezamora.srecscanner.firebase.Classes.CloudImage;
import com.josezamora.srecscanner.firebase.Classes.CloudNotebook;

import java.io.InputStream;


/**
 * The type Firebase storage controller.
 */
class FirebaseStorageController {

    private FirebaseStorage storage;

    /**
     * Instantiates a new Firebase storage controller.
     */
    FirebaseStorageController() {
        storage = FirebaseStorage.getInstance();
    }

    private FirebaseStorage getStorage() {
        return storage;
    }

    private StorageReference getReference(CloudImage image) {
        StorageReference storageReference = storage.getReference();
        return storageReference.child(image.getFirebaseStoragePath());
    }

    /**
     * Delete.
     *
     * @param image the image
     */
    void delete(CloudImage image) {
        StorageReference storageReference = getReference(image);
        storageReference.delete();
    }

    /**
     * Gets reference.
     *
     * @param imageId  the image id
     * @param notebook the notebook
     * @return the reference
     */
    StorageReference getReference(String imageId, CloudNotebook notebook) {
        return getStorage().getReference()
                .child(notebook.getOwner())
                .child(notebook.getId())
                .child(imageId)
                .child(imageId + ".jpg");
    }

    /**
     * Upload upload task.
     *
     * @param imageId  the image id
     * @param notebook the notebook
     * @param stream   the stream
     * @return the upload task
     */
    UploadTask upload(String imageId, CloudNotebook notebook, InputStream stream) {
        StorageReference imgRef = getReference(imageId, notebook);
        return imgRef.putStream(stream);
    }
}
