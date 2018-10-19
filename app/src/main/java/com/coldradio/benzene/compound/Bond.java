package com.coldradio.benzene.compound;

public class Bond {
    public enum BondType {
        // TODO consider DOUBLE, DOUBLE_OTHER_SIDE, that differs in the drawing. Then the mNextDoubleBond can be deleted
        NONE, SINGLE, DOUBLE, TRIPLE
    }

    private Atom mAtom;
    private BondType mBondType;
    private boolean mNextDoubleBond = false;

    public Bond(Atom bondTo, BondType bondType) {
        mAtom = bondTo;
        mBondType = bondType;
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

    public void cycleBond() {
        switch (mBondType) {
            case SINGLE:
                mBondType = BondType.DOUBLE;
                break;
            case DOUBLE:
                if (mNextDoubleBond) {
                    mBondType = BondType.TRIPLE;
                }
                mNextDoubleBond = !mNextDoubleBond;
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
