package com.coldradio.benzene.compound;

import com.coldradio.benzene.geometry.Geometry;

import java.util.ArrayList;
import java.util.List;

public class Compound {
    protected Atom mHeadAtom;
    protected Geometry mGeometry = new Geometry();
    boolean mSelected = false;

    public Compound() {

    }

    public Geometry getGeometry() {
        return mGeometry;
    }

    public boolean isSelected() {
        return mSelected;
    }

    public boolean select(float x, float y) {
        return mSelected = mGeometry.select(x, y);
    }
}
