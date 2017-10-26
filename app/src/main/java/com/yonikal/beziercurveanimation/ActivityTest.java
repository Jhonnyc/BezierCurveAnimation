package com.yonikal.beziercurveanimation;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.widget.RelativeLayout;

/**
 * Created by yonikal on 19/08/17.
 */
public class ActivityTest extends Activity {

    private RelativeLayout mContainer;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);
        mContainer = (RelativeLayout) findViewById(R.id.container);
    }

    @Override
    protected void onResume() {
        super.onResume();
    }
}
