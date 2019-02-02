package com.coldradio.benzene.compound;

import java.util.BitSet;

public class AtomCondition {
    private AtomicNumber mAtomicNumber;
    private int mCharge;
    private BitSet mFieldMask = new BitSet();
    private enum CondMask {ATOMIC_NUMBER_MASK, CHARGE_MASK, HAS_HYDROGEN_MASK};

    public boolean satisfiedBy(Atom atom) {
        if (mFieldMask.get(CondMask.ATOMIC_NUMBER_MASK.ordinal()) && atom.getAtomicNumber() != mAtomicNumber)
            return false;
        if (mFieldMask.get(CondMask.CHARGE_MASK.ordinal()) && atom.getAtomDecoration().getCharge() != mCharge)
            return false;
        if (mFieldMask.get(CondMask.HAS_HYDROGEN_MASK.ordinal()) && CompoundInspector.numberOfHydrogen(atom) == 0)
            return false;

        return true;
    }

    public AtomCondition atomicNumber(AtomicNumber an) {
        mAtomicNumber = an;
        mFieldMask.set(CondMask.ATOMIC_NUMBER_MASK.ordinal());
        return this;
    }

    public AtomCondition charge(int c) {
        mCharge = c;
        mFieldMask.set(CondMask.CHARGE_MASK.ordinal());
        return this;
    }

    public AtomCondition hasHydrogen() {
        mFieldMask.set(CondMask.HAS_HYDROGEN_MASK.ordinal());
        return this;
    }
}
