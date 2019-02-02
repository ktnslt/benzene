package com.coldradio.benzene.compound.funcgroup;

import com.coldradio.benzene.compound.Atom;
import com.coldradio.benzene.compound.CompoundArranger;
import com.coldradio.benzene.compound.CompoundReactor;
import com.coldradio.benzene.util.Geometry;

public class IsoPropyl_FG extends Ethyl_FG {
    public IsoPropyl_FG(Atom a_atom) {
        super(a_atom);

        Atom c1 = super.appendAtom();
        Atom c2 = c1.getSkeletonAtom();

        Methyl_FG methyl = new Methyl_FG(c1);
        Atom c2_methyl = methyl.appendAtom();

        // set the position of C, but H are NOT arranged here
        c2_methyl.setPoint(Geometry.symmetricToLine(c2.getPoint(), c1.getPoint(), a_atom.getPoint()));
        CompoundReactor.addFunctionalGroupToAtom(super.getCompound(), c1, methyl, true);
        // after FuncGroup is completed, adjust the positions of H
        CompoundArranger.adjustHydrogenPosition(c2_methyl);
    }

    @Override
    public String getName() {
        return "iso-propyl";
    }
}
