package com.coldradio.benzene.project;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PointF;
import android.graphics.RectF;
import android.view.MotionEvent;

import com.coldradio.benzene.lib.Geometry;

public class RectSelector implements IRegionSelector {
    enum MarkerIndex {
        LEFT_TOP, RIGHT_TOP, LEFT_BOTTOM, RIGHT_BOTTOM, WHOLE_RECT, NONE_TOUCHED_DOWN
    }

    private PointF[] mMarkers = new PointF[4];
    private Paint mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private MarkerIndex mSelectedMarkerIndex = MarkerIndex.NONE_TOUCHED_DOWN;
    private PointF mPrevPoint = new PointF();

    private void drawVertexMarker(Canvas canvas) {
        int CIRCLE_RADIUS = 20;

        for (int ii = 0; ii < mMarkers.length; ++ii) {
            canvas.drawCircle(mMarkers[ii].x, mMarkers[ii].y, CIRCLE_RADIUS, mPaint);
        }
    }

    private RectF selectedRegionAsRect() {
        int lt_ii = MarkerIndex.LEFT_TOP.ordinal(), rb_ii = MarkerIndex.RIGHT_BOTTOM.ordinal();

        return new RectF(mMarkers[lt_ii].x, mMarkers[lt_ii].y, mMarkers[rb_ii].x, mMarkers[rb_ii].y);
    }

    private MarkerIndex selectMarker(PointF point) {
        for (int ii = 0; ii < mMarkers.length; ++ii) {
            if (Geometry.distanceFromPointToPoint(mMarkers[ii], point) < Configuration.SELECT_RANGE) {
                return MarkerIndex.values()[ii];
            }
        }

        return MarkerIndex.NONE_TOUCHED_DOWN;
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
            mSelectedMarkerIndex = selectMarker(point);

            if (mSelectedMarkerIndex == MarkerIndex.NONE_TOUCHED_DOWN && selectedRegionAsRect().contains(point.x, point.y)) {
                mSelectedMarkerIndex = MarkerIndex.WHOLE_RECT;
            }
            mPrevPoint.set(point);
        } else if (touchAction == MotionEvent.ACTION_UP) {
            mSelectedMarkerIndex = MarkerIndex.NONE_TOUCHED_DOWN;
        } else if (touchAction == MotionEvent.ACTION_MOVE && mSelectedMarkerIndex == MarkerIndex.WHOLE_RECT) {
            for (PointF marker : mMarkers) {
                marker.offset(point.x - mPrevPoint.x, point.y - mPrevPoint.y);
            }
            mPrevPoint.set(point);
        } else if (touchAction == MotionEvent.ACTION_MOVE && mSelectedMarkerIndex != MarkerIndex.NONE_TOUCHED_DOWN) {
            int markerIndex = mSelectedMarkerIndex.ordinal();

            mMarkers[markerIndex].set(point);
            switch (mSelectedMarkerIndex) {
                case LEFT_TOP:
                    mMarkers[MarkerIndex.RIGHT_TOP.ordinal()].y = mMarkers[markerIndex].y;
                    mMarkers[MarkerIndex.LEFT_BOTTOM.ordinal()].x = mMarkers[markerIndex].x;
                    break;
                case RIGHT_TOP:
                    mMarkers[MarkerIndex.LEFT_TOP.ordinal()].y = mMarkers[markerIndex].y;
                    mMarkers[MarkerIndex.RIGHT_BOTTOM.ordinal()].x = mMarkers[markerIndex].x;
                    break;
                case LEFT_BOTTOM:
                    mMarkers[MarkerIndex.RIGHT_BOTTOM.ordinal()].y = mMarkers[markerIndex].y;
                    mMarkers[MarkerIndex.LEFT_TOP.ordinal()].x = mMarkers[markerIndex].x;
                    break;
                case RIGHT_BOTTOM:
                    mMarkers[MarkerIndex.LEFT_BOTTOM.ordinal()].y = mMarkers[markerIndex].y;
                    mMarkers[MarkerIndex.RIGHT_TOP.ordinal()].x = mMarkers[markerIndex].x;
                    break;
            }
        } else {
            return false;
        }

        return true;
    }

    @Override
    public boolean contains(PointF point) {
        return selectedRegionAsRect().contains(point.x, point.y);
    }
}