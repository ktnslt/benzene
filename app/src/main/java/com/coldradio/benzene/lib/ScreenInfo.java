package com.coldradio.benzene.lib;

import android.graphics.PointF;

public class ScreenInfo {
    private int mScreenWidth;
    private int mScreenHeight;
    private float mVisualX;
    private float mVisualY;
    private static ScreenInfo smInstance = new ScreenInfo();

    public static ScreenInfo instance() {
        return smInstance;
    }

    public void setScreenSize(int width, int height) {
        mScreenWidth = width;
        mScreenHeight = height;
    }

    public void offset(float dx, float dy) {
        mVisualX += dx;
        mVisualY += dy;
    }

    public PointF centerPoint() {
        return new PointF(mVisualX + mScreenWidth / 2, mVisualY + mScreenHeight / 2);
    }

    public PointF leftTopPoint() {
        return new PointF(mVisualX, mVisualY);
    }

    public int screenWidth() {
        return mScreenWidth;
    }

    public int screenHeight() {
        return mScreenHeight;
    }
}
