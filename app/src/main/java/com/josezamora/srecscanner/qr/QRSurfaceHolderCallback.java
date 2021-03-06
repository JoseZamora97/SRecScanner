package com.josezamora.srecscanner.qr;

import android.view.SurfaceHolder;
import android.view.SurfaceView;

import com.google.android.gms.vision.CameraSource;

import java.io.IOException;

/**
 * The type Qr surface holder callback.
 */
public class QRSurfaceHolderCallback implements SurfaceHolder.Callback {

    /**
     * The Surface view.
     */
    private SurfaceView surfaceView;

    /**
     * The Camera source.
     */
    private CameraSource cameraSource;

    /**
     * Instantiates a new Qr surface holder callback.
     *
     * @param surfaceView  the surface view
     * @param cameraSource the camera source
     */
    public QRSurfaceHolderCallback(SurfaceView surfaceView, CameraSource cameraSource) {
        this.surfaceView = surfaceView;
        this.cameraSource = cameraSource;
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        try {
            this.cameraSource.start(this.surfaceView.getHolder());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {}

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        cameraSource.stop();
    }
}
