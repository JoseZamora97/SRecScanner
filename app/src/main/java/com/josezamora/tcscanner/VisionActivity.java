package com.josezamora.tcscanner;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.FutureTarget;
import com.josezamora.tcscanner.Firebase.Classes.CloudImage;
import com.josezamora.tcscanner.Firebase.Vision.VisualAnalyzer;
import com.josezamora.tcscanner.Interfaces.AppGlobals;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutionException;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;


@SuppressWarnings("unchecked")
public class VisionActivity extends AppCompatActivity {

    Bitmap image;
    Toolbar toolbar;

    CardView progressCard;
    ImageView imageView;

    GlideTaskMaker glideDownloader;
    List<CloudImage> images;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vision);

        imageView = findViewById(R.id.imagePreview);
        progressCard = findViewById(R.id.card_progress);
        progressCard.setVisibility(View.VISIBLE);

        images = (List<CloudImage>) getIntent().getSerializableExtra(AppGlobals.IMAGES_KEY);
        glideDownloader = new GlideTaskMaker(new CountDownLatch(images.size()), this);
        glideDownloader.download(images);

        toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("Previsualización del Código");
        setSupportActionBar(toolbar);
    }

    @Override
    protected void onStart() {
        super.onStart();

        glideDownloader.await();
        progressCard.setVisibility(View.GONE);

        List<Bitmap> bitmaps = new ArrayList<>();
        for(GlideImageDownload download : glideDownloader.tasksResults)
            bitmaps.add(download.result);

        image = combineBitmaps(bitmaps);
        imageView.setImageBitmap(image);

        VisualAnalyzer visualAnalyzer = new VisualAnalyzer(image);
        Thread analyzerTask = new Thread(visualAnalyzer);
        analyzerTask.start();

        try {
            analyzerTask.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }

    public void export(View v){
        /* TODO */
    }

    public void exportToSRec(View v) {
        /* TODO */
    }

    public void exportToShare(View v){
        /* TODO */
    }

    private class GlideTaskMaker{
        List<GlideImageDownload> tasksResults;
        CountDownLatch latch;
        Context context;

        GlideTaskMaker(CountDownLatch latch, Context context) {
            this.latch = latch;
            this.context = context;
            tasksResults = new ArrayList<>();
        }

        void download(List<CloudImage> images){
            for(CloudImage image : images){
                GlideImageDownload download = new GlideImageDownload(image, latch, context);
                tasksResults.add(download);

                new Thread(download).start();
            }
        }

        void await() {
            try {
                latch.await();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    private class GlideImageDownload implements Runnable {

        CloudImage image;
        Context context;
        Bitmap result = null;
        CountDownLatch countDownLatch;

        GlideImageDownload(CloudImage image, CountDownLatch countDownLatch, Context context) {
            this.image = image;
            this.context = context;
            this.countDownLatch = countDownLatch;
        }

        @Override
        public void run() {
            FutureTarget<Bitmap> futureBitmap = GlideApp.with(this.context)
                    .asBitmap()
                    .load(this.image.getDownloadLink())
                    .diskCacheStrategy(DiskCacheStrategy.RESOURCE)
                    .submit();
            try {
                result = futureBitmap.get();
                this.countDownLatch.countDown();
            } catch (ExecutionException | InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    private Bitmap combineBitmaps(List<Bitmap> bitmaps) {

        int height, width;
        height = width = 0;

        for(Bitmap bitmap : bitmaps) {
            height += bitmap.getHeight();
            width = bitmap.getWidth() > width ? bitmap.getWidth() : width;
        }

        Bitmap bitmapResult = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmapResult);
        int index = 0;

        Paint paint = new Paint();

        for(Bitmap bitmap : bitmaps) {
            canvas.drawBitmap(bitmap, 0, index, paint);
            index += bitmap.getHeight();
            bitmap = null;
        }

        return bitmapResult;
    }
}
