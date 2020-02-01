package com.josezamora.tcscanner.Firebase.Controllers;

import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.josezamora.tcscanner.Firebase.Classes.CloudImage;
import com.josezamora.tcscanner.Firebase.Classes.CloudNotebook;

import java.io.InputStream;

@SuppressWarnings("SpellCheckingInspection")
class FirebaseStorageController {

    private FirebaseStorage storage;

    FirebaseStorageController() {
        storage = FirebaseStorage.getInstance();
    }

    FirebaseStorage getStorage() {
        return storage;
    }

    StorageReference getReference(CloudImage image) {
        StorageReference storageReference = storage.getReference();
        return storageReference.child(image.getFirebaseStoragePath());
    }

    void delete(CloudImage image) {
        StorageReference storageReference = getReference(image);
        storageReference.delete();
    }

    StorageReference getReference(String imageId, CloudNotebook notebook) {
        return getStorage().getReference()
                .child(notebook.getOwner())
                .child(notebook.getId())
                .child(imageId)
                .child(imageId + ".jpg");
    }

    UploadTask upload(String imageId, CloudNotebook notebook, InputStream stream) {
        StorageReference imgRef = getReference(imageId, notebook);
        return imgRef.putStream(stream);
    }
}
