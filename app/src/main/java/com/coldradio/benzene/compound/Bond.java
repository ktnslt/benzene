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
    public BondType hasBondTo(Atom bondTo) {
        if(mAtom == bondTo) {
            return mBondType;
        }
        else {
            return BondType.NONE;
        }
    }
}
