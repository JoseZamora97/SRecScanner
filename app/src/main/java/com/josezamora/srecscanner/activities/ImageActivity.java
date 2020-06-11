package com.josezamora.srecscanner.activities;

import android.os.Bundle;
import android.view.MenuItem;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.josezamora.srecscanner.AppGlobals;
import com.josezamora.srecscanner.R;
import com.josezamora.srecscanner.firebase.Classes.CloudImage;
import com.josezamora.srecscanner.firebase.GlideApp;

import java.util.Objects;

/**
 * The type Image activity.
 */
public class ImageActivity extends AppCompatActivity {

    /**
     * The Activity Toolbar.
     */
    Toolbar toolbar;
    /**
     * The Image view where will be show the image.
     */
    ImageView imageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image);

        // Get the image to be showed.
        CloudImage image = (CloudImage) getIntent().getSerializableExtra(AppGlobals.IMAGES_KEY);

        // If not null.
        assert image != null;

        // Set-up toolbar
        toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle(image.getId());
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);

        // Set-up image view.
        imageView = findViewById(R.id.imageViewFull);
        // Download the original image to image view.
        GlideApp.with(this).load(image.getDownloadLink()).into(imageView);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            // Return to the previous activity.
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
