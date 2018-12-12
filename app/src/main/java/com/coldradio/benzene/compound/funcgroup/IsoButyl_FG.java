package com.coldradio.benzene.compound.funcgroup;

import android.graphics.PointF;

import com.coldradio.benzene.compound.Atom;
import com.coldradio.benzene.compound.Compound;
import com.coldradio.benzene.compound.CompoundReactor;

public class IsoButyl_FG extends Propyl_FG {
    public IsoButyl_FG(Atom a_atom) {
        super(a_atom);

        Atom c1 = super.appendAtom();
        Atom c2 = c1.getSkeletonAtom();
        Atom c3 = c2.getSkeletonAtomExcept(c1);

        Compound tmpCompound = CompoundReactor.chainCompound(new PointF[]{c1.getPoint(), c2.getPoint(), c3.getPoint()});
        Methyl_FG methyl = new Methyl_FG(tmpCompound.getAtom(1));   // append to the middle C

        super.getCompound().addFunctionalGroupToAtom(methyl, c2, true);
    }

    @Override
    public String getName() {
        return "iso-butyl";
    }
}
