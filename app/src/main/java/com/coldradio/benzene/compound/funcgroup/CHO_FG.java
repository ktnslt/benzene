package com.coldradio.benzene.compound.funcgroup;

import com.coldradio.benzene.compound.Atom;
import com.coldradio.benzene.compound.AtomicNumber;
import com.coldradio.benzene.compound.Bond;
import com.coldradio.benzene.compound.CompoundReactor;
import com.coldradio.benzene.project.Configuration;
import com.coldradio.benzene.util.Geometry;

public class CHO_FG extends IsoPropyl_FG {
    public CHO_FG(Atom a_atom) {
        super(a_atom);

        CompoundReactor.deleteAllHydrogen(super.getCompound());

        Atom c1 = super.appendAtom();
        Atom c2 = c1.getSkeletonAtom();
        Atom c2_dot = c1.getSkeletonAtomExcept(c2);

        c2.setAtomicNumber(AtomicNumber.O);
        c1.doubleBond(c2);
        c2.setBond(c1, Bond.BondType.DOUBLE_MIDDLE);

        c2_dot.setAtomicNumber(AtomicNumber.H);
        c2_dot.getAtomDecoration().setShowElementName(false);
        c2_dot.setPoint(Geometry.pointInLine(c1.getPoint(), c2_dot.getPoint(), Configuration.H_BOND_LENGTH_RATIO));
    }

    @Override
    public String getName() {
        return "CHO";
    }
}
