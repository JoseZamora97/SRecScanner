package com.josezamora.tcscanner.Firebase.Controllers;

import android.net.Uri;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.josezamora.tcscanner.Firebase.Classes.CloudComposition;
import com.josezamora.tcscanner.Firebase.Classes.CloudImage;
import com.josezamora.tcscanner.Firebase.Classes.CloudUser;

import java.io.InputStream;

import androidx.annotation.NonNull;

@SuppressWarnings("SpellCheckingInspection")
class FirebaseStorageController {

    private FirebaseStorage storage;
    private Task<Uri> urlTask;

    private FirebaseDatabaseController controller;

    FirebaseStorageController() {
        storage = FirebaseStorage.getInstance();
        controller = new FirebaseDatabaseController();
    }

    void uploadImage(final CloudUser user, final CloudComposition composition, InputStream data) {

        final String imageId = String.valueOf(System.currentTimeMillis());

        final StorageReference imgRef = storage.getReference()
                .child(user.getuId())
                .child(composition.getId())
                .child(imageId)
                .child(imageId + ".jpg");

        UploadTask uploadTask = imgRef.putStream(data);

        urlTask = uploadTask
                .continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                    @Override
                    public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                        return imgRef.getDownloadUrl();
                    }
                })
                .addOnCompleteListener(new OnCompleteListener<Uri>() {
                    @Override
                    public void onComplete(@NonNull Task<Uri> task) {
                        Uri downloadUri = task.getResult();
                        assert downloadUri != null;
                        composition.setNumImages(composition.getNumImages() + 1);
                        CloudImage image = new CloudImage(imageId, user.getuId(), composition.getId(),
                                imgRef.getPath(), downloadUri.toString(), composition.getNumImages());
                        controller.addImage(image);
                    }
                });
    }

    StorageReference getReference(CloudImage image) {
        StorageReference storageReference = storage.getReference();
        return storageReference.child(image.getFirebaseStoragePath());
    }

    void delete(CloudImage image) {
        StorageReference storageReference = getReference(image);
        storageReference.delete();
    }
}
