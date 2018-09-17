package com.coldradio.benzene.project;

import android.graphics.PointF;

import com.coldradio.benzene.compound.Compound;
import com.coldradio.benzene.geometry.Geometry;

public class SelectedCompound {
    private Compound mCompound;
    private PointF mRotationPivotPoint = new PointF();

    public SelectedCompound(Compound compound) {
        mCompound = compound;
        mRotationPivotPoint.set(0, -200);
    }

    public PointF getRotationPivotPoint() {
        PointF pivot = new PointF(mRotationPivotPoint.x, mRotationPivotPoint.y);
        PointF center = Geometry.centerOfCompound(mCompound);

        pivot.offset(center.x, center.y);
        return pivot;
    }

    public Compound getCompound() {
        return mCompound;
    }

    public boolean isPivotGrasped() {
        return false;
    }
}
