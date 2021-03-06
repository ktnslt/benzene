package com.coldradio.benzene.project;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PointF;
import android.graphics.RectF;
import android.view.MotionEvent;

import com.coldradio.benzene.compound.Atom;
import com.coldradio.benzene.compound.Compound;
import com.coldradio.benzene.util.Geometry;
import com.coldradio.benzene.util.ScreenInfo;

import java.util.ArrayList;
import java.util.List;

public class RectSelector implements IRegionSelector {
    enum MarkerIndex {
        LEFT_TOP, RIGHT_TOP, LEFT_BOTTOM, RIGHT_BOTTOM, WHOLE_RECT, NONE
    }

    private PointF[] mMarkers = new PointF[4];
    private RectF mRegionRect = new RectF();    // this is used for drawing rect and testing 'contains()'
    private MarkerIndex mSelectedMarkerIndex = MarkerIndex.NONE;
    private PointF mInitTouchPoint = new PointF();
    private List<Atom> mSelectedAtoms = new ArrayList<>();

    private void updateRegionRect() {
        mRegionRect.left = Math.min(Math.min(mMarkers[0].x, mMarkers[1].x), Math.min(mMarkers[2].x, mMarkers[3].x));
        mRegionRect.right = Math.max(Math.max(mMarkers[0].x, mMarkers[1].x), Math.max(mMarkers[2].x, mMarkers[3].x));
        mRegionRect.top = Math.min(Math.min(mMarkers[0].y, mMarkers[1].y), Math.min(mMarkers[2].y, mMarkers[3].y));
        mRegionRect.bottom = Math.max(Math.max(mMarkers[0].y, mMarkers[1].y), Math.max(mMarkers[2].y, mMarkers[3].y));

        // update Selected Atoms
        mSelectedAtoms.clear();
        for (Compound compound : Project.instance().getCompounds()) {
            for (Atom atom : compound.getAtoms()) {
                if (atom.isVisible() && mRegionRect.contains(atom.getPoint().x, atom.getPoint().y)) {
                    mSelectedAtoms.add(atom);
                }
            }
        }
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

        if (mRegionRect.contains(point.x, point.y)) {
            return MarkerIndex.WHOLE_RECT;
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
        updateRegionRect();
    }

    @Override
    public void draw(Canvas canvas, Paint paint) {
        Paint.Style origStyle = paint.getStyle();

        paint.setStyle(Paint.Style.STROKE);
        canvas.drawRect(mRegionRect, paint);
        paint.setStyle(origStyle);

        drawVertexMarker(canvas, paint);
    }

    @Override
    public boolean onTouchEvent(PointF point, int touchAction) {
        if (touchAction == MotionEvent.ACTION_DOWN
                && (mSelectedMarkerIndex = selectMarker(point)) != MarkerIndex.NONE) {
            if (mSelectedMarkerIndex == MarkerIndex.WHOLE_RECT) {
                mInitTouchPoint.set(point);
            }
        } else if (touchAction == MotionEvent.ACTION_UP) {
            mSelectedMarkerIndex = MarkerIndex.NONE;
        } else if (touchAction == MotionEvent.ACTION_MOVE && mSelectedMarkerIndex == MarkerIndex.WHOLE_RECT) {
            for (PointF marker : mMarkers) {
                marker.offset(point.x - mInitTouchPoint.x, point.y - mInitTouchPoint.y);
            }
            mRegionRect.offset(point.x - mInitTouchPoint.x, point.y - mInitTouchPoint.y);
            mInitTouchPoint.set(point);
            updateRegionRect();
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
            updateRegionRect();
        } else {
            return false;
        }
        return true;
    }

    @Override
    public List<Atom> getSelectedAtoms() {
        return mSelectedAtoms;
    }

    @Override
    public boolean canceled() {
        return false;
    }
}