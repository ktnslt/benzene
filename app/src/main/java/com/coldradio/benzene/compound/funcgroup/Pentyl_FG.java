package com.coldradio.benzene.compound.funcgroup;

import android.graphics.PointF;

import com.coldradio.benzene.compound.Atom;
import com.coldradio.benzene.compound.Compound;
import com.coldradio.benzene.compound.CompoundReactor;

public class Pentyl_FG extends Butyl_FG {
    public Pentyl_FG(Atom a_atom) {
        super(a_atom);

        Atom c1 = super.appendAtom();
        Atom c2 = c1.getSkeletonAtom();
        Atom c3 = c2.getSkeletonAtomExcept(c1);
        Atom c4 = c3.getSkeletonAtomExcept(c2);

        Compound tmpCompound = CompoundReactor.chainCompound(new PointF[]{c2.getPoint(), c3.getPoint(), c4.getPoint()});
        Methyl_FG methyl = new Methyl_FG(tmpCompound.getAtom(2));   // append to the last

        super.getCompound().addFunctionalGroupToAtom(methyl, c4, true);
    }

    @Override
    public String getName() {
        return "pentyl";
    }
}
