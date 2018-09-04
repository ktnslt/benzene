package com.coldradio.benzene.compound;

public class Bond {
    public enum BondType {
        NO_BOND, SINGLE_BOND, DOUBLE_BOND, TRIPLE_BOND
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
            return BondType.NO_BOND;
        }
    }
}
