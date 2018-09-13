package com.coldradio.benzene.view;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PointF;

import com.coldradio.benzene.project.Configuration;

public class RegionSelector {
    private PointF mLeftTop = new PointF(), mRightBottom = new PointF();
    private boolean mSelecting = true;  // TODO: default to false
    private Paint mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);

    public RegionSelector() {
        mPaint.setColor(Color.argb(80, 0, 100, 255));
        mPaint.setStyle(Paint.Style.FILL);
    }

    public void initiate(float x, float y) {
        mLeftTop.set(x, y);
        mRightBottom.set(x + Configuration.INITIAL_REGION_SIZE, y + Configuration.INITIAL_REGION_SIZE);
        mSelecting = true;
    }

    public void draw(Canvas canvas) {
        if (mSelecting) {
            canvas.drawRect(mLeftTop.x, mLeftTop.y, mRightBottom.x, mRightBottom.y, mPaint);
        }
    }
}
