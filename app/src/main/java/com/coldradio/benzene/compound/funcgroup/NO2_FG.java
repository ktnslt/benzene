package com.coldradio.benzene.compound.funcgroup;

import com.coldradio.benzene.compound.Atom;
import com.coldradio.benzene.compound.AtomCondition;
import com.coldradio.benzene.compound.AtomicNumber;
import com.coldradio.benzene.compound.CompoundInspector;

public class NO2_FG extends COO_FG {
    public NO2_FG(Atom a_atom) {
        super(a_atom);

        Atom c1 = super.appendAtom();
        c1.setAtomicNumber(AtomicNumber.N);

        Atom o_minus = CompoundInspector.findBoundAtom(c1, new AtomCondition().atomicNumber(AtomicNumber.O).charge(-1));
        o_minus.getAtomDecoration().setCharge(0);
    }

    @Override
    public String getName() {
        return "NO2";
    }
}
