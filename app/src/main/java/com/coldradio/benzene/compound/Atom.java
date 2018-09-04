package com.coldradio.benzene.compound;

import android.graphics.PointF;
import java.util.ArrayList;
import java.util.List;

public class Atom {
    private PointF mPoint = new PointF();
    private List<Bond> mBonds = new ArrayList<>();

    public void doubleBond(Atom bondTo) {
        mBonds.add(new Bond(bondTo, Bond.BondType.DOUBLE));
    }
    public void singleBond(Atom bondTo) {
        mBonds.add(new Bond(bondTo, Bond.BondType.SINGLE));
    }
    public void setPoint(PointF point) {
        mPoint.set(point);
    }
    public PointF getPoint() {
        return mPoint;
    }
    public Bond.BondType bondTo(Atom atom) {
        for(Bond bond : mBonds) {
            Bond.BondType bondType = bond.hasBondTo(atom);

            if(bondType != Bond.BondType.NONE) {
                return bondType;
            }
        }
        return Bond.BondType.NONE;
    }
}
