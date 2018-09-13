package com.coldradio.benzene.compound;

import android.graphics.PointF;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

public class Atom {
    private PointF mPoint = new PointF();
    private List<Bond> mBonds = new ArrayList<>();

    private Bond findBond(Atom atom) {
        for (Bond bond : mBonds) {
            if (bond.hasBondTo(atom)) {
                return bond;
            }
        }
        return null;
    }

    private void setBond(Atom atom, Bond.BondType bondType) {
        Bond bond = findBond(atom);

        if (bond == null) {
            mBonds.add(new Bond(atom, bondType));
            atom.setBond(this, bondType);
        } else if (bond.getBondType() != bondType) {
            bond.setBondType(bondType);
            atom.setBond(this, bondType);
        }
    }

    public void singleBond(Atom atom) {
        setBond(atom, Bond.BondType.SINGLE);
    }

    public void doubleBond(Atom atom) {
        setBond(atom, Bond.BondType.DOUBLE);
    }

    public void tripleBond(Atom atom) {
        setBond(atom, Bond.BondType.TRIPLE);
    }

    public void setPoint(PointF point) {
        mPoint.set(point);
    }

    public PointF getPoint() {
        return mPoint;
    }

    public Bond.BondType getBondType(Atom atom) {
        Bond bond = findBond(atom);

        if (bond != null) {
            return bond.getBondType();
        } else {
            return Bond.BondType.NONE;
        }
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
        } else {
            // TODO: this case needs to be addressed.
        }
        return null;
    }

    public List<Bond> getBonds() {
        return Collections.unmodifiableList(mBonds);
    }

    public int carbonBoundNumber() {
        int boundCarbons = 0;

        for (Bond bond : mBonds) {
            if (bond.isCarbonBond()) {
                ++boundCarbons;
            }
        }
        return boundCarbons;
    }

    public void cycleBond(Atom atom) {
        Bond bond = findBond(atom);

        if (bond != null) {
            bond.cycleBond();
        }
    }

    public boolean isNextDoubleBond(Atom atom) {
        Bond bond = findBond(atom);

        if (bond != null) {
            return bond.isNextDoubleBond();
        } else {
            return false;
        }
    }
}
