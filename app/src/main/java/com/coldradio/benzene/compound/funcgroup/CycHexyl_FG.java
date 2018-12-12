package com.coldradio.benzene.compound.funcgroup;

import com.coldradio.benzene.compound.Atom;
import com.coldradio.benzene.compound.CompoundReactor;

public class CycHexyl_FG extends Hexyl_FG {
    public CycHexyl_FG(Atom a_atom) {
        super(a_atom);

        Atom[] c = new Atom[6];

        c[0] = super.appendAtom();  // c1
        c[1] = c[0].getSkeletonAtom();
        for (int ii = 2; ii < 6; ++ii) {
            c[ii] = c[ii-1].getSkeletonAtomExcept(c[ii-2]);
        }

        CompoundReactor.alkaneToCyclo(super.getCompound(), c, a_atom.getPoint());
    }

    @Override
    public String getName() {
        return "cyc-hexyl";
    }
}
