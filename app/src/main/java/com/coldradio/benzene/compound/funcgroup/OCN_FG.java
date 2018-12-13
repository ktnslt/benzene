package com.coldradio.benzene.compound.funcgroup;

import com.coldradio.benzene.compound.Atom;
import com.coldradio.benzene.compound.AtomicNumber;
import com.coldradio.benzene.compound.Bond;
import com.coldradio.benzene.compound.CompoundInspector;

public class OCN_FG extends NCO_FG {
    public OCN_FG(Atom a_atom) {
        super(a_atom);

        Atom[] skel = CompoundInspector.extractSkeletonChain(super.appendAtom(), 3);
        skel[0].setAtomicNumber(AtomicNumber.O);
        skel[2].setAtomicNumber(AtomicNumber.N);

        skel[0].setBond(skel[1], Bond.BondType.SINGLE);
        skel[1].setBond(skel[2], Bond.BondType.TRIPLE);
    }

    @Override
    public String getName() {
        return "OCN";
    }
}
