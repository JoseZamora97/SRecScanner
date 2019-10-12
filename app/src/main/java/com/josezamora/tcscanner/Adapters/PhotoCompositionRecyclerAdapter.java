package com.josezamora.tcscanner.Adapters;

import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import com.josezamora.tcscanner.Classes.Composition;
import com.josezamora.tcscanner.Interfaces.RecyclerViewOnClickInterface;
import com.josezamora.tcscanner.R;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class PhotoCompositionRecyclerAdapter extends
        RecyclerView.Adapter<PhotoCompositionRecyclerAdapter.PhotoCompositionViewHolder> {

    private Composition composition;
    private RecyclerViewOnClickInterface recyclerViewOnClick;

    public PhotoCompositionRecyclerAdapter(Composition composition,
                                           RecyclerViewOnClickInterface recyclerViewOnClick) {
        this.composition = composition;
        this.recyclerViewOnClick = recyclerViewOnClick;

    }

    @NonNull
    @Override
    public PhotoCompositionViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        LayoutInflater li = LayoutInflater.from(parent.getContext());
        View view = li.inflate(R.layout.list_photo_item, parent, false);

        return new PhotoCompositionViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PhotoCompositionViewHolder holder, int position) {
        holder.imageView.setImageURI(Uri.parse(composition.getListPhotos()
                .get(position).getPhotoUri()));
    }

    @Override
    public int getItemCount() {
        return composition.getNumImages();
    }

    class PhotoCompositionViewHolder extends RecyclerView.ViewHolder {

        ImageView imageView;

        PhotoCompositionViewHolder(@NonNull final View itemView) {
            super(itemView);

            itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    recyclerViewOnClick.onLongItemClick(getAdapterPosition());
                    return true;
                }
            });

            itemView.findViewById(R.id.btnRestore).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Toast.makeText(itemView.getContext(), "Restaurar",
                            Toast.LENGTH_SHORT).show();
                }
            });

            itemView.findViewById(R.id.btnCrop).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Toast.makeText(itemView.getContext(), "Abrir Recortar",
                            Toast.LENGTH_SHORT).show();
                }
            });

            imageView = itemView.findViewById(R.id.imagePreview);

        }
    }
}
