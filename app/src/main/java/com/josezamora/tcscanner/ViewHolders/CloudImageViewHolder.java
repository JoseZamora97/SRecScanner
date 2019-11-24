package com.josezamora.tcscanner.ViewHolders;

import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.josezamora.tcscanner.Interfaces.RecyclerViewOnClickInterface;
import com.josezamora.tcscanner.R;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class CloudImageViewHolder extends RecyclerView.ViewHolder {

    private ImageView imageView;
    private ProgressBar progressBar;

    public CloudImageViewHolder(@NonNull View itemView,
                                final RecyclerViewOnClickInterface recyclerViewOnClickInterface) {
        super(itemView);

        imageView = itemView.findViewById(R.id.imagePreview);
        progressBar = itemView.findViewById(R.id.progress_bar);

        itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                recyclerViewOnClickInterface.onItemClick(getAdapterPosition());
            }
        });

        itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                recyclerViewOnClickInterface.onLongItemClick(getAdapterPosition());
                return false;
            }
        });

    }

    public ImageView getImageView() {
        return imageView;
    }

    public ProgressBar getProgressBar() {
        return progressBar;
    }
}
