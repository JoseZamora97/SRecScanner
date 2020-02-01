package com.josezamora.tcscanner.Activities;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import androidx.core.content.FileProvider;
import androidx.core.content.res.ResourcesCompat;

import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.FutureTarget;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.josezamora.tcscanner.AppGlobals;
import com.josezamora.tcscanner.Editor.CodeEditor;
import com.josezamora.tcscanner.Editor.LanguageProvider;
import com.josezamora.tcscanner.Firebase.Classes.CloudImage;
import com.josezamora.tcscanner.Firebase.Classes.CloudNotebook;
import com.josezamora.tcscanner.Firebase.Controllers.FirebaseController;
import com.josezamora.tcscanner.Firebase.GlideApp;
import com.josezamora.tcscanner.Firebase.Vision.VisualAnalyzer;
import com.josezamora.tcscanner.Preferences.PreferencesController;
import com.josezamora.tcscanner.R;
import com.josezamora.tcscanner.SRecProtocol.SRecController;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutionException;


@SuppressWarnings("unchecked")
public class VisionActivity extends AppCompatActivity {

    Bitmap image;
    Toolbar toolbar;

    TextView textViewExt;
    TextView textName;

    SpinnerAdapter spinnerAdapter;

    CardView progressCard;

    GlideTaskMaker glideDownloader;
    List<CloudImage> images;
    CloudNotebook notebook;

    CodeEditor codeEditor;

    Spinner spinner;

    PreferencesController preferencesController;
    SRecController sRecController;
    FirebaseController firebaseController;

    boolean isOpen = false;
    Animation fabOpen, fabClose, rotateForward, rotateBackward;
    FloatingActionButton btnExport, btnExportToSRec, btnShare;

