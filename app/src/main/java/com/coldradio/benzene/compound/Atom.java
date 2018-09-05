package com.coldradio.benzene.compound;

import android.graphics.PointF;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class Atom {
    private PointF mPoint = new PointF();
    private List<Bond> mBonds = new ArrayList<>();

    public void doubleBond(Atom bondTo) {
        if(hasBond(bondTo) == Bond.BondType.NONE) {
            mBonds.add(new Bond(bondTo, Bond.BondType.DOUBLE));
            bondTo.doubleBond(this);
        }
    }
    public void singleBond(Atom bondTo) {
        if(hasBond(bondTo) == Bond.BondType.NONE) {
            mBonds.add(new Bond(bondTo, Bond.BondType.SINGLE));
            bondTo.singleBond(this);
        }
    }
    public void setPoint(PointF point) {
        mPoint.set(point);
    }
    public PointF getPoint() {
        return mPoint;
    }
    public Bond.BondType hasBond(Atom atom) {
        for(Bond bond : mBonds) {
            Bond.BondType bondType = bond.hasBondTo(atom);

            if(bondType != Bond.BondType.NONE) {
                return bondType;
            }
        }
        return Bond.BondType.NONE;
    }
    public boolean cutBond(Atom bondTo) {
        for(Iterator<Bond> it = mBonds.iterator(); it.hasNext(); ) {
            Bond bond = it.next();

            if(bond.hasBondTo(bondTo) != Bond.BondType.NONE) {
                it.remove();
                bondTo.cutBond(this);
                return true;
            }
        }
        return false;
    }
}
