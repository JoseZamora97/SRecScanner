package com.josezamora.tcscanner.ViewHolders;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.josezamora.tcscanner.R;

import androidx.recyclerview.widget.RecyclerView;

public class CloudCompositionViewHolder extends RecyclerView.ViewHolder {

    private ImageView imageView;
    private TextView txtName;

    public CloudCompositionViewHolder(View itemView) {
        super(itemView);
        imageView = itemView.findViewById(R.id.imagePreview);
        txtName = itemView.findViewById(R.id.textviewName);
    }

    public ImageView getImageView() {
        return imageView;
    }

    public void setImageView(ImageView imageView) {
        this.imageView = imageView;
    }

    public TextView getTxtName() {
        return txtName;
    }

    public void setTxtName(TextView txtName) {
        this.txtName = txtName;
    }
}
