package com.coldradio.benzene.compound.funcgroup;

import com.coldradio.benzene.compound.Atom;
import com.coldradio.benzene.compound.AtomicNumber;
import com.coldradio.benzene.compound.CompoundInspector;

public class NNN_FG extends NCO_FG {
    public NNN_FG(Atom a_atom) {
        super(a_atom);

        Atom[] c = CompoundInspector.extractSkeletonChain(super.appendAtom(), 3);

        c[1].setAtomicNumber(AtomicNumber.N);
        c[2].setAtomicNumber(AtomicNumber.N);
    }

    @Override
    public String getName() {
        return "NNN";
    }
}
