package com.coldradio.benzene.compound;

import android.graphics.PointF;
import android.graphics.RectF;

import com.coldradio.benzene.geometry.Geometry;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

public class Compound {
    protected Atom mHeadAtom;
    protected List<Atom> mAtoms = new ArrayList<>();
    boolean mSelected = false;

    public void offset(float x, float y) {
        for(Atom atom : mAtoms) {
            atom.getPoint().offset(x, y);
        }
    }
    public void fillCarbon(int carbonNumber) {
        mAtoms.clear();
        for(int ii = 0; ii < carbonNumber; ++ii) {
            mAtoms.add(new Carbon());
        }
    }
    public boolean isSelected() {
        return mSelected;
    }
    public void setSelected(boolean selected) {
        mSelected = selected;
    }
    public boolean select(float x, float y) {
        return mSelected = Geometry.select(x, y, this);
    }
    public List<Atom> getAtoms() {
        return Collections.unmodifiableList(mAtoms);
    }
    public boolean isCyclo() {
        return mAtoms.get(0).bondTo(mAtoms.get(mAtoms.size() - 1)) != Bond.BondType.NONE;
    }
    public PointF center() {
        RectF region = rectRegion();

        return new PointF((region.left + region.right) / 2, (region.top + region.bottom) / 2);
    }
    public RectF rectRegion() {
        float left = (float)10e10, top = (float)10e10, right = 0, bottom = 0;

        for(Atom atom : mAtoms) {
            PointF p = atom.getPoint();

            left = Math.min(left, p.x);
            top = Math.min(top, p.y);
            right = Math.max(right, p.x);
            bottom = Math.max(bottom, p.y);
        }

        return new RectF(left, top, right, bottom);
    }
}
