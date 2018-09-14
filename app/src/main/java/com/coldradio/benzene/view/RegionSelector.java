package com.coldradio.benzene.view;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PointF;

import com.coldradio.benzene.project.Configuration;

public class RegionSelector {
    private PointF mLeftTop = new PointF(), mRightBottom = new PointF();
    private boolean mSelecting = false;
    private Paint mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);

    private void drawVertexMarker(Canvas canvas) {
        int CIRCLE_RADIUS = 20;

        canvas.drawCircle(mLeftTop.x, mLeftTop.y, CIRCLE_RADIUS, mPaint);
        canvas.drawCircle(mRightBottom.x, mLeftTop.y, CIRCLE_RADIUS, mPaint);
        canvas.drawCircle(mLeftTop.x, mRightBottom.y, CIRCLE_RADIUS, mPaint);
        canvas.drawCircle(mRightBottom.x, mRightBottom.y, CIRCLE_RADIUS, mPaint);
    }

    public RegionSelector() {
        mPaint.setColor(Color.argb(80, 0, 100, 255));
        mPaint.setStyle(Paint.Style.FILL);
    }

    public void initiate(PointF point) {
        mLeftTop.set(point);
        mRightBottom.set(point.x + Configuration.INITIAL_REGION_SIZE, point.y + Configuration.INITIAL_REGION_SIZE);
        mSelecting = true;
    }

    public void draw(Canvas canvas) {
        if (mSelecting) {
            canvas.drawRect(mLeftTop.x, mLeftTop.y, mRightBottom.x, mRightBottom.y, mPaint);
            drawVertexMarker(canvas);
        }
    }
}
