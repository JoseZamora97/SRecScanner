package com.josezamora.tcscanner.ViewHolders;

import android.animation.ValueAnimator;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;

import com.josezamora.tcscanner.Interfaces.RecyclerViewOnClickInterface;
import com.josezamora.tcscanner.R;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;


public class CloudImageViewHolder extends RecyclerView.ViewHolder {

    private ImageView imageView;
    private ProgressBar progressBar;
    private CardView cardView;

    private boolean expanded;

    private ValueAnimator animExpand;
    private ValueAnimator animCollapse;

    private int height;

    private static int ANIM_SPEED = 300;

    public CloudImageViewHolder(@NonNull final View itemView,
                                final RecyclerViewOnClickInterface recyclerViewOnClickInterface) {
        super(itemView);

        imageView = itemView.findViewById(R.id.imagePreview);
        progressBar = itemView.findViewById(R.id.progress_bar);
        cardView = itemView.findViewById(R.id.cardview_imagen_item);

        ViewTreeObserver viewTreeObserver = cardView.getViewTreeObserver();

        ViewTreeObserver.OnGlobalLayoutListener viewTreeListener = new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                height = cardView.getMeasuredHeight();

                animExpand = ValueAnimator.ofInt(height, height * 2);
                animExpand.setDuration(ANIM_SPEED);
                animExpand.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                    @Override
                    public void onAnimationUpdate(ValueAnimator valueAnimator) {
                        int val = (Integer) valueAnimator.getAnimatedValue();
                        RecyclerView.LayoutParams layoutParams = (RecyclerView.LayoutParams) cardView.getLayoutParams();
                        layoutParams.height = val;
                        cardView.setLayoutParams(layoutParams);
                    }
                });

                animCollapse = ValueAnimator.ofInt(height * 2, height);
                animCollapse.setDuration(ANIM_SPEED);
                animCollapse.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                    @Override
                    public void onAnimationUpdate(ValueAnimator valueAnimator) {
                        int val = (Integer) valueAnimator.getAnimatedValue();
                        RecyclerView.LayoutParams layoutParams = (RecyclerView.LayoutParams) cardView.getLayoutParams();
                        layoutParams.height = val;
                        cardView.setLayoutParams(layoutParams);
                    }
                });

                cardView.getViewTreeObserver().removeOnGlobalLayoutListener(this);
            }
        };

        viewTreeObserver.addOnGlobalLayoutListener(viewTreeListener);

        itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!expanded)
                    animExpand.start();
                else
                    animCollapse.start();

                expanded = !expanded;
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

    public void collapse() {
        if(expanded)
            animCollapse.start();

        expanded = !expanded;
    }

    public void expand(){
        if(!expanded)
            animExpand.start();

        expanded = !expanded;
    }
}