    File temp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vision);

        progressCard = findViewById(R.id.card_progress);
        progressCard.setVisibility(View.VISIBLE);

        images = (List<CloudImage>) getIntent().getSerializableExtra(AppGlobals.IMAGES_KEY);
        notebook = (CloudNotebook) getIntent().getSerializableExtra(AppGlobals.NOTEBOOK_KEY);

        assert images != null;

        glideDownloader = new GlideTaskMaker(new CountDownLatch(images.size()), this);

        if (notebook.isDirty()) glideDownloader.download(images);

        toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("Previsualización del Código");
        setSupportActionBar(toolbar);

        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);

        codeEditor = findViewById(R.id.code_editor);
        spinner = findViewById(R.id.spinnerLenguaje);

        spinnerAdapter = new SpinnerAdapter(this,
                android.R.layout.simple_spinner_dropdown_item,
                LanguageProvider.Languages.values());

        spinner.setAdapter(spinnerAdapter);
        spinner.setOnItemSelectedListener(spinnerAdapter);

        textViewExt = findViewById(R.id.text_ext);
        textName = findViewById(R.id.name_file);

        spinner.setSelection(0, true);

        firebaseController = new FirebaseController();
        preferencesController = new PreferencesController(this);
        sRecController = new SRecController();

        btnExport = findViewById(R.id.fabExport);
        btnExportToSRec = findViewById(R.id.fabExportSrec);
        btnShare = findViewById(R.id.fabExportToShare);

        initAnimations();
    }

    public void initAnimations() {
        fabOpen = AnimationUtils.loadAnimation(this, R.anim.fab_open);
        fabClose = AnimationUtils.loadAnimation(this, R.anim.fab_close);

        rotateForward =  AnimationUtils.loadAnimation(this, R.anim.rotation_forward);
        rotateForward.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                /* Nothing */
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                btnExport.setImageResource(R.drawable.ic_add_white_24dp);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
                /* Nothing */
            }
        });

        rotateBackward =  AnimationUtils.loadAnimation(this, R.anim.rotation_backward);
        rotateBackward.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                /* Nothing */
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                btnExport.setImageResource(R.drawable.ic_export_24dp);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
                /* Nothing */
            }
        });
    }

    public class SpinnerAdapter extends ArrayAdapter<LanguageProvider.Languages>
            implements AdapterView.OnItemSelectedListener{

        Typeface font;

        SpinnerAdapter(@NonNull Context context, int resource,
                       LanguageProvider.Languages[] items) {
            super(context, resource, items);
            font = ResourcesCompat.getFont(context, R.font.nunito);
        }

        @NonNull
        @Override
        public View getView(int position, View convertView, @NonNull ViewGroup parent) {
            TextView view = (TextView) super.getView(position, convertView, parent);
            view.setTypeface(font);
            return view;
        }

        @Override
        public View getDropDownView(int position, View convertView, @NonNull ViewGroup parent) {
            TextView view = (TextView) super.getDropDownView(position, convertView, parent);
            view.setTypeface(font);
            return view;
        }

        @Override
        public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
            codeEditor.setLanguage((LanguageProvider.Languages) spinner.getSelectedItem());
            textViewExt.setText(LanguageProvider.getExtension(
                    (LanguageProvider.Languages)spinner.getSelectedItem()));
        }

        @Override
        public void onNothingSelected(AdapterView<?> adapterView) {
            /* Nothing */
        }
    }

    @Override
    public void onBackPressed() {
        if (codeEditor.isModified()) {
            AlertDialog.Builder builderConfig = new AlertDialog.Builder(this);

            builderConfig.setCancelable(false);
            builderConfig.setTitle("Ups!!");
            builderConfig.setMessage("Estás a punto de salir y hay cambios que no " +
                    "se han guardado.¿Desea guardar cambios y volver?");

            builderConfig.setPositiveButton("Guardar", (dialogInterface, i) -> {
                saveChanges();
                onBackPressed();
            });

            builderConfig.setNegativeButton("Descartar", (dialogInterface, i) -> {
                dialogInterface.dismiss();
                finish();
            });

            AlertDialog alertDialog = builderConfig.create();
            alertDialog.show();
        } else
            super.onBackPressed();
    }

    private void saveChanges() {
        notebook.setContent(Objects.requireNonNull(codeEditor.getText()).toString());
        notebook.setLanguage(textViewExt.getText().toString());
        notebook.setDirty(false);

        firebaseController.update(notebook, CloudNotebook.DIRTY_KEY);
        firebaseController.update(notebook, CloudNotebook.LANGUAGE_KEY);
        firebaseController.update(notebook, CloudNotebook.CONTENT_KEY);

        codeEditor.setModified(false);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onStart() {
        super.onStart();

        if (notebook.isDirty()) {
            glideDownloader.await();
            progressCard.setVisibility(View.GONE);

            List<Bitmap> bitmaps = new ArrayList<>();
            for (GlideImageDownload download : glideDownloader.tasksResults)
                bitmaps.add(download.result);

            image = combineBitmaps(bitmaps);

            VisualAnalyzer visualAnalyzer = new VisualAnalyzer(image, codeEditor);
            Thread analyzerTask = new Thread(visualAnalyzer);
            analyzerTask.start();

            try {
                analyzerTask.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        } else {
            codeEditor.setText(notebook.getContent());
            progressCard.setVisibility(View.GONE);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK && requestCode == AppGlobals.REQUEST_CODE_QR)
            if (data != null)
                handleQRResult(Objects.requireNonNull(data.getStringExtra("result")));

    }

    public void addTab(View v) {
        codeEditor.insertTab();
    }

    public void export(View v){
        if(isOpen){
            btnExport.startAnimation(rotateBackward);

            btnShare.startAnimation(fabClose);
            btnShare.setClickable(false);

            btnExportToSRec.startAnimation(fabClose);
            btnExportToSRec.setClickable(false);
            isOpen = false;
        }
        else {
            btnExport.startAnimation(rotateForward);

            btnShare.startAnimation(fabOpen);
            btnShare.setClickable(true);

            btnExportToSRec.startAnimation(fabOpen);
            btnExportToSRec.setClickable(true);
            isOpen = true;
        }
    }

    public void exportToSRec(View v) {
        temp = editTextToFile();
        String[] ip_port = preferencesController.getConnectionDetailsSRec();

        if(ip_port[0] == null || ip_port[0].equals(SRecController.NONE)) {
            Intent connectToSRec = new Intent(this, QRActivity.class);
            startActivityForResult(connectToSRec, AppGlobals.REQUEST_CODE_QR);
        }
        else
            sRecController.connectAndSendFile(ip_port[0], ip_port[1], temp);

    }

    private File editTextToFile(){
        File file = new File(getExternalCacheDir(),
                textName.getText().toString() + textViewExt.getText().toString());

        String content = Objects.requireNonNull(codeEditor.getText()).toString();

        try {
            FileOutputStream stream = new FileOutputStream(file);
            stream.write(content.getBytes());
            stream.close();
        }
        catch (IOException ioe) {
            ioe.printStackTrace();
        }

        return file;
    }

    private void handleQRResult(String result) {
        String[] ip_port = result.split(":");
        sRecController.startConnection(ip_port[1], ip_port[2]);
        sRecController.sendFile(temp);

        preferencesController.connectedToSRec(sRecController.getIp(), sRecController.getPort());
    }

    public void exportToShare(View v){

        temp = editTextToFile();

        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.setType(URLConnection.guessContentTypeFromName(temp.getName()));

        Uri uri = FileProvider.getUriForFile(this,
                AppGlobals.APP_SIGNATURE + ".provider", temp);

        shareIntent.putExtra(Intent.EXTRA_STREAM, uri);
        shareIntent.putExtra(Intent.EXTRA_SUBJECT,
                "Sharing File...");
        shareIntent.putExtra(Intent.EXTRA_TEXT, "Sharing File...");

        startActivity(Intent.createChooser(shareIntent, "Compartir"));
    }

    public void rename(View v) {

        LayoutInflater li = LayoutInflater.from(VisionActivity.this);
        AlertDialog.Builder builderConfig = new AlertDialog.Builder(this);

        @SuppressLint("InflateParams")
        View view = li.inflate(R.layout.dialog_name, null);

        Typeface font = ResourcesCompat.getFont(this, R.font.nunito_bold);

        builderConfig.setTitle("Renombrar fichero");
        builderConfig.setView(view);
        builderConfig.setCancelable(false);

        final EditText editText = view.findViewById(R.id.editTextName);

        builderConfig.setPositiveButton("Aceptar",
                (dialogInterface, i) -> textName.setText(editText.getText().toString()));

        builderConfig.setNegativeButton("Cancelar",
                (dialogInterface, i) -> dialogInterface.dismiss());

        AlertDialog alertDialog = builderConfig.create();
        alertDialog.show();

        Button btn1 = alertDialog.findViewById(android.R.id.button1);
        assert btn1 != null;
        btn1.setTypeface(font);

        Button btn2 = alertDialog.findViewById(android.R.id.button2);
        assert btn2 != null;
        btn2.setTypeface(font);
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
        }

        return bitmapResult;
    }
}
