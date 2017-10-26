package com.yonikal.uibezierpath;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.graphics.Color;
import android.graphics.PathMeasure;
import android.os.Handler;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Created by yoni on 22/10/2017.
 */
public class UIBezierPath {

    private final int MIN_PLANES_ON_SCREEN = 1;
    private final int MAX_PLANES_ON_SCREEN = 4;
    private final int MIN_EDGE_DELTA = -200;
    private final int MAX_EDGE_DELTA = 200;
    private static final int MIN_ANIMATION_TIME = 1800;
    private static final int MAX_ANIMATION_TIME = 3000;
    private static final int TIME_INTERVAL = 800;
    private static final int[] COLORS = new int[]{Color.RED, Color.YELLOW, Color.GREEN, Color.BLUE, Color.MAGENTA, Color.CYAN};

    private ViewGroup mContainer;
    private ImageView mImageView;
    private int mImage;
    private Handler mHandler;

    private Runnable r = new Runnable() {
        @Override
        public void run() {
            createAnimation();
            mHandler.postDelayed(r, TIME_INTERVAL);
        }
    };

    public UIBezierPath(ViewGroup container, ImageView imageView, int image) {
        mContainer = container;
        mHandler = new Handler();
        mImageView = imageView;
        mImage = image;
    }

    public DataPath getRandomPath() {

        float width = mContainer.getWidth();
        float x1 = width / 3;
        float x2 = x1 * 2;
        float singleImageWidth = 2 * mImageView.getWidth();

        float startX = 0 - singleImageWidth;
        float startY = getRandomEdgeHeight();
        DataPath randomPath = new DataPath(startX, startY);

        randomPath.moveTo(randomPath.getStartX(), randomPath.getStartY());
        randomPath.cubicTo(x1, getRandomHeight(), x2, getRandomHeight(), width + singleImageWidth,
                getRandomEdgeHeight());

        return randomPath;
    }

    public float getRandomHeight() {
        float low = mContainer.getTop();
        float high = mContainer.getBottom();
        int d = (int) (high - low);
        return new Random().nextInt(d);
    }

    public float getRandomEdgeHeight() {
        float height = mContainer.getHeight() / 2;
        return height + (new Random().nextInt(MAX_EDGE_DELTA - MIN_EDGE_DELTA) + MIN_EDGE_DELTA);
    }

    private void createAnimation() {
        List<AnimatedPlaneView> planes = new ArrayList<>();
        int planesOnScreen = new Random().nextInt(MAX_PLANES_ON_SCREEN - MIN_PLANES_ON_SCREEN) + MIN_PLANES_ON_SCREEN;
        for (int i = 0; i < planesOnScreen; i++) {
            planes.add(new AnimatedPlaneView(mContainer, getRandomPath(), mImage));
        }

        for (AnimatedPlaneView plane : planes) {
            plane.startAnimtion();
        }
    }

    public static class AnimatedPlaneView {

        float[] last;
        ImageView imageView;
        ValueAnimator pathAnimator;

        public AnimatedPlaneView(final ViewGroup parentView, DataPath path, int imageview) {
            last = new float[]{parentView.getX() + parentView.getHeight() / 2, parentView.getWidth() / 2};
            imageView = new ImageView(parentView.getContext());
            imageView.setImageResource(imageview);

            RelativeLayout.LayoutParams rlParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT,
                    RelativeLayout.LayoutParams.WRAP_CONTENT);
            imageView.setLayoutParams(rlParams);
            imageView.setX(path.getStartX());
            imageView.setY(path.getStartY());
            int color = COLORS[new Random().nextInt(COLORS.length)];
            imageView.setColorFilter(color);

            parentView.addView(imageView);

            final PathMeasure pm = new PathMeasure(path, false);

            pathAnimator = ValueAnimator.ofFloat(0.0f, pm.getLength());
            pathAnimator.setDuration(new Random().nextInt(MAX_ANIMATION_TIME - MIN_ANIMATION_TIME) + MIN_ANIMATION_TIME);
            pathAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator animation) {
                    float val = animation.getAnimatedFraction();

                    float distance = pm.getLength();
                    if (val != 0) {
                        distance = pm.getLength() * val;
                    }
                    float[] tan = new float[2];
                    imageView.setX(last[0]);
                    imageView.setY(last[1]);

                    pm.getPosTan(distance, last, tan);
                    float deg = (float) (Math.atan2(tan[1], tan[0]) * 180.0 / Math.PI);
                    imageView.setRotation(deg);
                }
            });
            pathAnimator.addListener(new AnimatorListenerAdapter() {

                @Override
                public void onAnimationStart(Animator animation) {
                    super.onAnimationStart(animation);
                    imageView.setLayerType(View.LAYER_TYPE_HARDWARE, null);
                }

                @Override
                public void onAnimationEnd(Animator animation) {
                    super.onAnimationEnd(animation);
                    imageView.setVisibility(View.GONE);
                    imageView.setLayerType(View.LAYER_TYPE_NONE, null);
                    imageView.setDrawingCacheEnabled(false);
                    parentView.removeView(imageView);
                }

            });
        }

        public void startAnimtion() {
            pathAnimator.start();
        }

    }
}
