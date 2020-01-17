package com.josezamora.tcscanner.ViewHolders;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.josezamora.tcscanner.Activities.RecyclerViewOnClickInterface;
import com.josezamora.tcscanner.R;

import androidx.recyclerview.widget.RecyclerView;

public class CloudCompositionViewHolder extends RecyclerView.ViewHolder  {

    private ImageView imageLanguage;
    private TextView txtNameComposition;
    private TextView txtNumImages;

    public CloudCompositionViewHolder(View itemView, final RecyclerViewOnClickInterface onClickInterface) {
        super(itemView);

        imageLanguage = itemView.findViewById(R.id.imageLanguage);
        txtNameComposition = itemView.findViewById(R.id.textviewName);
        txtNumImages = itemView.findViewById(R.id.num_images);

        itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onClickInterface.onItemClick(getAdapterPosition());
            }
        });
    }

    public TextView getTxtNameComposition() {
        return txtNameComposition;
    }

    public TextView getTxtNumImages(){
        return txtNumImages;
    }

}
