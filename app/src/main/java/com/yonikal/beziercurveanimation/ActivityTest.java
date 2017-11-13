package com.yonikal.beziercurveanimation;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.ViewTreeObserver;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.yonikal.uibezierpath.UIBezierPath;

/**
 * Created by yonikal on 19/08/17.
 */
public class ActivityTest extends Activity {

    private RelativeLayout mContainer;
    private ImageView mPlaneImageView;
    private UIBezierPath mBezierPath;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);
        mContainer = (RelativeLayout) findViewById(R.id.container);
        mPlaneImageView = (ImageView) findViewById(R.id.plane_iv);
        mBezierPath = new UIBezierPath(mContainer, mPlaneImageView, R.mipmap.plane, new UIBezierPath.OnSetup() {
            @Override
            public void onSetupDone() {
                // Starting the animation
                mBezierPath.startAnimation();
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
    }
}
