package com.coldradio.benzene.compound.funcgroup;

import com.coldradio.benzene.compound.Atom;
import com.coldradio.benzene.compound.AtomicNumber;
import com.coldradio.benzene.compound.CompoundReactor;
import com.coldradio.benzene.project.Configuration;
import com.coldradio.benzene.util.Geometry;

public class NH2_FG extends NMeH_FG {
    public NH2_FG(Atom a_atom) {
        super(a_atom);

        Atom n = super.appendAtom();
        Atom c2 = n.getSkeletonAtom();

        CompoundReactor.deleteAllHydrogen(super.getCompound(), c2);
        c2.setAtomicNumber(AtomicNumber.H);
        c2.setPoint(Geometry.pointInLine(n.getPoint(), c2.getPoint(), Configuration.H_BOND_LENGTH_RATIO));

        n.setHydrogenMode(Atom.HydrogenMode.LETTERING_H);
    }

    @Override
    public String getName() {
        return "NH2";
    }
}
