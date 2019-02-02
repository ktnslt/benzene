package com.coldradio.benzene.compound.funcgroup;

import android.graphics.PointF;

import com.coldradio.benzene.compound.Atom;
import com.coldradio.benzene.compound.Compound;
import com.coldradio.benzene.compound.CompoundReactor;

public class SecButyl_FG extends IsoPropyl_FG {
    public SecButyl_FG(Atom a_atom) {
        super(a_atom);

        Atom c1 = super.appendAtom();
        Atom c2 = c1.getSkeletonAtom();
        Atom c2_dot = c1.getSkeletonAtomExcept(c2);

        Compound tmpCompound = CompoundReactor.chainCompound(new PointF[]{c2.getPoint(), c1.getPoint(), c2_dot.getPoint()});
        Methyl_FG methyl = new Methyl_FG(tmpCompound.getAtom(2));   // append to the last C

        CompoundReactor.addFunctionalGroupToAtom(super.getCompound(), c2_dot, methyl, true);
    }

    @Override
    public String getName() {
        return "s-butyl";
    }
}
