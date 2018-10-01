package com.coldradio.benzene.compound;

import com.coldradio.benzene.lib.AtomicNumber;

public class Bond {
    public enum BondType {
        NONE, SINGLE, DOUBLE, TRIPLE
    }

    private Atom mAtom;
    private BondType mBondType;
    private boolean mNextDoubleBond = false;

    public Bond(Atom bondTo, BondType bondType) {
        mAtom = bondTo;
        mBondType = bondType;
    }

    public BondType getBondType(Atom atom) {
        if (hasBondTo(atom)) {
            return mBondType;
        } else {
            return BondType.NONE;
        }
    }

    public BondType getBondType() {
        return mBondType;
    }

    public boolean hasBondTo(Atom atom) {
        return mAtom == atom;
    }

    public void setBondType(BondType bondType) {
        mBondType = bondType;
    }

    public Atom getBoundAtom() {
        return mAtom;
    }

    public boolean isCarbonBond() {
        return mAtom.getAtomicNumber() == AtomicNumber.C;
    }

    public void cycleBond() {
        switch (mBondType) {
            case SINGLE:
                mBondType = BondType.DOUBLE;
                break;
            case DOUBLE:
                if(mNextDoubleBond) {
                    mBondType = BondType.TRIPLE;
                }
                mNextDoubleBond= !mNextDoubleBond;
                break;
            case TRIPLE:
                mBondType = BondType.SINGLE;
                break;
        }
    }

    public boolean isNextDoubleBond() {
        return mNextDoubleBond;
    }
}
