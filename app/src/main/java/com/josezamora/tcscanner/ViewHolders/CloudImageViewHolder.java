package com.josezamora.tcscanner.ViewHolders;

import android.animation.ValueAnimator;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.josezamora.tcscanner.Activities.NotebookActivity;
import com.josezamora.tcscanner.R;


public class CloudImageViewHolder extends RecyclerView.ViewHolder {

    private ImageView imageView;
    private ProgressBar progressBar;
    private CardView cardView;
    private TextView textView;
    private ImageView btnfullScreen;

    private boolean expanded;

    private ValueAnimator animExpand;
    private ValueAnimator animCollapse;

    private int height;

    private static int ANIM_SPEED = 250;

    public CloudImageViewHolder(@NonNull final View itemView,
                                final NotebookActivity recyclerViewOnClickInterface) {
        super(itemView);

        imageView = itemView.findViewById(R.id.imagePreview);
        progressBar = itemView.findViewById(R.id.progress_bar);
        cardView = itemView.findViewById(R.id.cardview_imagen_item);
        textView = itemView.findViewById(R.id.text_image);
        btnfullScreen = itemView.findViewById(R.id.full_screen);

        btnfullScreen.setVisibility(View.GONE);

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

        itemView.setOnClickListener(view -> recyclerViewOnClickInterface.onItemClick(getAdapterPosition()));

        itemView.setOnLongClickListener(view -> {
            recyclerViewOnClickInterface.onLongItemClick(getAdapterPosition());
            return false;
        });

        btnfullScreen.setOnClickListener(v -> {
            recyclerViewOnClickInterface.toImageFullscreen(getAdapterPosition());
        });
    }

    public void update() {
        if (!expanded) {
            animExpand.start();

            btnfullScreen.setVisibility(View.VISIBLE);
            btnfullScreen.setEnabled(true);
        } else {
            animCollapse.start();

            btnfullScreen.setVisibility(View.GONE);
            btnfullScreen.setEnabled(false);
        }

        expanded = !expanded;
    }

    public boolean isExpanded() {
        return expanded;
    }

    public ImageView getImageView() {
        return imageView;
    }

    public ProgressBar getProgressBar() {
        return progressBar;
    }

    public TextView getTextView() {
        return textView;
    }
}
