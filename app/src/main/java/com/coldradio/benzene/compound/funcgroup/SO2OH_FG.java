package com.coldradio.benzene.compound.funcgroup;

import com.coldradio.benzene.compound.Atom;
import com.coldradio.benzene.compound.AtomCondition;
import com.coldradio.benzene.compound.AtomicNumber;
import com.coldradio.benzene.compound.CompoundInspector;
import com.coldradio.benzene.compound.CompoundReactor;

public class SO2OH_FG extends SO2Me_FG {
    public SO2OH_FG(Atom a_atom) {
        super(a_atom);

        Atom s = super.appendAtom();
        Atom c_of_s = CompoundInspector.findBoundAtom(s, new AtomCondition().atomicNumber(AtomicNumber.C));

        CompoundReactor.deleteHydrogen(super.getCompound(), c_of_s, 2);
        c_of_s.setAtomicNumber(AtomicNumber.O);
        c_of_s.lettering(true);
    }

    @Override
    public String getName() {
        return "SO2OH";
    }
}
