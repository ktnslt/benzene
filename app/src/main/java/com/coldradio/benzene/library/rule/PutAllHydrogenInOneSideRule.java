package com.coldradio.benzene.library.rule;

import com.coldradio.benzene.compound.Atom;
import com.coldradio.benzene.compound.Compound;
import com.coldradio.benzene.compound.CompoundArranger;

public class PutAllHydrogenInOneSideRule implements ICompoundRule {
    @Override
    public Compound apply(Compound compound) {
        for (Atom atom : compound.getAtoms()) {
            CompoundArranger.putAllHydrogenOppositeToSkeleton(atom);
        }
        return compound;
    }
}
