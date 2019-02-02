package com.coldradio.benzene.compound.funcgroup;

import com.coldradio.benzene.compound.Atom;
import com.coldradio.benzene.compound.AtomicNumber;
import com.coldradio.benzene.compound.CompoundReactor;
import com.coldradio.benzene.project.Configuration;
import com.coldradio.benzene.util.Geometry;

public class NMeH_FG extends NMe2_FG {
    public NMeH_FG(Atom a_atom) {
        super(a_atom);

        Atom c1 = super.appendAtom();
        Atom c2 = c1.getSkeletonAtom();

        CompoundReactor.deleteAllHydrogen(super.getCompound(), c2);
        c2.setAtomicNumber(AtomicNumber.H);
        c2.setPoint(Geometry.pointInLine(c1.getPoint(), c2.getPoint(), Configuration.H_BOND_LENGTH_RATIO));
        c1.getAtomDecoration().lettering(true);
    }

    @Override
    public String getName() {
        return "NMeH";
    }
}
