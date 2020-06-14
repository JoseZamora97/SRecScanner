package com.josezamora.srecscanner.activities;

import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.view.MenuItem;
import android.view.SurfaceView;
import android.view.View;
import android.widget.RelativeLayout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.android.gms.vision.CameraSource;
import com.google.android.gms.vision.barcode.Barcode;
import com.google.android.gms.vision.barcode.BarcodeDetector;
import com.google.android.material.snackbar.Snackbar;
import com.josezamora.srecscanner.R;
import com.josezamora.srecscanner.qr.QRDetectorProcessor;
import com.josezamora.srecscanner.qr.QRSurfaceHolderCallback;

import java.util.Objects;

import ir.drax.netwatch.NetWatch;
import ir.drax.netwatch.cb.NetworkChangeReceiver_navigator;

/**
 * The type Qr activity.
 */
public class QRActivity extends AppCompatActivity {

    private static boolean internet = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_qr);

        NetWatch.builder(this)
                .setCallBack(new NetworkChangeReceiver_navigator() {
                    @Override
                    public void onConnected(int source) {
                        internet = true;
                        Snackbar.make(findViewById(R.id.rl_content_holder), "SRecScanner está conectado a internet"
                                , Snackbar.LENGTH_SHORT)
                                .setAction(R.string.aceptar, v -> {
                                })
                                .show();
                        onStart();
                    }

                    @Override
                    public void onDisconnected() {
                        internet = false;
                        showSnakeBarNoConnection();
                        onStart();
                    }
                })
                .setNotificationEnabled(false)
                .build();

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
    protected void onStart() {
        super.onStart();

        RelativeLayout rlContentHolder = findViewById(R.id.rl_content_holder);
        RelativeLayout rlNotConnection = findViewById(R.id.rl_no_connection);

        if (internet) {
            rlNotConnection.setVisibility(View.GONE);
            rlContentHolder.animate().alpha(1.0f);
            rlContentHolder.setVisibility(View.VISIBLE);
        } else {
            rlContentHolder.setVisibility(View.GONE);
            rlNotConnection.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void showSnakeBarNoConnection() {
        Snackbar.make(findViewById(R.id.rl_no_connection), "Desconectado de internet"
                , Snackbar.LENGTH_SHORT)
                .setAction(R.string.ajustes, v -> {
                    Intent dialogIntent = new Intent(Settings.ACTION_SETTINGS);
                    dialogIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(dialogIntent);
                })
                .show();
    }
}
