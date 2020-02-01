package com.josezamora.tcscanner.Activities;

import android.content.Intent;
import android.graphics.Canvas;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.core.content.res.ResourcesCompat;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.josezamora.tcscanner.AppGlobals;
import com.josezamora.tcscanner.Firebase.Adapters.CloudNotebookRecyclerAdapter;
import com.josezamora.tcscanner.Firebase.Classes.CloudImage;
import com.josezamora.tcscanner.Firebase.Classes.CloudNotebook;
import com.josezamora.tcscanner.Firebase.Classes.CloudUser;
import com.josezamora.tcscanner.Firebase.Controllers.FirebaseController;
import com.josezamora.tcscanner.R;
import com.josezamora.tcscanner.ViewHolders.CloudImageViewHolder;
import com.theartofdev.edmodo.cropper.CropImage;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.Serializable;
import java.util.Collections;
import java.util.Objects;

import it.xabaras.android.recyclerview.swipedecorator.RecyclerViewSwipeDecorator;

import static android.util.TypedValue.COMPLEX_UNIT_SP;


public class NotebookActivity extends AppCompatActivity
        implements RecyclerViewOnClickInterface {

    Toolbar toolbar;

    CloudNotebook notebook;
    CloudUser user;

    CardView cardViewProgressShow;

    RecyclerView recyclerView;
    ItemTouchHelper itemTouchHelper;

    FirebaseController firebaseController;

    FloatingActionButton btnAdd, btnCamera, btnGallery;
    Animation fabOpen, fabClose, rotateForward, rotateBackward;

    private boolean isOpen = false;

    private boolean newChanges = false;

    private Uri photoUri;
    private CloudNotebookRecyclerAdapter cloudImagesAdapter;
    ItemTouchHelper.SimpleCallback simpleCallback =
            new ItemTouchHelper.SimpleCallback(ItemTouchHelper.UP
                    | ItemTouchHelper.DOWN
                    | ItemTouchHelper.START
                    | ItemTouchHelper.END,
                    ItemTouchHelper.LEFT) {

                @Override
                public boolean onMove(@NonNull RecyclerView recyclerView,
                                      @NonNull RecyclerView.ViewHolder viewHolder,
                                      @NonNull RecyclerView.ViewHolder target) {


                    int srcPosition = viewHolder.getAdapterPosition();
                    int dstPosition = target.getAdapterPosition();

                    if (srcPosition != dstPosition) {
                        newChanges = true;
                        Collections.swap(cloudImagesAdapter.getCloudImages(), srcPosition, dstPosition);
                        cloudImagesAdapter.notifyItemMoved(srcPosition, dstPosition);
                        notebook.setDirty(true);
                        firebaseController.update(notebook, CloudNotebook.DIRTY_KEY);
                    }

                    return true;
                }

                boolean undo;

                @Override
                public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) { // Todo: Fix
                    if (direction == ItemTouchHelper.LEFT) {
                        final int position = viewHolder.getAdapterPosition();
                        final CloudImage image = cloudImagesAdapter.getItem(position);

                        saveChanges();
                        firebaseController.deleteImage(image, false);

                        Snackbar.make(recyclerView, "Foto eliminada", Snackbar.LENGTH_INDEFINITE)
                                .setDuration(3000)
                                .addCallback(new Snackbar.Callback() {
                                    @Override
                                    public void onDismissed(Snackbar snackbar, int event) {
                                        undo = event == BaseTransientBottomBar
                                                .BaseCallback.DISMISS_EVENT_ACTION;
                                        if (!undo) firebaseController.deleteImage(image, false);
                                        else firebaseController.addImage(image);
                                    }
                                })
                                .setAction("Deshacer", v -> {
                                })
                                .show();
                    }
                }

                @Override
                public void onChildDraw(@NonNull Canvas c, @NonNull RecyclerView recyclerView,
                                        @NonNull RecyclerView.ViewHolder viewHolder, float dX, float dY,
                                        int actionState, boolean isCurrentlyActive) {

                    new RecyclerViewSwipeDecorator.Builder(c, recyclerView, viewHolder, dX, dY, actionState
                            , isCurrentlyActive)
                            .addBackgroundColor(getResources().getColor(R.color.colorAccent))
                            .addActionIcon(R.drawable.ic_delete_sweep_30dp)
                            .addSwipeLeftLabel("Eliminar")
                            .setSwipeLeftLabelTextSize(COMPLEX_UNIT_SP, 16)
                            .setSwipeLeftLabelTypeface(ResourcesCompat.getFont(getApplicationContext(), R.font.nunito))
                            .setSwipeLeftLabelColor(getResources().getColor(R.color.colorPrimary))
                            .create()
                            .decorate();

                    super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
                }
            };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notebook);

        firebaseController = new FirebaseController();

        user = CloudUser.userFromFirebase(
                Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()));

        notebook = (CloudNotebook) getIntent().getSerializableExtra(AppGlobals.NOTEBOOK_KEY);

        btnAdd = findViewById(R.id.fabAdd);
        btnCamera = findViewById(R.id.fabAddFromCamera);
        btnGallery = findViewById(R.id.fabAddFromGallery);

        cardViewProgressShow = findViewById(R.id.card_progress);

        fabOpen = AnimationUtils.loadAnimation(this, R.anim.fab_open);
        fabClose = AnimationUtils.loadAnimation(this, R.anim.fab_close);
        rotateForward =  AnimationUtils.loadAnimation(this, R.anim.rotation_forward);
        rotateBackward =  AnimationUtils.loadAnimation(this, R.anim.rotation_backward);

        toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("Editor de composición");
        setSupportActionBar(toolbar);

        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);

        ((TextView) findViewById(R.id.notebook)).setText(notebook.getName());
        recyclerView = findViewById(R.id.rv_photos);

        DividerItemDecoration itemDecor = new DividerItemDecoration(this,
                DividerItemDecoration.VERTICAL);
        itemDecor.setDrawable(Objects.requireNonNull(
                ContextCompat.getDrawable(this, R.drawable.recycler_divider_horizontal)));
        recyclerView.addItemDecoration(itemDecor);

        itemTouchHelper = new ItemTouchHelper(simpleCallback);
        itemTouchHelper.attachToRecyclerView(recyclerView);
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        cloudImagesAdapter.startListening();
    }

    @Override
    protected void onPause() {
        super.onPause();
        cloudImagesAdapter.stopListening();
    }

    @Override
    protected void onStop() {
        super.onStop();
        cloudImagesAdapter.stopListening();
    }

    @Override
    protected void onStart() {
        super.onStart();

        cloudImagesAdapter = new CloudNotebookRecyclerAdapter(
                firebaseController.getRecyclerOptions(user, notebook), this);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(cloudImagesAdapter);

        cloudImagesAdapter.startListening();
    }

    @Override
    public void onBackPressed() {

        notebook.setNumImages(cloudImagesAdapter.getCloudImages().size());
        firebaseController.update(notebook, CloudNotebook.NUM_IMAGES_KEY);

        if(newChanges) {
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
                onBackPressed();
            });

            AlertDialog alertDialog = builderConfig.create();
            alertDialog.show();
        }
        else
            super.onBackPressed();
    }

    @Override
    public void onItemClick(int position) {
        for (int childCount = recyclerView.getChildCount(), i = 0; i < childCount; ++i) {
            CloudImageViewHolder holder = (CloudImageViewHolder) recyclerView
                    .getChildViewHolder(recyclerView.getChildAt(i));
            if (i == position)
                holder.update();
            else if (holder.isExpanded())
                holder.update();
        }
    }

    @Override
    public void onLongItemClick(int position) {
        Toast.makeText(this, "Arrastra y suelta la foto en la posición que desees",
                Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK)
            switch (requestCode) {
                case AppGlobals.REQUEST_CODE_CAMERA:
                    cropImage(photoUri);
                    break;
                case AppGlobals.REQUEST_CODE_STORAGE:
                    assert data != null;
                    cropImage(data.getData());
                    break;
                case CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE:
                    uploadImage(Objects.requireNonNull(CropImage.getActivityResult(data)).getUri());
                    notebook.setDirty(true);
                    firebaseController.update(notebook, CloudNotebook.DIRTY_KEY);
                    break;
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

    private void cropImage(Uri uri) {
        CropImage.activity(uri)
                .setActivityMenuIconColor(getResources().getColor(R.color.colorBodies))
                .setActivityTitle("Recortar")
                .start(this);
    }

    public void toImageFullscreen(int position) {
        CloudImage image = cloudImagesAdapter.getItem(position);
        Intent toFullscreen = new Intent(this, ImageActivity.class);
        toFullscreen.putExtra("image", image);
        startActivity(toFullscreen);
    }

    private void toVisionActivity() {
        Intent toVisionActivity = new Intent(this, VisionActivity.class);
        toVisionActivity.putExtra(AppGlobals.IMAGES_KEY, (Serializable) cloudImagesAdapter.getCloudImages());
        toVisionActivity.putExtra(AppGlobals.NOTEBOOK_KEY, notebook);

        startActivity(toVisionActivity);
    }

    public void scan(View v){
        if(cloudImagesAdapter.getCloudImages().size()>0){
            saveChanges();
            toVisionActivity();
        }
        else
            Toast.makeText(this, "Tienes que añadir una imagen", Toast.LENGTH_SHORT)
                    .show();
    }

    public void saveChanges() {
        for (int i = 0; i < cloudImagesAdapter.getCloudImages().size(); i++) {
            CloudImage image = cloudImagesAdapter.getCloudImages().get(i);
            image.setOrder(i);
            firebaseController.update(image);
        }

        notebook.setNumImages(cloudImagesAdapter.getCloudImages().size());
        firebaseController.update(notebook, CloudNotebook.NUM_IMAGES_KEY);

        newChanges = false;
    }

    public void addPhotoFromCamera(View v) {
        animateFloatActionButtons(v);

        Intent cameraOpenIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        File file = new File(getExternalCacheDir(),
                System.currentTimeMillis() + ".png");

        photoUri = FileProvider.getUriForFile(this,
                AppGlobals.APP_SIGNATURE + ".provider", file);

        cameraOpenIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri);
        cameraOpenIntent.putExtra("return-data", true);

        startActivityForResult(cameraOpenIntent, AppGlobals.REQUEST_CODE_CAMERA);
    }

    public void addPhotoFromGallery(View v) {
        animateFloatActionButtons(v);

        Intent galleryOpen = new Intent(Intent.ACTION_PICK,
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI);

        startActivityForResult(Intent.createChooser(galleryOpen,
                "Selecciona una imagen"), AppGlobals.REQUEST_CODE_STORAGE);
    }

    public void animateFloatActionButtons(View v) {
        if (cloudImagesAdapter.getCloudImages().size() < AppGlobals.MAX_PHOTOS_PER_NOTEBOOK)
            if(isOpen){
                btnAdd.startAnimation(rotateBackward);

                btnGallery.startAnimation(fabClose);
                btnGallery.setClickable(false);

                btnCamera.startAnimation(fabClose);
                btnCamera.setClickable(false);
                isOpen = false;
            }
            else {
                btnAdd.startAnimation(rotateForward);

                btnGallery.startAnimation(fabOpen);
                btnGallery.setClickable(true);

                btnCamera.startAnimation(fabOpen);
                btnCamera.setClickable(true);
                isOpen = true;
            }
        else
            Toast.makeText(this, "No puedes añadir más imágenes a esta Composición"
                    , Toast.LENGTH_SHORT).show();
    }

    private void uploadImage(Uri data) {
        InputStream stream;

        try {

            cardViewProgressShow.setVisibility(View.VISIBLE);
            stream = getContentResolver().openInputStream(data);
            firebaseController
                    .uploadImage(user, notebook, stream)
                    .addOnSuccessListener(taskSnapshot ->
                            cardViewProgressShow.setVisibility(View.GONE));

        }
        catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }
}
