package com.coldradio.benzene.compound.funcgroup;

import com.coldradio.benzene.compound.Atom;
import com.coldradio.benzene.compound.CompoundInspector;
import com.coldradio.benzene.compound.CompoundReactor;

public class ConjCycPentyl_FG extends CycPentyl_FG {
    public ConjCycPentyl_FG(Atom a_atom) {
        super(a_atom);

        Atom[] c = CompoundInspector.extractSkeletonChain(super.appendAtom(), 5);

        CompoundReactor.alkaneToConjugated(super.getCompound(), c, 1);
    }

    @Override
    public String getName() {
        return "conj-cyc-pentyl";
    }
}
