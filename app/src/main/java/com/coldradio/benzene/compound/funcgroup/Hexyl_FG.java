package com.coldradio.benzene.compound.funcgroup;

import android.graphics.PointF;

import com.coldradio.benzene.compound.Atom;
import com.coldradio.benzene.compound.Compound;
import com.coldradio.benzene.compound.CompoundReactor;

public class Hexyl_FG extends Pentyl_FG {
    public Hexyl_FG(Atom a_atom) {
        super(a_atom);

        Atom c1 = super.appendAtom();
        Atom c2 = c1.getSkeletonAtom();
        Atom c3 = c2.getSkeletonAtomExcept(c1);
        Atom c4 = c3.getSkeletonAtomExcept(c2);
        Atom c5 = c4.getSkeletonAtomExcept(c3);

        Compound tmpCompound = CompoundReactor.chainCompound(new PointF[]{c3.getPoint(), c4.getPoint(), c5.getPoint()});
        Methyl_FG methyl = new Methyl_FG(tmpCompound.getAtom(2));   // append to the last

        super.getCompound().addFunctionalGroupToAtom(methyl, c5, true);
    }

    @Override
    public String getName() {
        return "hexyl";
    }
}
