package com.coldradio.benzene.project;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PointF;
import android.view.MotionEvent;

import com.coldradio.benzene.util.Geometry;
import com.coldradio.benzene.util.ScreenInfo;

public class RectSelector implements IRegionSelector {
    enum MarkerIndex {
        LEFT_TOP, RIGHT_TOP, LEFT_BOTTOM, RIGHT_BOTTOM, NONE
    }

    private PointF[] mMarkers = new PointF[4];
    private MarkerIndex mSelectedMarkerIndex = MarkerIndex.NONE;

    private void reorderMarkerPoint() {

    }

    private void drawVertexMarker(Canvas canvas, Paint paint) {
        for (int ii = 0; ii < mMarkers.length; ++ii) {
            canvas.drawCircle(mMarkers[ii].x, mMarkers[ii].y, Configuration.SELECT_RANGE, paint);
        }
    }

    private MarkerIndex selectMarker(PointF point) {
        for (int ii = 0; ii < mMarkers.length; ++ii) {
            if (Geometry.distanceFromPointToPoint(mMarkers[ii], point) < Configuration.SELECT_RANGE) {
                return MarkerIndex.values()[ii];
            }
        }

        return MarkerIndex.NONE;
    }

    public RectSelector() {
        for (int ii = 0; ii < mMarkers.length; ++ii) {
            mMarkers[ii] = new PointF();
        }

        PointF center = ScreenInfo.instance().centerPoint();
        int shift = Configuration.INITIAL_REGION_SIZE;

        mMarkers[MarkerIndex.LEFT_TOP.ordinal()].set(center.x - shift, center.y - shift);
        mMarkers[MarkerIndex.RIGHT_TOP.ordinal()].set(center.x + shift, center.y - shift);
        mMarkers[MarkerIndex.LEFT_BOTTOM.ordinal()].set(center.x - shift, center.y + shift);
        mMarkers[MarkerIndex.RIGHT_BOTTOM.ordinal()].set(center.x + shift, center.y + shift);
    }

    @Override
    public void draw(Canvas canvas, Paint paint) {
        Paint.Style origStyle = paint.getStyle();

        paint.setStyle(Paint.Style.STROKE);
        canvas.drawRect(mMarkers[MarkerIndex.LEFT_TOP.ordinal()].x, mMarkers[MarkerIndex.LEFT_TOP.ordinal()].y,
                mMarkers[MarkerIndex.RIGHT_BOTTOM.ordinal()].x, mMarkers[MarkerIndex.RIGHT_BOTTOM.ordinal()].y, paint);
        paint.setStyle(origStyle);

        drawVertexMarker(canvas, paint);
    }

    @Override
    public boolean onTouchEvent(PointF point, int touchAction) {
        if (touchAction == MotionEvent.ACTION_DOWN
                && (mSelectedMarkerIndex = selectMarker(point)) != MarkerIndex.NONE) {
            // empty body. just set mSelectedMarkerIndex
        } else if (touchAction == MotionEvent.ACTION_UP) {
            mSelectedMarkerIndex = MarkerIndex.NONE;
        } else if (touchAction == MotionEvent.ACTION_MOVE && mSelectedMarkerIndex != MarkerIndex.NONE) {
            int ind = mSelectedMarkerIndex.ordinal();

            mMarkers[ind].set(point);
            switch (mSelectedMarkerIndex) {
                case LEFT_TOP:
                    mMarkers[MarkerIndex.RIGHT_TOP.ordinal()].y = mMarkers[ind].y;
                    mMarkers[MarkerIndex.LEFT_BOTTOM.ordinal()].x = mMarkers[ind].x;
                    break;
                case RIGHT_TOP:
                    mMarkers[MarkerIndex.LEFT_TOP.ordinal()].y = mMarkers[ind].y;
                    mMarkers[MarkerIndex.RIGHT_BOTTOM.ordinal()].x = mMarkers[ind].x;
                    break;
                case LEFT_BOTTOM:
                    mMarkers[MarkerIndex.RIGHT_BOTTOM.ordinal()].y = mMarkers[ind].y;
                    mMarkers[MarkerIndex.LEFT_TOP.ordinal()].x = mMarkers[ind].x;
                    break;
                case RIGHT_BOTTOM:
                    mMarkers[MarkerIndex.LEFT_BOTTOM.ordinal()].y = mMarkers[ind].y;
                    mMarkers[MarkerIndex.RIGHT_TOP.ordinal()].x = mMarkers[ind].x;
                    break;
            }
        } else {
            return false;
        }

        reorderMarkerPoint();
        return true;
    }

    @Override
    public boolean contains(PointF point) {
        return mMarkers[MarkerIndex.LEFT_TOP.ordinal()].x < point.x && mMarkers[MarkerIndex.LEFT_TOP.ordinal()].y < point.y
                && point.x < mMarkers[MarkerIndex.RIGHT_BOTTOM.ordinal()].x && point.y < mMarkers[MarkerIndex.RIGHT_BOTTOM.ordinal()].y;
    }

    @Override
    public boolean move(float dx, float dy) {
        for (int ii = 0; ii < mMarkers.length; ++ii) {
            mMarkers[ii].offset(dx, dy);
        }
        return true;
    }
}