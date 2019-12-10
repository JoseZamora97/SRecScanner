package com.josezamora.tcscanner.Firebase.Controllers;

import android.content.Context;
import android.net.Uri;
import android.view.View;
import android.widget.ProgressBar;

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
}
