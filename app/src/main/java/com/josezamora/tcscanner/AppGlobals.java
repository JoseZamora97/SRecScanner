package com.josezamora.tcscanner;

import android.Manifest;

import com.firebase.ui.auth.AuthUI;

import java.util.Arrays;
import java.util.List;

public interface AppGlobals {

    String APP_SIGNATURE = "com.josezamora.tcscanner";

    /**
     * The Required permissions.
     */
    String[] REQUIRED_PERMISSIONS = {
            Manifest.permission.CAMERA,
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE};

    /**
     * The constant REQUEST_CODE_PERMISSIONS.
     */
    int REQUEST_CODE_PERMISSIONS = 33;

    /**
     * The constant REQUEST_CODE_CAMERA.
     */
    int REQUEST_CODE_CAMERA = 0;

    /**
     * The constant REQUEST_CODE_STORAGE.
     */
    int REQUEST_CODE_STORAGE = 1;

    /**
     * The constant REQUEST_CODE_QR.
     */
    int REQUEST_CODE_QR = 2;

    /**
     * The constant REQUEST_CODE_SIGN_IN.
     */
    int REQUEST_CODE_SIGN_IN = 3;

    /**
     * The constant REQUEST_CODE_CROP.
     */
    int REQUEST_CODE_CROP = 4;

    /*
     * The constant MAX_PHOTOS_PER_COMPOSITION.
     */
    int MAX_PHOTOS_PER_COMPOSITION = 5;

    /**
     * The constant COMPOSITION_KEY
     */
    String COMPOSITION_KEY = "KEY_COMPOSITION";

    List<AuthUI.IdpConfig> PROVIDERS = Arrays.asList(
            new AuthUI.IdpConfig.EmailBuilder().build(),
            new AuthUI.IdpConfig.GoogleBuilder().build());

    String IMAGES_KEY = "KEY_IMAGES";

    String VERSION = "v0.9";
}
