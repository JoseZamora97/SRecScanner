package com.josezamora.tcscanner;

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

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.josezamora.tcscanner.Adapters.PhotoCompositionRecyclerAdapter;
import com.josezamora.tcscanner.Classes.Composition;
import com.josezamora.tcscanner.Classes.IOCompositions;
import com.josezamora.tcscanner.Classes.PhotoComposition;
import com.josezamora.tcscanner.Interfaces.AppGlobals;
import com.josezamora.tcscanner.Interfaces.RecyclerViewOnClickInterface;

import java.io.File;
import java.io.IOException;
import java.util.Collections;
import java.util.Objects;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.FileProvider;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import it.xabaras.android.recyclerview.swipedecorator.RecyclerViewSwipeDecorator;


public class CompositionActivity extends AppCompatActivity
        implements RecyclerViewOnClickInterface {

    Toolbar toolbar;
    Composition composition;
    RecyclerView recyclerView;
    PhotoCompositionRecyclerAdapter recyclerAdapter;
    ItemTouchHelper itemTouchHelper;
    PhotoComposition photoDeleted;

    FloatingActionButton btnAdd, btnCamera, btnGallery;
    Animation fabOpen, fabClose, rotateForward, rotateBackward;

    private boolean isOpen = false;

    private Uri photoUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_composition);

        btnAdd = findViewById(R.id.fabAdd);
        btnCamera = findViewById(R.id.fabAddFromCamera);
        btnGallery = findViewById(R.id.fabAddFromGallery);

        fabOpen = AnimationUtils.loadAnimation(this, R.anim.fab_open);
        fabClose = AnimationUtils.loadAnimation(this, R.anim.fab_close);
        rotateForward =  AnimationUtils.loadAnimation(this, R.anim.rotation_forward);
        rotateBackward =  AnimationUtils.loadAnimation(this, R.anim.rotation_backward);

        composition = (Composition) getIntent().getSerializableExtra(AppGlobals.TO_COMPOSITION_KEY);
        toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("Editor de composición");
        setSupportActionBar(toolbar);

        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        ((TextView) findViewById(R.id.composition)).setText(composition.getName());

        recyclerView = findViewById(R.id.rv_photos);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerAdapter = new PhotoCompositionRecyclerAdapter(composition, this);

        recyclerView.setAdapter(recyclerAdapter);

        itemTouchHelper = new ItemTouchHelper(simpleCallback);
        itemTouchHelper.attachToRecyclerView(recyclerView);

    }

    public void animateFabs(View v) {
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
    }

    public void addPhotoFromCamera(View v) {
        animateFabs(v);

        Intent cameraOpenIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        File file = new File(getExternalCacheDir(), String.valueOf(System.currentTimeMillis()));

        photoUri = FileProvider.getUriForFile(this,
                AppGlobals.APP_SIGNATURE + ".provider", file);

        cameraOpenIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri);
        cameraOpenIntent.putExtra("return-data",true);

        startActivityForResult(cameraOpenIntent, AppGlobals.REQUEST_CODE_CAMERA);
    }

    public void addPhotoFromGallery(View v) {
        animateFabs(v);

        Intent galleryOpen = new Intent(Intent.ACTION_PICK,
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI);

        startActivityForResult(Intent.createChooser(galleryOpen,
                "Selecciona una imagen"), AppGlobals.REQUEST_CODE_STORAGE);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {

        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK) {
            switch (requestCode) {

                case AppGlobals.REQUEST_CODE_CAMERA:
                    addPhoto(photoUri);
                    break;

                case AppGlobals.REQUEST_CODE_STORAGE:
                    assert data != null;
                    addPhoto(Objects.requireNonNull(data.getData()));
                    break;

                default:
                    throw new IllegalStateException("Error request code : " + requestCode);
            }
        }
    }

    private void addPhoto(Uri uriSrc) {
//        File fileSrc = new File(uriSrc.getPath());
//        File fileDst = new File(composition.getAbsolutePath(), String.valueOf(System.currentTimeMillis()));
//
//        try {
//            if(fileDst.createNewFile())
//                IOCompositions.copyPhoto(fileSrc, fileDst);
//        } catch (IOException e) {
//            e.printStackTrace();
//        }

        System.out.println(uriSrc);
        System.out.println(uriSrc.getPath());

        composition.addPhoto(new PhotoComposition(uriSrc));
        recyclerAdapter.notifyDataSetChanged();
    }

    @Override
    public void onItemClick(int position) {
        Toast.makeText(this, "" + position, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onLongItemClick(int position) {
        Toast.makeText(this, "Long" + position, Toast.LENGTH_SHORT).show();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    ItemTouchHelper.SimpleCallback simpleCallback = new ItemTouchHelper.SimpleCallback(ItemTouchHelper.UP | ItemTouchHelper.DOWN | ItemTouchHelper.START | ItemTouchHelper.END, 0) {
        @Override
        public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {

            int srcPosition = viewHolder.getAdapterPosition();
            int dstPosition = target.getAdapterPosition();

            Collections.swap(composition.getListUris(), srcPosition, dstPosition);
            Objects.requireNonNull(recyclerView.getAdapter()).notifyItemMoved(srcPosition, dstPosition);

            return false;
        }

        @Override
        public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
            // Swipe Right to Left to delete.
            if(direction == ItemTouchHelper.LEFT) {

                final int position  = viewHolder.getAdapterPosition();

                photoDeleted = composition.removePhoto(position);
                recyclerAdapter.notifyItemRemoved(position);

                Snackbar.make(recyclerView, "Foto eliminada"
                        , Snackbar.LENGTH_INDEFINITE)
                        .setDuration(3000)
                        .setAction("Deshacer", new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                composition.addPhotoAt(position, photoDeleted);
                                recyclerAdapter.notifyItemInserted(position);
                            }
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
                    .setSwipeLeftLabelColor(getResources().getColor(R.color.colorPrimary))
                    .create()
                    .decorate();

            super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
        }
    };

}
