package com.yonikal.uibezierpath;

import android.os.Handler;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.ImageView;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Created by yoni on 22/10/2017.
 */
public class UIBezierPath {

    private final int TIME_INTERVAL = 800;
    private final int MAX_EDGE_DELTA = 200;
    private final int MIN_EDGE_DELTA = -200;
    private final int MIN_PLANES_ON_SCREEN = 1;
    private final int MAX_PLANES_ON_SCREEN = 4;

    private int mImage;
    private ViewGroup mContainer;
    private ImageView mImageView;
    private Handler mHandler;

    private Runnable mTask = new Runnable() {
        @Override
        public void run() {
            createAnimation();
            mHandler.postDelayed(mTask, TIME_INTERVAL);
        }
    };

    public UIBezierPath(final ViewGroup container, final ImageView imageView, final int image, final OnSetup onSetup) {
        container.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                container.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                mContainer = container;
                mHandler = new Handler();
                mImageView = imageView;
                mImage = image;
                if (onSetup != null) {
                    onSetup.onSetupDone();
                }
            }
        });
    }

    private DataPath getRandomPath() {
        float width = mContainer.getWidth();
        float x1 = width / 3;
        float x2 = x1 * 2;
        float singleImageDoubleWidth = 2 * mImageView.getWidth();

        float startX = 0 - singleImageDoubleWidth;
        float startY = getRandomEdgeHeight();
        DataPath randomPath = new DataPath(startX, startY);

        randomPath.moveTo(randomPath.getStartX(), randomPath.getStartY());
        randomPath.cubicTo(x1, getRandomHeight(), x2, getRandomHeight(), width + singleImageDoubleWidth,
                getRandomEdgeHeight());

        return randomPath;
    }

    private float getRandomHeight() {
        float low = mContainer.getTop();
        float high = mContainer.getBottom();
        int d = (int) (high - low);
        return new Random().nextInt(d);
    }

    private float getRandomEdgeHeight() {
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
            plane.startAnimation();
        }
    }

    public void startAnimation() {
        createAnimation();
    }

}
