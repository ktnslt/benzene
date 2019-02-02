package com.coldradio.benzene.compound.funcgroup;

import com.coldradio.benzene.compound.Atom;
import com.coldradio.benzene.compound.CompoundInspector;
import com.coldradio.benzene.compound.CompoundReactor;

public class Phenyl_FG extends CycHexyl_FG {
    public Phenyl_FG(Atom a_atom) {
        super(a_atom);

        Atom[] c = CompoundInspector.extractSkeletonChain(super.appendAtom(), 6);

        CompoundReactor.alkaneToConjugated(super.getCompound(), c, 0);
    }

    @Override
    public String getName() {
        return "phenyl";
    }
}
