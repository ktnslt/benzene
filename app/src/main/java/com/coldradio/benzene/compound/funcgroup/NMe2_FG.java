package com.coldradio.benzene.compound.funcgroup;

import com.coldradio.benzene.compound.Atom;
import com.coldradio.benzene.compound.AtomicNumber;
import com.coldradio.benzene.compound.CompoundReactor;

public class NMe2_FG extends IsoPropyl_FG {
    public NMe2_FG(Atom a_atom) {
        super(a_atom);

        Atom c1 = super.appendAtom();

        CompoundReactor.deleteAndCutBonds(super.getCompound(), c1.getHydrogen());

        c1.setAtomicNumber(AtomicNumber.N);
    }

    @Override
    public String getName() {
        return "NMe2";
    }
}
