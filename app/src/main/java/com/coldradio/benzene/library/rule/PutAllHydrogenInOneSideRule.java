package com.coldradio.benzene.library.rule;

import com.coldradio.benzene.compound.Atom;
import com.coldradio.benzene.compound.Compound;
import com.coldradio.benzene.compound.CompoundArranger;
import com.coldradio.benzene.compound.CompoundInspector;

public class PutAllHydrogenInOneSideRule implements ICompoundRule {
    // need to evaluate the pros cons of PutAllHydrogenInOneSideRule
    // pros: flip H works more well. but there are exceptional cases such as NH2 (skeleton is in vertical position)
    // cons: CH3 case, the initial position would be weird.
    @Override
    public Compound apply(Compound compound) {
        for (Atom atom : compound.getAtoms()) {
            if (CompoundInspector.numberOfSkeletonAtoms(atom) == 1 && CompoundInspector.numberOfHydrogen(atom) == 1) {
                // only for OH case
                CompoundArranger.putAllHydrogenOppositeToSkeleton(atom);
            }
        }
        return compound;
    }
}
