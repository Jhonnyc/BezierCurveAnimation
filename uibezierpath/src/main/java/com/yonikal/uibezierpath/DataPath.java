package com.yonikal.uibezierpath;

import android.graphics.Path;

/**
 * Created by yoni on 24/10/2017.
 */

public class DataPath extends Path {

    private float mStartX;
    private float mStartY;

    public DataPath(float startX, float startY) {
        mStartX = startX;
        mStartY = startY;
    }

    public float getStartX() {
        return mStartX;
    }

    public float getStartY() {
        return mStartY;
    }



}
