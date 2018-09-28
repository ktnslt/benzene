package com.coldradio.benzene.project;

import android.graphics.PointF;

import com.coldradio.benzene.compound.Compound;
import com.coldradio.benzene.lib.Geometry;

public class SelectedCompound {
    private Compound mCompound;
    private PointF mRotationPivotPoint = new PointF();

    public SelectedCompound(Compound compound) {
        mCompound = compound;
        mRotationPivotPoint.set(compound.centerOfRectangle());
        mRotationPivotPoint.offset(0, -200);
    }

    public PointF getRotationPivotPoint() {
        return mRotationPivotPoint;
    }

    public Compound getCompound() {
        return mCompound;
    }

    public void rotateToPoint(PointF point) {
        float angle = Geometry.cwAngle(mRotationPivotPoint, point, mCompound.centerOfRectangle());

        mCompound.rotate(angle);
        mRotationPivotPoint = Geometry.rotatePoint(mRotationPivotPoint, mCompound.centerOfRectangle(), angle);
    }

    public boolean isPivotGrasped(PointF point) {
        return Geometry.distanceFromPointToPoint(mRotationPivotPoint, point) < Configuration.ROTATION_PIVOT_SIZE;
    }

    public void offset(PointF distance) {
        mRotationPivotPoint.offset(distance.x, distance.y);
        mCompound.offset(distance.x, distance.y);
    }
}