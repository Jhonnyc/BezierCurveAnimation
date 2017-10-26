package com.yonikal.beziercurveanimation;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.app.Activity;
import android.graphics.Color;
import android.graphics.Path;
import android.graphics.PathMeasure;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Created by yonikal on 19/08/17.
 */
public class ActivityTestcp extends Activity {

    private RelativeLayout mContainer;
    private LinearLayout mParent;
    private ImageView imageView;

    private final int MIN_PLANES_ON_SCREEN = 1;
    private final int MAX_PLANES_ON_SCREEN = 4;

    private final int MIN_EDGE_DELTA = -200;
    private final int MAX_EDGE_DELTA = 200;

    private static final int MIN_ANIMATION_TIME = 1800;
    private static final int MAX_ANIMATION_TIME = 3000;
    private static final int[] COLORS = new int[]{Color.RED, Color.YELLOW, Color.GREEN, Color.BLUE, Color.MAGENTA, Color.CYAN};

    private Handler h;
    private int timeInterval = 800;
    Runnable r = new Runnable() {
        @Override
        public void run() {
            createAnimation();
            h.postDelayed(r, timeInterval);
        }
    };


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);
        mContainer = (RelativeLayout) findViewById(R.id.container);
        mParent = (LinearLayout) findViewById(R.id.parent);
        imageView = (ImageView) findViewById(R.id.plane_iv);
        findViewById(R.id.again).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                r.run();
            }
        });
        h = new Handler();
    }

    @Override
    protected void onResume() {
        super.onResume();

        mParent.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                createAnimation();
                mParent.getViewTreeObserver().removeOnGlobalLayoutListener(this);
            }
        });
    }

    private void createAnimation() {
        List<AnimatedPlaneView> planes = new ArrayList<>();
        int planesOnScreen = new Random().nextInt(MAX_PLANES_ON_SCREEN - MIN_PLANES_ON_SCREEN) + MIN_PLANES_ON_SCREEN;
        for (int i = 0; i < planesOnScreen; i++) {
            planes.add(new AnimatedPlaneView(mContainer, getRandomPath()));
        }

        for (AnimatedPlaneView plane : planes) {
            plane.startAnimtion();
        }
    }

    public PathWithVariables getRandomPath() {

        float width = mContainer.getWidth();
        float x1 = width / 3;
        float x2 = x1 * 2;
        float singleImageWidth = 2 * imageView.getWidth();

        final Path path = new Path();
        PathWithVariables randomPath = new PathWithVariables();
        randomPath.path = path;
        randomPath.startX = 0 - singleImageWidth;
        randomPath.startY = getRandomEdgeHeight();
        path.moveTo(randomPath.startX, randomPath.startY);
        path.cubicTo(x1, getRandomHeight(), x2, getRandomHeight(), width + singleImageWidth, getRandomEdgeHeight());

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

    public static class PathWithVariables {
        Path path;
        float startX;
        float startY;
    }

    public static class AnimatedPlaneView {

        float[] last;
        ImageView iv;
        ValueAnimator pathAnimator;

        public AnimatedPlaneView(final ViewGroup parentView, PathWithVariables path) {
            last = new float[]{parentView.getX() + parentView.getHeight() / 2, parentView.getWidth() / 2};
            iv = new ImageView(parentView.getContext());
            iv.setImageResource(R.mipmap.plane);

            RelativeLayout.LayoutParams rlParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT,
                    RelativeLayout.LayoutParams.WRAP_CONTENT);
            iv.setLayoutParams(rlParams);
            iv.setX(path.startX);
            iv.setY(path.startY);
            int color = COLORS[new Random().nextInt(COLORS.length)];
            iv.setColorFilter(color);

            parentView.addView(iv);

            final PathMeasure pm = new PathMeasure(path.path, false);

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
                    iv.setX(last[0]);
                    iv.setY(last[1]);

                    pm.getPosTan(distance, last, tan);
                    float deg = (float) (Math.atan2(tan[1], tan[0]) * 180.0 / Math.PI);
                    iv.setRotation(deg);
                }
            });
            pathAnimator.addListener(new AnimatorListenerAdapter() {

                @Override
                public void onAnimationStart(Animator animation) {
                    super.onAnimationStart(animation);
                    iv.setLayerType(View.LAYER_TYPE_HARDWARE, null);
                }

                @Override
                public void onAnimationEnd(Animator animation) {
                    super.onAnimationEnd(animation);
                    iv.setVisibility(View.GONE);
                    iv.setLayerType(View.LAYER_TYPE_NONE, null);
                    iv.setDrawingCacheEnabled(false);
                    parentView.removeView(iv);
                }

            });
        }

        public void startAnimtion() {
            pathAnimator.start();
        }

    }
}
