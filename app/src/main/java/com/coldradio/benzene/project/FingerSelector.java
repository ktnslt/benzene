package com.coldradio.benzene.project;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PointF;
import android.view.MotionEvent;

import com.coldradio.benzene.compound.Atom;
import com.coldradio.benzene.compound.Compound;
import com.coldradio.benzene.util.Geometry;

import java.util.ArrayList;
import java.util.List;

public class FingerSelector implements IRegionSelector {
    private List<PointF> mFingerPath = new ArrayList<>();
    private List<Atom> mSelectedAtoms = new ArrayList<>();
    private boolean mCancelSelector = false;

    private void updateSelectedAtoms(PointF l1, PointF l2) {
        for (Compound compound : Project.instance().getCompounds()) {
            for (Atom atom : compound.getAtoms()) {
                float distance = (l1.equals(l2) ? Geometry.distanceFromPointToPoint(atom.getPoint(), l1) : Geometry.distanceFromPointToLineSegment(atom.getPoint(), l1, l2));

                if (! mSelectedAtoms.contains(atom) && atom.isVisible() && distance < Configuration.SELECT_RANGE) {
                    mSelectedAtoms.add(atom);
                }
            }
        }
    }

    private void addToPath(PointF point) {
        if (mFingerPath.size() > 0) {
            updateSelectedAtoms(mFingerPath.get(mFingerPath.size() - 1), point);
        } else {
            updateSelectedAtoms(point, point);
        }
        // added as new. the passed point is reused, and the reference will not be changed at all
        mFingerPath.add(new PointF(point.x, point.y));
    }

    @Override
    public void draw(Canvas canvas, Paint paint) {
        Paint.Style origStyle = paint.getStyle();
        float origStrokeWidth = paint.getStrokeWidth();

        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(Configuration.SELECT_RANGE * 2);

        for (int ii = 0; ii < mFingerPath.size() - 1; ++ii) {
            PointF p1 = mFingerPath.get(ii), p2 = mFingerPath.get(ii + 1);

            canvas.drawLine(p1.x, p1.y, p2.x, p2.y, paint);
        }

        paint.setStyle(origStyle);
        paint.setStrokeWidth(origStrokeWidth);
    }

    @Override
    public boolean onTouchEvent(PointF point, int touchAction) {
        if (touchAction == MotionEvent.ACTION_DOWN) {
            if (mFingerPath.isEmpty()) {
                addToPath(point);
            } else {
                // shall return false. if return true, this ACTION_DOWN event will not go to GestureDetector.
                // Then, the GestureDetector scroll the screen without DOWN event only with MOVE event
                mCancelSelector = true; // this will delete this object, hence no need to reset it to false
                return false;
            }
        } else if (touchAction == MotionEvent.ACTION_MOVE) {
            if (Geometry.distanceFromPointToPoint(point, mFingerPath.get(mFingerPath.size() - 1)) > 20) {
                addToPath(point);
            }
        } else if (touchAction == MotionEvent.ACTION_UP) {
            // empty body. if this case returns false, UP will be handled by others, and ElementSelector will be reset
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
        return mCancelSelector;
    }
}
