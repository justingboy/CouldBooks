package com.himoo.ydsc.animation.openbook;


import android.view.animation.Animation;
import android.view.animation.Transformation;

public class ContentScaleAnimation extends Animation {
    private final float mFromX;
    private final float mToX;
    private final float mFromY;
    private final float mToY;
    private float mPivotX;
    private float mPivotY;
    private float mPivotXValue;
    private float mPivotYValue;
    private boolean mReverse;

    public ContentScaleAnimation(float mFromX, float mToX, float mFromY, float mToY, float mPivotXValue, float mPivotYValue, boolean mReverse) {
        this.mFromX = mFromX;
        this.mToX = mToX;
        this.mFromY = mFromY;
        this.mToY = mToY;
        this.mPivotXValue = mPivotXValue;
        this.mPivotYValue = mPivotYValue;
        this.mReverse = mReverse;
    }

    @Override
    protected void applyTransformation(float interpolatedTime, Transformation t) {
        float sx = 1.0f;
        float sy = 1.0f;
        if (mFromX != 1.0f || mToX != 1.0f) {
            sx = mReverse ? mToX + (mFromX - mToX) * interpolatedTime : mFromX + (mToX - mFromX) * interpolatedTime;
        }
        if (mFromY != 1.0f || mToY != 1.0f) {
            sy = mReverse ? mToY + (mFromY - mToY) * interpolatedTime : mFromY + (mToY - mFromY) * interpolatedTime;
        }


        if (mPivotX == 0 && mPivotY == 0) {
            t.getMatrix().setScale(sx, sy);
        } else {
            t.getMatrix().setScale(sx, sy, mPivotX, mPivotY);
        }
    }

    @Override
    public void initialize(int width, int height, int parentWidth, int parentHeight) {
        super.initialize(width, height, parentWidth, parentHeight);
        mPivotX = resolvePivotX(mPivotXValue, parentWidth, width);
        mPivotY = resolvePivoY(mPivotYValue, parentHeight, height);
    }

    private float resolvePivotX(float margingLeft, int parentWidth, int width) {
        return (margingLeft * parentWidth) / (parentWidth - width);
    }

    private float resolvePivoY(float marginTop, int parentHeight, int height) {

        return (marginTop * parentHeight) / (parentHeight - height);
    }
    public void reverse() {
        mReverse = !mReverse;
    }

    public boolean getMReverse() {
        return mReverse;
    }
}
