package com.josezamora.srecscanner.activities;

import android.os.Bundle;
import android.view.MenuItem;
import android.view.SurfaceView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.android.gms.vision.CameraSource;
import com.google.android.gms.vision.barcode.Barcode;
import com.google.android.gms.vision.barcode.BarcodeDetector;
import com.josezamora.srecscanner.R;
import com.josezamora.srecscanner.qr.QRDetectorProcessor;
import com.josezamora.srecscanner.qr.QRSurfaceHolderCallback;

import java.util.Objects;

public class QRActivity extends AppCompatActivity {

    Toolbar toolbar;
    SurfaceView cameraView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_qr);

        toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("Conexión con SRecReceiver");
        setSupportActionBar(toolbar);

        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);

        cameraView = findViewById(R.id.camera_view);

        BarcodeDetector barcodeDetector = new BarcodeDetector.Builder(this)
                .setBarcodeFormats(Barcode.QR_CODE)
                .build();

        SurfaceView cameraView = findViewById(R.id.camera_view);

        CameraSource cameraSource = new CameraSource
                .Builder(this, barcodeDetector)
                .setRequestedPreviewSize(1024, 1024)
                .setAutoFocusEnabled(true)
                .build();

        cameraView.getHolder().addCallback(new QRSurfaceHolderCallback(cameraView, cameraSource));
        barcodeDetector.setProcessor(new QRDetectorProcessor(this));
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
