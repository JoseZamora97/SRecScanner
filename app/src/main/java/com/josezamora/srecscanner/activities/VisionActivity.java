package com.josezamora.srecscanner.activities;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

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
import com.google.android.material.snackbar.Snackbar;
import com.josezamora.srecscanner.AppGlobals;
import com.josezamora.srecscanner.R;
import com.josezamora.srecscanner.editor.CodeEditor;
import com.josezamora.srecscanner.editor.LanguageProvider;
import com.josezamora.srecscanner.firebase.Classes.CloudImage;
import com.josezamora.srecscanner.firebase.Classes.CloudNotebook;
import com.josezamora.srecscanner.firebase.Classes.CloudUser;
import com.josezamora.srecscanner.firebase.Controllers.FirebaseController;
import com.josezamora.srecscanner.firebase.GlideApp;
import com.josezamora.srecscanner.firebase.VisionAnalyzer;
import com.josezamora.srecscanner.preferences.PreferencesController;
import com.josezamora.srecscanner.srecprotocol.SRecProtocolController;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutionException;

import ir.drax.netwatch.NetWatch;
import ir.drax.netwatch.cb.NetworkChangeReceiver_navigator;


/**
 * The type Vision activity.
 */
@SuppressWarnings("unchecked")
public class VisionActivity extends AppCompatActivity {

    private static boolean internet = true;
    /**
     * The User.
     */
    private CloudUser user;

    /**
     * The Text view ext.
     */
    private TextView textViewExt;
    /**
     * The Text name.
     */
    private TextView textName;

    /**
     * The Progress card.
     */
    private CardView progressCard;

    /**
     * The Glide downloader.
     */
    private GlideTaskMaker glideDownloader;

    /**
     * The Notebook.
     */
    private CloudNotebook notebook;

    /**
     * The Code editor.
     */
    private CodeEditor codeEditor;

    /**
     * The Spinner.
     */
    private Spinner spinner;

    /**
     * The Preferences controller.
     */
    private PreferencesController preferencesController;
    /**
     * The S rec protocol controller.
     */
    private SRecProtocolController sRecProtocolController;
    /**
     * The Firebase controller.
     */
    private FirebaseController firebaseController;

    /**
     * The Is open.
     */
    private boolean isOpen = false;

    /**
     * The Fab Animations, open.
     */
    private Animation fabOpen, /**
     * The Fab close.
     */
    fabClose, /**
     * The Rotate forward.
     */
    rotateForward, /**
     * The Rotate backward.
     */
    rotateBackward;

    /**
     * The Btn export.
     */
    private FloatingActionButton btnExport, /**
     * The Btn export to s rec.
     */
    btnExportToSRec, /**
     * The Btn share.
     */
    btnShare;

    /**
     * The Temp.
     */
    private File temp;

