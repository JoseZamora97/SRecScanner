package com.josezamora.srecscanner.viewholders;

import android.animation.ValueAnimator;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.josezamora.srecscanner.R;
import com.josezamora.srecscanner.activities.NotebookActivity;


/**
 * The type Cloud image view holder.
 */
public class CloudImageViewHolder extends RecyclerView.ViewHolder {

    private ImageView imageView;
    private ProgressBar progressBar;
    private CardView cardView;
    private TextView textView;
    private static int ANIM_SPEED = 300;

    private boolean expanded;

    private ValueAnimator animExpand;
    private ValueAnimator animCollapse;

    private int height;
    private ImageView btnFullScreen;

    /**
     * Instantiates a new Cloud image view holder.
     *
     * @param itemView         the item view
     * @param notebookActivity the notebook activity
     */
    public CloudImageViewHolder(@NonNull final View itemView,
                                final NotebookActivity notebookActivity) {
        super(itemView);

        imageView = itemView.findViewById(R.id.imagePreview);
        progressBar = itemView.findViewById(R.id.progress_bar);
        cardView = itemView.findViewById(R.id.cardview_imagen_item);
        textView = itemView.findViewById(R.id.text_image);
        btnFullScreen = itemView.findViewById(R.id.full_screen);

        btnFullScreen.setVisibility(View.GONE);

        ViewTreeObserver viewTreeObserver = cardView.getViewTreeObserver();

        ViewTreeObserver.OnGlobalLayoutListener viewTreeListener = new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {

                height = cardView.getMeasuredHeight();

                animExpand = ValueAnimator.ofInt(height, height * 2);
                animExpand.setDuration(ANIM_SPEED);
                animExpand.addUpdateListener(valueAnimator -> {
                    RecyclerView.LayoutParams cardLayoutParams = (RecyclerView.LayoutParams) cardView.getLayoutParams();
                    cardLayoutParams.height = (Integer) valueAnimator.getAnimatedValue();
                    cardView.setLayoutParams(cardLayoutParams);
                });

                animCollapse = ValueAnimator.ofInt(height * 2, height);
                animCollapse.setDuration(ANIM_SPEED);
                animCollapse.addUpdateListener(valueAnimator -> {
                    RecyclerView.LayoutParams cardLayoutParams = (RecyclerView.LayoutParams) cardView.getLayoutParams();
                    cardLayoutParams.height = (Integer) valueAnimator.getAnimatedValue();
                    cardView.setLayoutParams(cardLayoutParams);
                });

                cardView.getViewTreeObserver().removeOnGlobalLayoutListener(this);
            }
        };

        viewTreeObserver.addOnGlobalLayoutListener(viewTreeListener);

        itemView.setOnClickListener(view -> notebookActivity
                .onItemClick(getAdapterPosition()));

        itemView.setOnLongClickListener(view -> {
            notebookActivity.onLongItemClick(getAdapterPosition());
            return false;
        });

        btnFullScreen.setOnClickListener(v -> notebookActivity
                .toImageFullscreen(getAdapterPosition()));
    }

    /**
     * Update.
     */
    public void update() {
        if (!expanded) {
            animExpand.start();

            btnFullScreen.setVisibility(View.VISIBLE);
            btnFullScreen.setEnabled(true);
        } else {
            animCollapse.start();

            btnFullScreen.setVisibility(View.GONE);
            btnFullScreen.setEnabled(false);
        }

        expanded = !expanded;
    }

    /**
     * Is expanded boolean.
     *
     * @return the boolean
     */
    public boolean isExpanded() {
        return expanded;
    }

    /**
     * Gets image view.
     *
     * @return the image view
     */
    public ImageView getImageView() {
        return imageView;
    }

    /**
     * Gets progress bar.
     *
     * @return the progress bar
     */
    public ProgressBar getProgressBar() {
        return progressBar;
    }

    /**
     * Gets text view.
     *
     * @return the text view
     */
    public TextView getTextView() {
        return textView;
    }
}
