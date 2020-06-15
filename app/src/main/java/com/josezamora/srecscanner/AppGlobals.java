package com.josezamora.srecscanner;

import android.Manifest;

public interface AppGlobals {

    String APP_SIGNATURE = "com.josezamora.srecscanner";

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

    /*
     * The constant MAX_PHOTOS_PER_NOTEBOOK.
     */
    int MAX_PHOTOS_PER_NOTEBOOK = 5;

    /**
     * The constant NOTEBOOK_KEY
     */
    String NOTEBOOK_KEY = "KEY_NOTEBOOK";

    /**
     * The constant KEY_IMAGES
     */
    String IMAGES_KEY = "KEY_IMAGES";

    /**
     * The constant USER_KEY
     */
    String USER_KEY = "USER_KEY";

    /**
     * The constant PREFERENCES_NAME
     */
    String PREFERENCES_NAME = "SREC_SCANNER_PREFERENCES";

    /**
     * The constant WEBSITE
     */
    String WEBSITE = "https://josezamora-tfg.web.app/";

    /**
     * VERSION
     */
    String VERSION = "v1.0.0";

    /**
     * ID TOKEN FOR OAUTH Protocol.
     */
    String ID_CLIENT_OAUTH_TOKEN = "370776486718-a7765hv30k559a7eg7s26bver5275" +
            "pku.apps.googleusercontent.com";
}
