package com.coldradio.benzene.compound.funcgroup;

import com.coldradio.benzene.compound.Atom;
import com.coldradio.benzene.compound.AtomicNumber;

public class NO2_FG extends COO_FG {
    public NO2_FG(Atom a_atom) {
        super(a_atom);

        Atom c1 = super.appendAtom();
        c1.setAtomicNumber(AtomicNumber.N);
        c1.getAtomDecoration().setCharge(1);
    }

    @Override
    public String getName() {
        return "NO2";
    }
}
