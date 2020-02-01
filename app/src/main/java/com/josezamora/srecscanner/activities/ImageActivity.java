package com.josezamora.srecscanner.activities;

import android.os.Bundle;
import android.view.MenuItem;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.josezamora.srecscanner.R;
import com.josezamora.srecscanner.firebase.Classes.CloudImage;
import com.josezamora.srecscanner.firebase.GlideApp;

import java.util.Objects;

public class ImageActivity extends AppCompatActivity {

    Toolbar toolbar;
    ImageView imageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image);

        CloudImage image = (CloudImage) getIntent().getSerializableExtra("image");

        assert image != null;

        toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle(image.getId());
        setSupportActionBar(toolbar);

        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);

        imageView = findViewById(R.id.imageViewFull);

        GlideApp.with(this).load(image.getDownloadLink()).into(imageView);
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
