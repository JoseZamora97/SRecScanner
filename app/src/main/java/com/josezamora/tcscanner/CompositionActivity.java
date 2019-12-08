package com.josezamora.tcscanner;

import android.content.Intent;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.josezamora.tcscanner.Firebase.Classes.CloudComposition;
import com.josezamora.tcscanner.Firebase.Classes.CloudImage;
import com.josezamora.tcscanner.Firebase.Classes.CloudUser;
import com.josezamora.tcscanner.Firebase.Controllers.FirebaseController;
import com.josezamora.tcscanner.Interfaces.AppGlobals;
import com.josezamora.tcscanner.Interfaces.RecyclerViewOnClickInterface;
import com.josezamora.tcscanner.ViewHolders.CloudImageViewHolder;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.Objects;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import it.xabaras.android.recyclerview.swipedecorator.RecyclerViewSwipeDecorator;


public class CompositionActivity extends AppCompatActivity
        implements RecyclerViewOnClickInterface {

    Toolbar toolbar;

    CloudComposition composition;
    CloudUser user;

    ProgressBar progressBar;

    RecyclerView recyclerView;
    ItemTouchHelper itemTouchHelper;

    FirebaseController firebaseController;

    FloatingActionButton btnAdd, btnCamera, btnGallery;
    Animation fabOpen, fabClose, rotateForward, rotateBackward;

    private boolean isOpen = false;

    private Uri photoUri;
    private FirestoreRecyclerAdapter cloudImagesAdapter;

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

        cloudImagesAdapter = getCloudRecyclerAdapter();

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
                    uploadImage(photoUri);
                    break;
                case AppGlobals.REQUEST_CODE_STORAGE:
                    assert data != null;
                    uploadImage(data.getData());
                    break;
            }
        }
    }

    @Override
    public void onItemClick(int position) {
        CloudImage image = (CloudImage)cloudImagesAdapter.getItem(position);
        for (int childCount = recyclerView.getChildCount(), i = 0; i < childCount; ++i) {
            CloudImageViewHolder holder = (CloudImageViewHolder)recyclerView
                    .getChildViewHolder(recyclerView.getChildAt(i));
            if(i == position) {
                holder.update();
            }
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
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private FirestoreRecyclerAdapter getCloudRecyclerAdapter() {

        final int resourceLayout = R.layout.list_photo_item;
        final RecyclerViewOnClickInterface rvOnClick = this;

        return new FirestoreRecyclerAdapter<CloudImage, CloudImageViewHolder>(
                firebaseController.getRecyclerOptions(user, composition)) {

            @NonNull
            @Override
            public CloudImageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext())
                        .inflate(resourceLayout, parent, false);
                return new CloudImageViewHolder(view, rvOnClick);
            }

            @Override
            protected void onBindViewHolder(@NonNull CloudImageViewHolder holder, int position,
                                            @NonNull CloudImage model) {


                fetchImage(holder, model);
            }
        };
    }

    private void fetchImage(CloudImageViewHolder holder, CloudImage image) {
        ImageView imageView = holder.getImageView();
        final ProgressBar progressBar = holder.getProgressBar();

        GlideApp.with(getApplicationContext())
                .load(firebaseController.getReference(image))
                .transition(DrawableTransitionOptions.withCrossFade())
                .listener(new RequestListener<Drawable>() {
                    @Override
                    public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                        progressBar.setVisibility(View.GONE);
                        return false;
                    }
                    @Override
                    public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                        progressBar.setVisibility(View.GONE);
                        return false;
                    }
                })
                .into(imageView);
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
            stream = getContentResolver().openInputStream(data);
            firebaseController.uploadImage(user, composition, stream);
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
            Objects.requireNonNull(recyclerView.getAdapter()).notifyItemMoved(srcPosition,
                    dstPosition);

            return true;
        }

        boolean undo;

        @Override
        public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {

            if(direction == ItemTouchHelper.LEFT) {
                final int position  = viewHolder.getAdapterPosition();
                final CloudImage image = (CloudImage) cloudImagesAdapter.getItem(position);

                firebaseController.deleteImage(image, false);

                Snackbar.make(recyclerView, "Foto eliminada", Snackbar.LENGTH_INDEFINITE)
                        .setDuration(3000)
                        .addCallback(new Snackbar.Callback(){
                            @Override
                            public void onDismissed(Snackbar snackbar, int event) {
                                undo = event == BaseTransientBottomBar
                                        .BaseCallback.DISMISS_EVENT_ACTION;
                                if(!undo)
                                    firebaseController.deleteImage(image, false);
                                else
                                    firebaseController.addImage(image);
                            }
                        })
                        .setAction("Deshacer", new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {}
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
