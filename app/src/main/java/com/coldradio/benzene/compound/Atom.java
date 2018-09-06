package com.coldradio.benzene.compound;

import android.graphics.PointF;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class Atom {
    private PointF mPoint = new PointF();
    private List<Bond> mBonds = new ArrayList<>();

    public void doubleBond(Atom bondTo) {
        if (getBondType(bondTo) == Bond.BondType.NONE) {
            mBonds.add(new Bond(bondTo, Bond.BondType.DOUBLE));
            bondTo.doubleBond(this);
        }
    }

    public void singleBond(Atom bondTo) {
        if (getBondType(bondTo) == Bond.BondType.NONE) {
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

    public Bond.BondType getBondType(Atom atom) {
        for (Bond bond : mBonds) {
            Bond.BondType bondType = bond.getBondType(atom);

            if (bondType != Bond.BondType.NONE) {
                return bondType;
            }
        }
        return Bond.BondType.NONE;
    }

    public boolean cutBond(Atom atom) {
        for (Iterator<Bond> it = mBonds.iterator(); it.hasNext(); ) {
            Bond bond = it.next();

            if (bond.hasBondTo(atom)) {
                it.remove();
                atom.cutBond(this);
                return true;
            }
        }
        return false;
    }

    public void setBond(Atom atom, Bond.BondType bondType) {
        for (Bond bond : mBonds) {
            if(bond.hasBondTo(atom)) {
                bond.setBondType(bondType);
                break;
            }
        }
    }

    public Atom getBoundAtomExcept(Atom atom) {
        if (mBonds.size() == 2) {
            for (Bond bond : mBonds) {
                if (bond.getBoundAtom() != atom) {
                    return bond.getBoundAtom();
                }
            }
        }
        return null;
    }
}
