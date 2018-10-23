package com.coldradio.benzene.compound;

public class Bond {
    public enum BondType {
        NONE, SINGLE, DOUBLE, DOUBLE_OTHER_SIDE, DOUBLE_MIDDLE, TRIPLE
    }

    private Atom mAtom;
    private BondType mBondType;

    public Bond(Atom bondTo, BondType bondType) {
        mAtom = bondTo;
        mBondType = bondType;
    }

    public BondType getBondType() {
        if (mBondType == BondType.DOUBLE_MIDDLE || mBondType == BondType.DOUBLE_OTHER_SIDE) {
            return BondType.DOUBLE;
        } else {
            return mBondType;
        }
    }

    public BondType getDetailedBondType() {
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

    public void cycleBond() {
        mBondType = BondType.values()[(mBondType.ordinal() + 1) % BondType.values().length];
        if (mBondType == BondType.NONE)
            mBondType = BondType.SINGLE;
    }
}
