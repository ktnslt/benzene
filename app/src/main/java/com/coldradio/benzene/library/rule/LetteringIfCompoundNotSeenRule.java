package com.coldradio.benzene.library.rule;

import com.coldradio.benzene.compound.Atom;
import com.coldradio.benzene.compound.AtomicNumber;
import com.coldradio.benzene.compound.Compound;
import com.coldradio.benzene.compound.CompoundArranger;
import com.coldradio.benzene.compound.CompoundInspector;

public class LetteringIfCompoundNotSeenRule implements ICompoundRule {
    @Override
    public Compound apply(Compound compound) {
        if (CompoundInspector.lessThanTwoSkeletonAtom(compound)) {
            for (Atom atom : compound.getAtoms()) {
                if (atom.getAtomicNumber() != AtomicNumber.H) {
                    atom.getAtomDecoration().lettering(true);
                    CompoundArranger.showAllHydrogen(atom, false);
                }
            }
        }

        return compound;
    }
}
