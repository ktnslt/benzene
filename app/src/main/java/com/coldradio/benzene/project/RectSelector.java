package com.coldradio.benzene.project;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PointF;
import android.view.MotionEvent;

import com.coldradio.benzene.lib.Geometry;

public class RectSelector implements IRegionSelector {
    enum MarkerIndex {
        LEFT_TOP, RIGHT_TOP, LEFT_BOTTOM, RIGHT_BOTTOM
    }

    private PointF[] mMarkers = new PointF[4];
    private Paint mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private int mSelectedMarkerIndex = -1;
    private PointF mTouchDown = new PointF();

    private void drawVertexMarker(Canvas canvas) {
        int CIRCLE_RADIUS = 20;

        for (int ii = 0; ii < mMarkers.length; ++ii) {
            canvas.drawCircle(mMarkers[ii].x, mMarkers[ii].y, CIRCLE_RADIUS, mPaint);
        }
    }

    public RectSelector(PointF point) {
        mPaint.setColor(Color.argb(80, 0, 100, 255));
        mPaint.setStyle(Paint.Style.FILL);

        for (int ii = 0; ii < mMarkers.length; ++ii) {
            mMarkers[ii] = new PointF();
        }

        mMarkers[MarkerIndex.LEFT_TOP.ordinal()].set(point);
        mMarkers[MarkerIndex.RIGHT_TOP.ordinal()].set(point.x + Configuration.INITIAL_REGION_SIZE, point.y);
        mMarkers[MarkerIndex.LEFT_BOTTOM.ordinal()].set(point.x, point.y + Configuration.INITIAL_REGION_SIZE);
        mMarkers[MarkerIndex.RIGHT_BOTTOM.ordinal()].set(point.x + Configuration.INITIAL_REGION_SIZE, point.y + Configuration.INITIAL_REGION_SIZE);
    }

    @Override
    public void draw(Canvas canvas) {
        canvas.drawRect(mMarkers[MarkerIndex.LEFT_TOP.ordinal()].x, mMarkers[MarkerIndex.LEFT_TOP.ordinal()].y,
                mMarkers[MarkerIndex.RIGHT_BOTTOM.ordinal()].x, mMarkers[MarkerIndex.RIGHT_BOTTOM.ordinal()].y, mPaint);
        drawVertexMarker(canvas);
    }

    @Override
    public boolean onTouchEvent(PointF point, int touchAction) {
        if (touchAction == MotionEvent.ACTION_DOWN) {
            for (int ii = 0; ii < mMarkers.length; ++ii) {
                if (Geometry.distanceFromPointToPoint(mMarkers[ii], point) < Configuration.SELECT_RANGE) {
                    mSelectedMarkerIndex = ii;
                    mTouchDown.set(point);
                    break;
                }
            }
        } else if (touchAction == MotionEvent.ACTION_UP) {
            mSelectedMarkerIndex = -1;
        } else if (touchAction == MotionEvent.ACTION_MOVE && mSelectedMarkerIndex >= 0) {
            mMarkers[mSelectedMarkerIndex].set(point);
            switch (MarkerIndex.values()[mSelectedMarkerIndex]) {
                case LEFT_TOP:
                    mMarkers[MarkerIndex.RIGHT_TOP.ordinal()].y = mMarkers[mSelectedMarkerIndex].y;
                    mMarkers[MarkerIndex.LEFT_BOTTOM.ordinal()].x = mMarkers[mSelectedMarkerIndex].x;
                    break;
                case RIGHT_TOP:
                    mMarkers[MarkerIndex.LEFT_TOP.ordinal()].y = mMarkers[mSelectedMarkerIndex].y;
                    mMarkers[MarkerIndex.RIGHT_BOTTOM.ordinal()].x = mMarkers[mSelectedMarkerIndex].x;
                    break;
                case LEFT_BOTTOM:
                    mMarkers[MarkerIndex.RIGHT_BOTTOM.ordinal()].y = mMarkers[mSelectedMarkerIndex].y;
                    mMarkers[MarkerIndex.LEFT_TOP.ordinal()].x = mMarkers[mSelectedMarkerIndex].x;
                    break;
                case RIGHT_BOTTOM:
                    mMarkers[MarkerIndex.LEFT_BOTTOM.ordinal()].y = mMarkers[mSelectedMarkerIndex].y;
                    mMarkers[MarkerIndex.RIGHT_TOP.ordinal()].x = mMarkers[mSelectedMarkerIndex].x;
                    break;
            }
        } else {
            return false;
        }

        return true;
    }
}