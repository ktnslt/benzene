package com.coldradio.benzene.compound.funcgroup;

import com.coldradio.benzene.compound.Atom;
import com.coldradio.benzene.compound.AtomicNumber;
import com.coldradio.benzene.compound.Bond;
import com.coldradio.benzene.compound.CompoundInspector;
import com.coldradio.benzene.compound.CompoundReactor;
import com.coldradio.benzene.util.Geometry;

public class NCO_FG extends Propyl_FG {
    public NCO_FG(Atom a_atom) {
        super(a_atom);

        CompoundReactor.deleteAllHydrogen(super.getCompound());
        Atom[] c = CompoundInspector.extractSkeletonChain(super.appendAtom(), 3);

        c[2].setPoint(Geometry.symmetricToPoint(c[0].getPoint(), c[1].getPoint()));

        c[0].setAtomicNumber(AtomicNumber.N);
        c[2].setAtomicNumber(AtomicNumber.O);

        c[0].setBond(c[1], Bond.BondType.DOUBLE_MIDDLE);
        c[1].setBond(c[2], Bond.BondType.DOUBLE_MIDDLE);

        c[1].getAtomDecoration().setShowElementName(true);
    }

    @Override
    public String getName() {
        return "NCO";
    }
}
