package com.coldradio.benzene.util;

import android.graphics.PointF;

public class ScreenInfo {
    private int mScreenWidth;
    private int mScreenHeight;
    private int mVisualX;
    private int mVisualY;
    private PointF mCenterPoint = new PointF();
    private static ScreenInfo smInstance = new ScreenInfo();

    public static ScreenInfo instance() {
        return smInstance;
    }

    public void setScreen(int x, int y, int w, int h) {
        mVisualX = x;
        mVisualY = y;
        mScreenWidth = w;
        mScreenHeight = h;
        mCenterPoint.set(mVisualX + mScreenWidth / 2, mVisualY + mScreenHeight / 2);
    }

    public PointF centerPoint() {
        return mCenterPoint;
    }

    public int screenWidth() {
        return mScreenWidth;
    }

    public int screenHeight() {
        return mScreenHeight;
    }
}
