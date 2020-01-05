package com.josezamora.tcscanner;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.josezamora.tcscanner.Interfaces.AppGlobals;

import androidx.appcompat.app.AppCompatActivity;

public class VisionActivity extends AppCompatActivity {

    Bitmap image;
    LinearLayout linearLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vision);

        image = getIntent().getParcelableExtra(AppGlobals.IMAGES_KEY);

        linearLayout = findViewById(R.id.ll_images);

        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);

        ImageView imageView = new ImageView(this);
        imageView.setLayoutParams(params);
        imageView.setImageBitmap(image);

        linearLayout.addView(imageView);
    }
}
