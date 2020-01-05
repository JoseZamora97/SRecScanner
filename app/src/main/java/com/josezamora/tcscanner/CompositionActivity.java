package com.josezamora.tcscanner;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.FutureTarget;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.common.collect.BiMap;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.storage.UploadTask;
import com.josezamora.tcscanner.Firebase.Adapters.CloudCompositionRecyclerAdapter;
import com.josezamora.tcscanner.Firebase.Classes.CloudComposition;
import com.josezamora.tcscanner.Firebase.Classes.CloudImage;
import com.josezamora.tcscanner.Firebase.Classes.CloudUser;
import com.josezamora.tcscanner.Firebase.Controllers.FirebaseController;
import com.josezamora.tcscanner.Interfaces.AppGlobals;
import com.josezamora.tcscanner.Interfaces.RecyclerViewOnClickInterface;
import com.josezamora.tcscanner.ViewHolders.CloudImageViewHolder;
import com.theartofdev.edmodo.cropper.CropImage;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutionException;

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
import it.xabaras.android.recyclerview.swipedecorator.RecyclerViewSwipeDecorator;

import static android.util.TypedValue.COMPLEX_UNIT_SP;


public class CompositionActivity extends AppCompatActivity
        implements RecyclerViewOnClickInterface {

    Toolbar toolbar;

    CloudComposition composition;
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
    private CloudCompositionRecyclerAdapter cloudImagesAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_composition);

        firebaseController = new FirebaseController();

        user = CloudUser.userFromFirebase(
                Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()));

        composition = (CloudComposition) getIntent().getSerializableExtra(AppGlobals.COMPOSITION_KEY);

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

        ((TextView) findViewById(R.id.composition)).setText(composition.getName());
        recyclerView = findViewById(R.id.rv_photos);

        cloudImagesAdapter = new CloudCompositionRecyclerAdapter(
                firebaseController.getRecyclerOptions(user,composition), this);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(cloudImagesAdapter);

        DividerItemDecoration itemDecor = new DividerItemDecoration(this,
                DividerItemDecoration.VERTICAL);
        itemDecor.setDrawable(Objects.requireNonNull(
                ContextCompat.getDrawable(this, R.drawable.recycler_divider)));
        recyclerView.addItemDecoration(itemDecor);

        itemTouchHelper = new ItemTouchHelper(simpleCallback);
        itemTouchHelper.attachToRecyclerView(recyclerView);
    }

    @Override
    protected void onStart() {
        super.onStart();
        cloudImagesAdapter.startListening();
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
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
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
                    break;
            }
        }
    }

    @Override
    public void onItemClick(int position) {
        for (int childCount = recyclerView.getChildCount(), i = 0; i < childCount; ++i) {
            CloudImageViewHolder holder = (CloudImageViewHolder)recyclerView
                    .getChildViewHolder(recyclerView.getChildAt(i));
            if(i == position)
                holder.update();
            else
                if(holder.isExpanded())
                    holder.update();
        }
    }

    @Override
    public void onLongItemClick(int position) {
        Toast.makeText(this, "Arrastra y suelta la foto en la posición que desees",
                Toast.LENGTH_SHORT).show();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_scan, menu);

        MenuItem itemScan = menu.findItem(R.id.mItemActionScan);

        itemScan.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {

                if(cloudImagesAdapter.getCloudImages().size()>0) {
                    try {
                        toVisionActivity();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }

                return true;
            }
        });

        return super.onCreateOptionsMenu(menu);
    }

    private void cropImage(Uri uri) {
        CropImage.activity(uri)
                .setFixAspectRatio(true)
                .setActivityMenuIconColor(getResources().getColor(R.color.colorBodies))
                .setAspectRatio(AppGlobals.MAX_PHOTO_HEIGHT, AppGlobals.MAX_PHOTO_WIDTH)
                .setActivityTitle("Recortar")
                .start(this);
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

    private void toVisionActivity() throws InterruptedException {

        CountDownLatch countDownLatch = new CountDownLatch(cloudImagesAdapter.getItemCount());
        List<GlideImageDownload> tasks = new ArrayList<>();

        for(CloudImage image : cloudImagesAdapter.getCloudImages()){
            GlideImageDownload download = new GlideImageDownload(
                    image, countDownLatch, this);
            tasks.add(download);
            new Thread(download).start();
        }

        countDownLatch.await();

        List<Bitmap> bitmaps = new ArrayList<>();
        for(GlideImageDownload download : tasks)
            bitmaps.add(download.result);

        Bitmap temp = combineBitmaps(bitmaps);

        // TODO: fix parcel transaction
        Intent toVisionActivity = new Intent(this, VisionActivity.class);
        toVisionActivity.putExtra(AppGlobals.IMAGES_KEY, temp);
        startActivity(toVisionActivity);
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

        return resizeBitmap(bitmapResult);
    }

    public Bitmap resizeBitmap(Bitmap bitmap) {
        return bitmap;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            if(newChanges){

                AlertDialog.Builder builderConfig = new AlertDialog.Builder(this);

                builderConfig.setCancelable(false);
                builderConfig.setTitle("Ups!!");
                builderConfig.setMessage("Estás a punto de salir y hay cambios que no " +
                        "se han guardado.¿Desea guardar cambios y volver?");

                builderConfig.setPositiveButton("Guardar", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        saveChanges();
                        onBackPressed();
                    }
                });

                builderConfig.setNegativeButton("Descartar", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                        onBackPressed();
                    }
                });

                AlertDialog alertDialog = builderConfig.create();
                alertDialog.show();
            }
            else{
                onBackPressed();
            }

            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void saveChanges() {
        for (int i = 0; i<cloudImagesAdapter.getCloudImages().size(); i++) {
            CloudImage image = cloudImagesAdapter.getCloudImages().get(i);
            image.setOrder(i+1);
            firebaseController.update(image);
        }
    }

    public void animateFloatActionButtons(View v) {
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
        animateFloatActionButtons(v);

        Intent cameraOpenIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        File file = new File(getExternalCacheDir(),
                System.currentTimeMillis() + ".png");

        photoUri = FileProvider.getUriForFile(this,
                AppGlobals.APP_SIGNATURE + ".provider", file);

        cameraOpenIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri);
        cameraOpenIntent.putExtra("return-data",true);

        startActivityForResult(cameraOpenIntent, AppGlobals.REQUEST_CODE_CAMERA);
    }

    public void addPhotoFromGallery(View v) {
        animateFloatActionButtons(v);

        Intent galleryOpen = new Intent(Intent.ACTION_PICK,
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI);

        startActivityForResult(Intent.createChooser(galleryOpen,
                "Selecciona una imagen"), AppGlobals.REQUEST_CODE_STORAGE);
    }

    private void uploadImage(Uri data) {
        InputStream stream;
        try {
            cardViewProgressShow.setVisibility(View.VISIBLE);
            stream = getContentResolver().openInputStream(data);
            firebaseController
                    .uploadImage(user, composition, stream)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            cardViewProgressShow.setVisibility(View.GONE);
                        }
                    });
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

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

            if(srcPosition!=dstPosition) {
                newChanges = true;
                Collections.swap(cloudImagesAdapter.getCloudImages(), srcPosition, dstPosition);
                cloudImagesAdapter.notifyItemMoved(srcPosition, dstPosition);
            }

            return true;
        }

        boolean undo;

        @Override
        public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) { // Todo: Fix
//            if(direction == ItemTouchHelper.LEFT) {
//                final int position  = viewHolder.getAdapterPosition();
//                final CloudImage image = cloudImagesAdapter.getItem(position);
//
//                firebaseController.deleteImage(image, false);
//
//                Snackbar.make(recyclerView, "Foto eliminada", Snackbar.LENGTH_INDEFINITE)
//                        .setDuration(3000)
//                        .addCallback(new Snackbar.Callback(){
//                            @Override
//                            public void onDismissed(Snackbar snackbar, int event) {
//                                undo = event == BaseTransientBottomBar
//                                        .BaseCallback.DISMISS_EVENT_ACTION;
//                                if(!undo) firebaseController.deleteImage(image, false);
//                                else firebaseController.addImage(image);
//                            }
//                        })
//                        .setAction("Deshacer", new View.OnClickListener() {
//                            @Override
//                            public void onClick(View v) {}
//                        })
//                        .show();
//            }
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
}
