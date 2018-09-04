package com.coldradio.benzene.compound;

public class Bond {
    public enum BondType {
        SINGLE_BOND, DOUBLE_BOND, TRIPLE_BOND
    }
    Atom mAtom;
    BondType mBondType;

    public Bond(Atom bondTo, BondType bondType) {
        mAtom = bondTo;
        mBondType = bondType;
    }
}
