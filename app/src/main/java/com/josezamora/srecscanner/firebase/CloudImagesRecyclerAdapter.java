package com.josezamora.srecscanner.firebase;

import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.firebase.ui.common.ChangeEventType;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.firestore.DocumentSnapshot;
import com.josezamora.srecscanner.R;
import com.josezamora.srecscanner.activities.NotebookActivity;
import com.josezamora.srecscanner.firebase.Classes.CloudImage;
import com.josezamora.srecscanner.viewholders.CloudImageViewHolder;

import java.util.ArrayList;
import java.util.List;

public class CloudImagesRecyclerAdapter extends FirestoreRecyclerAdapter<CloudImage, CloudImageViewHolder> {

    private NotebookActivity activity;
    private List<CloudImage> listImages = new ArrayList<>();

    /**
     * Create a new RecyclerView adapter that listens to a Firestore Query.  See {@link
     * FirestoreRecyclerOptions} for configuration options.
     *
     * @param options options to build the adapter.
     */
    public CloudImagesRecyclerAdapter(@NonNull FirestoreRecyclerOptions<CloudImage> options,
                                      NotebookActivity activity) {
        super(options);
        this.activity = activity;
    }

    @NonNull
    @Override
    public CloudImageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_photo_item, parent, false);
        return new CloudImageViewHolder(view, activity);
    }

    @Override
    protected void onBindViewHolder(@NonNull CloudImageViewHolder holder, int position,
                                    @NonNull CloudImage model) {

        holder.getTextView().setText(model.getId());
        fetchImage(holder, model);
    }

    public List<CloudImage> getCloudImages(){
        return listImages;
    }

    private void fetchImage(final CloudImageViewHolder holder, CloudImage image) {
        GlideApp.with(activity)
                .load(image.getDownloadLink())
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .transition(DrawableTransitionOptions.withCrossFade())
                .listener(new RequestListener<Drawable>() {
                    @Override
                    public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                        holder.getProgressBar().setVisibility(View.GONE);
                        return false;
                    }
                    @Override
                    public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                        holder.getProgressBar().setVisibility(View.GONE);
                        return false;
                    }
                })
                .into(holder.getImageView());

    }

    @Override
    public void onChildChanged(@NonNull ChangeEventType type,
                               @NonNull DocumentSnapshot snapshot,
                               int newIndex, int oldIndex) {

        if(newIndex != -1) {
            CloudImage model = getSnapshots().get(newIndex);
            onChildUpdate(model, type, newIndex, oldIndex);
        }
        else
            onChildUpdate(null, type, newIndex, oldIndex);
    }

    private void onChildUpdate(CloudImage model, ChangeEventType type,
                       int newIndex,
                       int oldIndex){

        switch (type) {
            case ADDED:
                listImages.add(model);
                notifyItemInserted(newIndex);
                break;

            case CHANGED:
                listImages.remove(newIndex);
                listImages.add(newIndex, model);
                notifyItemChanged(newIndex);
                break;

            case REMOVED:
                listImages.remove(oldIndex);
                notifyItemRemoved(oldIndex);
                break;

            case MOVED:
                listImages.remove(oldIndex);
                listImages.add(newIndex, model);
                notifyItemMoved(oldIndex, newIndex);
                break;

            default:
                throw new IllegalStateException("Incomplete case statement");
        }
    }
}
