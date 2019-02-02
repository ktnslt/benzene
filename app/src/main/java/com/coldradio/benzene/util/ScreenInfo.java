package com.coldradio.benzene.util;

import android.graphics.PointF;
import android.graphics.RectF;

public class ScreenInfo {
    private RectF mRegion = new RectF();
    private PointF mCenterPoint = new PointF();
    private static ScreenInfo smInstance = new ScreenInfo();

    public static ScreenInfo instance() {
        return smInstance;
    }

    public void setScreen(int x, int y, int w, int h) {
        mRegion.set(x, y, x + w, y + h);
        mCenterPoint.set(mRegion.centerX(), mRegion.centerY());
    }

    public PointF centerPoint() {
        return mCenterPoint;
    }

    public RectF region() {
        return mRegion;
    }

    public int screenWidth() {
        return (int) mRegion.width();
    }

    public int screenHeight() {
        return (int) mRegion.height();
    }
}
