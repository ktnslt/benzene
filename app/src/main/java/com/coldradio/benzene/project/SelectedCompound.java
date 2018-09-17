package com.coldradio.benzene.project;

import android.graphics.PointF;

import com.coldradio.benzene.compound.Compound;
import com.coldradio.benzene.geometry.Geometry;

public class SelectedCompound {
    private Compound mCompound;
    final private PointF mInitialRotationPivotPoint;
    private PointF mRotationPivotPoint = new PointF();

    public SelectedCompound(Compound compound) {
        mCompound = compound;
        mRotationPivotPoint.set(compound.centerOfRectangle());
        mRotationPivotPoint.offset(0, -200);
        mInitialRotationPivotPoint = new PointF(mRotationPivotPoint.x, mRotationPivotPoint.y);
    }

    public PointF getRotationPivotPoint() {
        mRotationPivotPoint.set(mCompound.centerOfRectangle());
        mRotationPivotPoint.offset(0, -200);

        return mRotationPivotPoint;
    }

    public Compound getCompound() {
        return mCompound;
    }

    public boolean rotate(PointF point) {
        if (isPivotGrasped(point)) {
            PointF center = mCompound.centerOfRectangle();
            float rotationDegree = Geometry.degreeOfTriangle(mInitialRotationPivotPoint, point, center);

            mCompound.rotate(rotationDegree);
            mRotationPivotPoint = Geometry.rotatePointByDegree(mRotationPivotPoint, center, rotationDegree);

            return true;
        } else {
            return false;
        }
    }

    public boolean isPivotGrasped(PointF point) {
        return Geometry.distanceFromPointToPoint(mRotationPivotPoint, point) < Configuration.ROTATION_PIVOT_SIZE;
    }
}