    /*
     * Override methods.
     */

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vision);

        NetWatch.builder(this)
                .setCallBack(new NetworkChangeReceiver_navigator() {
                    @Override
                    public void onConnected(int source) {
                        internet = true;
                        Snackbar.make(codeEditor, "SRecScanner está conectado a internet"
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

        progressCard = findViewById(R.id.card_progress);
        progressCard.setVisibility(View.VISIBLE);

        // Load the images, notebook and user.
        List<CloudImage> images = (List<CloudImage>) getIntent().getSerializableExtra(AppGlobals.IMAGES_KEY);
        notebook = (CloudNotebook) getIntent().getSerializableExtra(AppGlobals.NOTEBOOK_KEY);
        user = (CloudUser) getIntent().getSerializableExtra(AppGlobals.USER_KEY);

        assert images != null;

        // If notebook is dirty create the download tasks.
        if (notebook.isDirty()) {
            glideDownloader = new GlideTaskMaker(new CountDownLatch(images.size()), this);
            glideDownloader.download(images);
        }

        // Set-up the toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle(this.getString(R.string.previsualización));
        setSupportActionBar(toolbar);

        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);

        // Set-up visual elements.
        codeEditor = findViewById(R.id.code_editor);
        spinner = findViewById(R.id.spinnerLenguaje);

        SpinnerAdapter spinnerAdapter = new SpinnerAdapter(this,
                android.R.layout.simple_spinner_dropdown_item,
                LanguageProvider.Languages.values());

        spinner.setAdapter(spinnerAdapter);
        spinner.setOnItemSelectedListener(spinnerAdapter);

        textViewExt = findViewById(R.id.text_ext);
        textName = findViewById(R.id.name_file);

        // Set-up spinner
        String lan = notebook.getLanguage();
        if (lan.equals(LanguageProvider.getExtension(LanguageProvider.Languages.JAVA)))
            spinner.setSelection(0, true);
        else if (lan.equals(LanguageProvider.getExtension(LanguageProvider.Languages.PYTHON)))
            spinner.setSelection(1, true);
        else
            spinner.setSelection(2, true);

        // Set-up controllers
        firebaseController = new FirebaseController();
        preferencesController = new PreferencesController(this);
        sRecProtocolController = new SRecProtocolController();

        // Set-up buttons.
        btnExport = findViewById(R.id.fabExport);
        btnExportToSRec = findViewById(R.id.fabExportSrec);
        btnShare = findViewById(R.id.fabExportToShare);

        initAnimations();
    }

    @Override
    public void onBackPressed() {
        AlertDialog.Builder builderConfig = new AlertDialog.Builder(this);

        builderConfig.setTitle(R.string.guardar);
        builderConfig.setMessage(this.getString(R.string.salir_sin_guardar_vision_activity));

        builderConfig.setPositiveButton(this.getString(R.string.guardar),
                (dialogInterface, i) -> {
                    saveChanges();
                    dialogInterface.dismiss();
                    super.onBackPressed();
                });

        builderConfig.setNegativeButton(this.getString(R.string.descartar),
                (dialogInterface, i) -> {
                    dialogInterface.dismiss();
                    finish();
                });

        AlertDialog alertDialog = builderConfig.create();
        alertDialog.show();
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

        RelativeLayout rlContentHolder = findViewById(R.id.rl_content_holder);
        RelativeLayout rlNotConnection = findViewById(R.id.rl_no_connection);

        if (internet) {
            rlNotConnection.setVisibility(View.GONE);

            if (notebook.isDirty()) {
                glideDownloader.await();
                progressCard.setVisibility(View.GONE);

                List<Bitmap> bitmaps = new ArrayList<>();

                for (GlideImageDownload download : glideDownloader.tasksResults)
                    bitmaps.add(download.result);

                Bitmap image = combineBitmaps(bitmaps);

                VisionAnalyzer visionAnalyzer = new VisionAnalyzer(image, codeEditor);
                Thread analyzerTask = new Thread(visionAnalyzer);
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

            rlContentHolder.animate().alpha(1.0f);
            rlContentHolder.setVisibility(View.VISIBLE);
        } else {
            rlContentHolder.setVisibility(View.GONE);
            rlNotConnection.setVisibility(View.VISIBLE);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK && requestCode == AppGlobals.REQUEST_CODE_QR)
            if (data != null)
                handleQRResult(Objects.requireNonNull(data.getStringExtra("result")));

    }

    /*
     *  OnClick Methods of buttons.
     */

    /**
     * Export.
     * Update floating action buttons caller.
     *
     * @param v the button that has the onClick set up.
     */
    public void export(View v){
        updateFabs();
    }

    /**
     * Export to SRec
     *
     * @param v the button that has the onClick set up.
     */
    public void exportToSRec(View v) {
        temp = editTextToFile();
        String[] ip_port = preferencesController.getConnectionDetailsSRec();

        if (ip_port[0] == null || ip_port[0].equals(SRecProtocolController.NONE))
            startActivityForResult(new Intent(this, QRActivity.class), AppGlobals.REQUEST_CODE_QR);
        else
            sRecProtocolController.connectAndSendFile(ip_port[0], ip_port[1], temp);

        updateFabs();
    }

    /**
     * Export to share in social networks.
     * @param v  the button that has the onClick set up.
     */
    public void exportToShare(View v){

        temp = editTextToFile();

        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.setType(URLConnection.guessContentTypeFromName(temp.getName()));

        updateFabs();

        Uri uri = FileProvider.getUriForFile(this,
                AppGlobals.APP_SIGNATURE + ".provider", temp);

        shareIntent.putExtra(Intent.EXTRA_STREAM, uri);
        shareIntent.putExtra(Intent.EXTRA_SUBJECT,
                "Sharing File...");
        shareIntent.putExtra(Intent.EXTRA_TEXT, "Sharing File...");

        startActivity(Intent.createChooser(shareIntent, "Compartir"));
    }

    /**
     * Rename.
     * Open a dialog where the user cans change the name.
     * @param v  the button that has the onClick set up.
     */
    public void rename(View v) {

        LayoutInflater li = LayoutInflater.from(VisionActivity.this);
        AlertDialog.Builder builderConfig = new AlertDialog.Builder(this);

        @SuppressLint("InflateParams")
        View view = li.inflate(R.layout.dialog_name, null);

        builderConfig.setTitle(this.getString(R.string.rename));
        builderConfig.setView(view);
        builderConfig.setCancelable(false);

        final EditText editText = view.findViewById(R.id.editTextName);

        builderConfig.setPositiveButton(this.getString(R.string.aceptar),
                (dialogInterface, i) -> textName.setText(editText.getText().toString()));

        builderConfig.setNegativeButton(this.getString(R.string.cancelar),
                (dialogInterface, i) -> dialogInterface.dismiss());

        AlertDialog alertDialog = builderConfig.create();
        alertDialog.show();
    }

    /*
     * Auxiliary Classes
     */

    private void initAnimations() {
        fabOpen = AnimationUtils.loadAnimation(this, R.anim.fab_open);
        fabClose = AnimationUtils.loadAnimation(this, R.anim.fab_close);

        rotateForward = AnimationUtils.loadAnimation(this, R.anim.rotation_forward);
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

        rotateBackward = AnimationUtils.loadAnimation(this, R.anim.rotation_backward);
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

    private void showSnakeBarNoConnection() {
        Snackbar.make(codeEditor, "Desconectado de internet"
                , Snackbar.LENGTH_SHORT)
                .setAction("Ajustes", v -> {
                    Intent dialogIntent = new Intent(android.provider.Settings.ACTION_SETTINGS);
                    dialogIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(dialogIntent);
                })
                .show();
    }

    private void saveChanges() {
        notebook.setContent(Objects.requireNonNull(codeEditor.getText()).toString());
        notebook.setLanguage(textViewExt.getText().toString());
        notebook.setDirty(false);

        firebaseController.update(notebook, CloudNotebook.DIRTY_KEY);
        firebaseController.update(notebook, CloudNotebook.LANGUAGE_KEY);
        firebaseController.update(notebook, CloudNotebook.CONTENT_KEY);
    }

    private File editTextToFile() {
        File file = new File(getExternalCacheDir(),
                user.getName() + "_" + user.getuId() + "_" + textName.getText().toString() +
                        textViewExt.getText().toString());

        String content = Objects.requireNonNull(codeEditor.getText()).toString();

        try {
            FileOutputStream stream = new FileOutputStream(file);
            stream.write(content.getBytes());
            stream.close();
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }

        return file;
    }

    /*
     * Auxiliary methods.
     */

    private void handleQRResult(String result) {
        String[] ip_port = result.split(":");
        new Thread(() -> {
            if (sRecProtocolController.serverInSameNetwork(result)) {
                sRecProtocolController.startConnection(ip_port[1], ip_port[2]);
                sRecProtocolController.sendFile(temp);
                preferencesController.connectedToSRec(sRecProtocolController.getIp(), sRecProtocolController.getPort());
            } else
                Toast.makeText(getApplicationContext(), getResources().getString(R.string.not_same_network),
                        Toast.LENGTH_SHORT).show();
        }).start();
    }

    private Bitmap combineBitmaps(List<Bitmap> bitmaps) {

        int height, width;
        height = width = 0;

        for (Bitmap bitmap : bitmaps) {
            height += bitmap.getHeight();
            width = Math.max(bitmap.getWidth(), width);
        }

        Bitmap bitmapResult = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmapResult);
        int index = 0;

        Paint paint = new Paint();

        for (Bitmap bitmap : bitmaps) {
            canvas.drawBitmap(bitmap, 0, index, paint);
            index += bitmap.getHeight();
        }

        return bitmapResult;
    }

    private void updateFabs() {
        if (isOpen) {
            btnExport.startAnimation(rotateBackward);

            btnShare.startAnimation(fabClose);
            btnShare.setClickable(false);

            btnExportToSRec.startAnimation(fabClose);
            btnExportToSRec.setClickable(false);
            isOpen = false;
        } else {
            btnExport.startAnimation(rotateForward);

            btnShare.startAnimation(fabOpen);
            btnShare.setClickable(true);

            btnExportToSRec.startAnimation(fabOpen);
            btnExportToSRec.setClickable(true);
            isOpen = true;
        }
    }

    private static class GlideTaskMaker {
        /**
         * The Tasks results.
         */
        private List<GlideImageDownload> tasksResults;
        /**
         * The Latch.
         */
        private CountDownLatch latch;
        /**
         * The Context.
         */
        private Context context;

        /**
         * Instantiates a new Glide task maker.
         *
         * @param latch   the latch
         * @param context the context
         */
        GlideTaskMaker(CountDownLatch latch, Context context) {
            this.latch = latch;
            this.context = context;
            tasksResults = new ArrayList<>();
        }

        /**
         * Download.
         *
         * @param images the images
         */
        void download(List<CloudImage> images){
            for(CloudImage image : images){
                GlideImageDownload download = new GlideImageDownload(image, latch, context);
                tasksResults.add(download);

                new Thread(download).start();
            }
        }

        /**
         * Await.
         */
        void await() {
            try {
                latch.await();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    private static class GlideImageDownload implements Runnable {

        /**
         * The Context.
         */
        Context context;
        /**
         * The Image.
         */
        CloudImage image;
        /**
         * The Result.
         */
        Bitmap result = null;
        /**
         * The Count down latch.
         */
        CountDownLatch countDownLatch;

        /**
         * Instantiates a new Glide image download.
         *
         * @param image          the image
         * @param countDownLatch the count down latch
         * @param context        the context
         */
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

    private class SpinnerAdapter extends ArrayAdapter<LanguageProvider.Languages>
            implements AdapterView.OnItemSelectedListener {

        /**
         * The Font.
         */
        Typeface font;

        /**
         * Instantiates a new Spinner adapter.
         *
         * @param context  the context
         * @param resource the resource
         * @param items    the items
         */
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
            textViewExt.setText(
                    LanguageProvider.getExtension(
                            (LanguageProvider.Languages) spinner.getSelectedItem()));

            Editable text = codeEditor.getText();
            codeEditor.setText("");
            codeEditor.setText(text.toString());
        }

        @Override
        public void onNothingSelected(AdapterView<?> adapterView) {
            /* Nothing */
        }
    }
}
