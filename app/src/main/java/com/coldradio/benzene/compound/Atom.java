package com.coldradio.benzene.compound;

import android.graphics.PointF;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

public class Atom {
    private PointF mPoint = new PointF();
    private List<Bond> mBonds = new ArrayList<>();

    private void setBondOnlyForMe(Atom atom, Bond.BondType bondType) {
        switch(getBondType(atom)) {
            case NONE:
                mBonds.add(new Bond(atom, bondType));
                break;
            default:
                for (Bond bond : mBonds) {
                    if (bond.hasBondTo(atom)) {
                        bond.setBondType(bondType);
                        break;
                    }
                    break;
                }
        }
    }

    public void singleBond(Atom bondTo) {
        switch(getBondType(bondTo)) {
            case NONE:
                mBonds.add(new Bond(bondTo, Bond.BondType.SINGLE));
                bondTo.setBondOnlyForMe(this, Bond.BondType.SINGLE);
                break;
            case DOUBLE:
            case TRIPLE:
                setBondOnlyForMe(bondTo, Bond.BondType.SINGLE);
                bondTo.setBondOnlyForMe(this, Bond.BondType.SINGLE);
                break;
        }
    }

    public void doubleBond(Atom bondTo) {
        switch(getBondType(bondTo)) {
            case NONE:
                mBonds.add(new Bond(bondTo, Bond.BondType.DOUBLE));
                bondTo.setBondOnlyForMe(this, Bond.BondType.DOUBLE);
                break;
            case SINGLE:
            case TRIPLE:
                setBondOnlyForMe(bondTo, Bond.BondType.DOUBLE);
                bondTo.setBondOnlyForMe(this, Bond.BondType.DOUBLE);
                break;
        }
    }

    public void tripleBond(Atom bondTo) {
        switch(getBondType(bondTo)) {
            case NONE:
                mBonds.add(new Bond(bondTo, Bond.BondType.TRIPLE));
                bondTo.setBondOnlyForMe(this, Bond.BondType.TRIPLE);
                break;
            case SINGLE:
            case DOUBLE:
                setBondOnlyForMe(bondTo, Bond.BondType.TRIPLE);
                bondTo.setBondOnlyForMe(this, Bond.BondType.TRIPLE);
                break;
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

    public List<Bond> getBonds() {
        return Collections.unmodifiableList(mBonds);
    }

    public int carbonBoundNumber() {
        int boundCarbons = 0;

        for (Bond bond : mBonds) {
            if(bond.isCarbonBond()) {
                ++boundCarbons;
            }
        }
        return boundCarbons;
    }
}
