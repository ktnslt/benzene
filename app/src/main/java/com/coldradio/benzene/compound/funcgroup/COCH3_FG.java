package com.coldradio.benzene.compound.funcgroup;

import com.coldradio.benzene.compound.Atom;
import com.coldradio.benzene.compound.CompoundReactor;

public class COCH3_FG extends CHO_FG {
    public COCH3_FG(Atom a_atom) {
        super(a_atom);

        CompoundReactor.deleteAllHydrogen(super.getCompound());

        Atom c1 = super.appendAtom();
        Methyl_FG methyl = new Methyl_FG(c1);

        super.getCompound().addFunctionalGroupToAtom(methyl, c1, true);
    }

    @Override
    public String getName() {
        return "COCH3";
    }
}
