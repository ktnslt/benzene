package com.coldradio.benzene.compound.funcgroup;

import com.coldradio.benzene.compound.Atom;
import com.coldradio.benzene.compound.AtomicNumber;
import com.coldradio.benzene.compound.Bond;
import com.coldradio.benzene.compound.CompoundReactor;

public class COO_FG extends IsoPropyl_FG {
    public COO_FG(Atom a_atom) {
        super(a_atom);

        CompoundReactor.deleteAllHydrogen(super.getCompound());

        Atom c1 = super.appendAtom();
        Atom c2 = c1.getSkeletonAtom();
        Atom c2_dot = c1.getSkeletonAtomExcept(c2);

        c2.setAtomicNumber(AtomicNumber.O);
        c1.doubleBond(c2);
        c2.setBond(c1, Bond.BondType.DOUBLE_MIDDLE);

        c2_dot.setAtomicNumber(AtomicNumber.O);
        c2_dot.getAtomDecoration().setCharge(-1);
    }

    @Override
    public String getName() {
        return "COO-";
    }
}
