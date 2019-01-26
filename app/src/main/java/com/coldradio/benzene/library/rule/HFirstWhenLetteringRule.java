package com.coldradio.benzene.library.rule;

import com.coldradio.benzene.compound.Atom;
import com.coldradio.benzene.compound.Compound;
import com.coldradio.benzene.compound.CompoundArranger;
import com.coldradio.benzene.compound.CompoundInspector;

public class HFirstWhenLetteringRule implements ICompoundRule {
    @Override
    public Compound apply(Compound compound) {
        // H2O, HCl case
        if (CompoundInspector.lessThanTwoSkeletonAtom(compound)) {
            Atom atom = CompoundInspector.anySkeletonAtom(compound);

            if (atom != null && CompoundInspector.moreHydrogensInRight(atom)) {
                CompoundArranger.flipHydrogen(atom);
            }
        }

        return compound;
    }
}
