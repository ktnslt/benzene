package com.coldradio.benzene.compound.funcgroup;

import com.coldradio.benzene.compound.Atom;
import com.coldradio.benzene.compound.AtomicNumber;
import com.coldradio.benzene.compound.Bond;
import com.coldradio.benzene.compound.CompoundReactor;

public class COCH3_FG extends IsoPropyl_FG {
    public COCH3_FG(Atom a_atom) {
        super(a_atom);

        Atom c1 = super.appendAtom();
        Atom c2 = c1.getSkeletonAtom();

        CompoundReactor.deleteAllHydrogen(super.getCompound(), c2);
        c2.setBond(c1, Bond.BondType.DOUBLE_MIDDLE);
        c2.setAtomicNumber(AtomicNumber.O);
    }

    @Override
    public String getName() {
        return "COCH3";
    }
}
