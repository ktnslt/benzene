package com.coldradio.benzene.util;

import android.graphics.PointF;

public class ScreenInfo {
    private int mScreenWidth;
    private int mScreenHeight;
    private int mVisualX;
    private int mVisualY;
    private PointF mCenterPoint = new PointF();
    private static ScreenInfo smInstance = new ScreenInfo();

    private void setScreen(int left, int top, int width, int height) {
        mVisualX = left;
        mVisualY = top;
        mScreenWidth = width;
        mScreenHeight = height;
        mCenterPoint.set(mVisualX + mScreenWidth / 2, mVisualY + mScreenHeight / 2);
    }

    public static ScreenInfo instance() {
        return smInstance;
    }

    public void setScreenSize(int width, int height) {
        setScreen(mVisualX, mVisualY, width, height);
    }

    public void setScreenXY(int x, int y) {
        setScreen(x, y, mScreenWidth, mScreenHeight);
    }

    public void offset(float dx, float dy) {
        setScreen(mVisualX + (int) dx, mVisualY + (int) dy, mScreenWidth, mScreenHeight);
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
