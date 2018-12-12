package com.coldradio.benzene.compound.funcgroup;

import com.coldradio.benzene.compound.Atom;
import com.coldradio.benzene.compound.CompoundReactor;

public class ConjCycPentyl_FG extends CycPentyl_FG {
    public ConjCycPentyl_FG(Atom a_atom) {
        super(a_atom);

        Atom[] c = new Atom[5];

        c[0] = super.appendAtom();  // c1
        c[1] = c[0].getSkeletonAtom();
        for (int ii = 2; ii < 5; ++ii) {
            c[ii] = c[ii-1].getSkeletonAtomExcept(c[ii-2]);
        }

        CompoundReactor.alkaneToConjugated(super.getCompound(), c, 1);
    }

    @Override
    public String getName() {
        return "conj-cyc-pentyl";
    }
}
