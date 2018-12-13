package com.coldradio.benzene.compound.funcgroup;

import com.coldradio.benzene.compound.Atom;
import com.coldradio.benzene.compound.AtomicNumber;
import com.coldradio.benzene.compound.Bond;
import com.coldradio.benzene.compound.CompoundReactor;

public class Ketone_FG extends Methyl_FG {
    public Ketone_FG(Atom a_atom) {
        super(a_atom);

        CompoundReactor.deleteAllHydrogen(super.getCompound());

        Atom c1 = super.appendAtom();

        c1.setAtomicNumber(AtomicNumber.O);
        c1.getAtomDecoration().setShowElementName(true);
    }

    @Override
    public String getName() {
        return "ketone";
    }

    @Override
    public Bond.BondType bondType() {
        return Bond.BondType.DOUBLE_MIDDLE;
    }
}
