package com.josezamora.srecscanner.viewholders;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.josezamora.srecscanner.R;
import com.josezamora.srecscanner.activities.RecyclerViewOnClickInterface;

public class CloudNotebookViewHolder extends RecyclerView.ViewHolder {

    private ImageView imageLanguage;
    private TextView txtNameNotebook;
    private TextView txtNumImages;

    public CloudNotebookViewHolder(View itemView, final RecyclerViewOnClickInterface onClickInterface) {
        super(itemView);

        imageLanguage = itemView.findViewById(R.id.imageLanguage);
        txtNameNotebook = itemView.findViewById(R.id.textviewName);
        txtNumImages = itemView.findViewById(R.id.num_images);

        itemView.setOnClickListener(v -> onClickInterface.onItemClick(getAdapterPosition()));
    }

    public TextView getTxtNameNotebook() {
        return txtNameNotebook;
    }

    public TextView getTxtNumImages(){
        return txtNumImages;
    }

}
