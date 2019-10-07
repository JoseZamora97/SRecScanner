package com.josezamora.tcscanner.Interfaces;

import android.Manifest;

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
     * The constant TO_COMPOSITION_KEY
     */
    String TO_COMPOSITION_KEY = "KEY_COMPOSITION";

    /**
     * The constant COMPOSITIONS_FILENAME
     */
    String COMPOSITIONS_FILENAME = "Compositions";

    /**
     * The constant IMAGE_FORMAT
     */
    String IMAGE_FORMAT = ".png";
}
