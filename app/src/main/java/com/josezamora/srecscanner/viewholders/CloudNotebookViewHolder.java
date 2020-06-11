package com.josezamora.srecscanner.viewholders;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.josezamora.srecscanner.R;
import com.josezamora.srecscanner.RecyclerViewOnClickListener;

/**
 * The type Cloud notebook view holder.
 */
public class CloudNotebookViewHolder extends RecyclerView.ViewHolder {

    private ImageView imageLanguage;
    private TextView txtNameNotebook;
    private TextView txtNumImages;

    /**
     * Instantiates a new Cloud notebook view holder.
     *
     * @param itemView         the item view
     * @param onClickInterface the on click interface
     */
    public CloudNotebookViewHolder(View itemView, final RecyclerViewOnClickListener onClickInterface) {
        super(itemView);

        imageLanguage = itemView.findViewById(R.id.imageLanguage);
        txtNameNotebook = itemView.findViewById(R.id.textviewName);
        txtNumImages = itemView.findViewById(R.id.num_images);

        itemView.setOnClickListener(v -> onClickInterface.onItemClick(getAdapterPosition()));
    }

    /**
     * Gets txt name notebook.
     *
     * @return the txt name notebook
     */
    public TextView getTxtNameNotebook() {
        return txtNameNotebook;
    }

    /**
     * Get txt num images text view.
     *
     * @return the text view
     */
    public TextView getTxtNumImages(){
        return txtNumImages;
    }

    public ImageView getImageLanguage() {
        return imageLanguage;
    }
}
