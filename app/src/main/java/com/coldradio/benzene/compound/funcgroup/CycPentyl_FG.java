package com.coldradio.benzene.compound.funcgroup;

import com.coldradio.benzene.compound.Atom;
import com.coldradio.benzene.compound.CompoundInspector;
import com.coldradio.benzene.compound.CompoundReactor;

public class CycPentyl_FG extends Pentyl_FG {
    public CycPentyl_FG(Atom a_atom) {
        super(a_atom);

        Atom[] c = CompoundInspector.extractSkeletonChain(super.appendAtom(), 5);

        CompoundReactor.alkaneToCyclo(super.getCompound(), c, a_atom.getPoint());
    }

    @Override
    public String getName() {
        return "cyc-pentyl";
    }
}
