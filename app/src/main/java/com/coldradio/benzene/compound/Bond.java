package com.coldradio.benzene.compound;

public class Bond {
    public enum BondType {
        NONE, SINGLE, DOUBLE, DOUBLE_OTHER_SIDE, DOUBLE_MIDDLE, TRIPLE
    }
    public enum BondAnnotation {
        NONE, WEDGE_UP, WEDGE_DOWN, WAVY
    }

    private transient Atom mAtom;
    private int mAIDForAtom;    // this is hack for resolving the circular reference caused by the mAtom during serialization.
    private BondType mBondType;
    private BondAnnotation mBondAnnotation = BondAnnotation.NONE;

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
        mBondType = BondType.values()[(mBondType.ordinal() + 1) % BondType.values().length];
        if (mBondType == BondType.NONE)
            mBondType = BondType.SINGLE;
    }

    public void preSerialization() {
        mAIDForAtom = mAtom.getAID();
    }

    public void postDeserialization(Compound compound) {
        mAtom = compound.getAtom(mAIDForAtom);
    }

    public BondAnnotation getBondAnnotation() {
        return mBondAnnotation;
    }

    public void setBondAnnotation(BondAnnotation bondAnnotation) {
        mBondAnnotation = bondAnnotation;
    }
}
