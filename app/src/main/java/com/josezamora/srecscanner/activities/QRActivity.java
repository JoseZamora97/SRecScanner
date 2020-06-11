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

/**
 * The type Qr activity.
 */
public class QRActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_qr);

        // Set-up toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle(this.getString(R.string.con_srec));
        setSupportActionBar(toolbar);

        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);

        // Set-up camera view
        SurfaceView cameraView = findViewById(R.id.camera_view);

        // Set-up barcode detector
        BarcodeDetector barcodeDetector = new BarcodeDetector.Builder(this)
                .setBarcodeFormats(Barcode.QR_CODE)
                .build();

        // Set-up camera source
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
