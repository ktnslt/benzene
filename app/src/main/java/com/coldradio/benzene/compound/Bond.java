package com.coldradio.benzene.compound;

public class Bond {
    public enum BondType {
        NONE, SINGLE, DOUBLE, TRIPLE
    }

    Atom mAtom;
    BondType mBondType;

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
        return mAtom instanceof Carbon;
    }
}
