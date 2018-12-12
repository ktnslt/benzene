package com.coldradio.benzene.compound.funcgroup;

import com.coldradio.benzene.compound.Atom;
import com.coldradio.benzene.compound.CompoundArranger;
import com.coldradio.benzene.util.Geometry;

public class TertButyl_FG extends IsoPropyl_FG {
    public TertButyl_FG(Atom a_atom) {
        super(a_atom);

        Atom c1 = super.appendAtom();

        Methyl_FG methyl = new Methyl_FG(c1);
        Atom c_in_methyl = methyl.appendAtom();

        // set the position of C, but H are NOT arranged here
        c_in_methyl.setPoint(Geometry.symmetricToPoint(a_atom.getPoint(), c1.getPoint()));
        super.getCompound().addFunctionalGroupToAtom(methyl, c1, true);
        // after FuncGroup is completed, adjust the positions of H
        CompoundArranger.adjustHydrogenPosition(c_in_methyl);
    }

    @Override
    public String getName() {
        return "t-butyl";
    }
}
