package com.coldradio.benzene.compound.funcgroup;

import com.coldradio.benzene.compound.Atom;
import com.coldradio.benzene.compound.AtomCondition;
import com.coldradio.benzene.compound.AtomicNumber;
import com.coldradio.benzene.compound.Bond;
import com.coldradio.benzene.compound.CompoundArranger;
import com.coldradio.benzene.compound.CompoundInspector;

public class COOH_FG extends COO_FG {
    public COOH_FG(Atom a_atom) {
        super(a_atom);

        Atom c1 = super.appendAtom();
        Atom o_minus = CompoundInspector.findBoundAtom(c1, new AtomCondition().atomicNumber(AtomicNumber.O).charge(-1));
        Atom h = new Atom(-1, AtomicNumber.H);

        o_minus.getAtomDecoration().setCharge(0);
        super.getCompound().addAtom(o_minus, Bond.BondType.SINGLE, h);
        o_minus.lettering(true);
        CompoundArranger.adjustHydrogenPosition(c1);
    }

    @Override
    public String getName() {
        return "COOH";
    }
}
