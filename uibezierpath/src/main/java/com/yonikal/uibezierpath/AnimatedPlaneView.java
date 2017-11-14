package com.yonikal.uibezierpath;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.graphics.Color;
import android.graphics.PathMeasure;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import java.util.Random;

/**
 * Created by yoni on 14/11/2017.
 */

public class AnimatedPlaneView {

    private static final int MIN_ANIMATION_TIME = 1800;
    private static final int MAX_ANIMATION_TIME = 3000;
    private static final int[] COLORS = new int[]{Color.RED, Color.YELLOW,
            Color.GREEN, Color.BLUE, Color.MAGENTA, Color.CYAN};

    private float[] mLast;
    private ImageView mImageView;
    private ValueAnimator mPathAnimator;

    public AnimatedPlaneView(final ViewGroup parentView, DataPath path, int imageView) {

        int color = COLORS[new Random().nextInt(COLORS.length)];
        mLast = new float[]{parentView.getX() + parentView.getHeight() / 2, parentView.getWidth() / 2};
        mImageView = new ImageView(parentView.getContext());
        mImageView.setImageResource(imageView);

        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT,
                RelativeLayout.LayoutParams.WRAP_CONTENT);
        mImageView.setLayoutParams(params);
        mImageView.setX(path.getStartX());
        mImageView.setY(path.getStartY());
        mImageView.setColorFilter(color);

        parentView.addView(mImageView);

        final PathMeasure pathMeasure = new PathMeasure(path, false);

        mPathAnimator = ValueAnimator.ofFloat(0.0f, pathMeasure.getLength());
        mPathAnimator.setDuration(new Random().nextInt(MAX_ANIMATION_TIME - MIN_ANIMATION_TIME) + MIN_ANIMATION_TIME);
        mPathAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                float val = animation.getAnimatedFraction();

                float distance = pathMeasure.getLength();
                if (val != 0) {
                    distance = pathMeasure.getLength() * val;
                }
                float[] tan = new float[2];
                mImageView.setX(mLast[0]);
                mImageView.setY(mLast[1]);

                pathMeasure.getPosTan(distance, mLast, tan);
                float degree = (float) (Math.atan2(tan[1], tan[0]) * 180.0 / Math.PI);
                mImageView.setRotation(degree);
            }
        });

        mPathAnimator.addListener(new AnimatorListenerAdapter() {

            @Override
            public void onAnimationStart(Animator animation) {
                super.onAnimationStart(animation);
                mImageView.setLayerType(View.LAYER_TYPE_HARDWARE, null);
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                mImageView.setVisibility(View.GONE);
                mImageView.setLayerType(View.LAYER_TYPE_NONE, null);
                mImageView.setDrawingCacheEnabled(false);
                parentView.removeView(mImageView);
            }
        });
    }

    public void startAnimation() {
        mPathAnimator.start();
    }

}
