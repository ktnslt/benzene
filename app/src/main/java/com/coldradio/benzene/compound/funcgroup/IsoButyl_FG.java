package com.coldradio.benzene.compound.funcgroup;

import android.graphics.PointF;

import com.coldradio.benzene.compound.Atom;
import com.coldradio.benzene.compound.Compound;
import com.coldradio.benzene.compound.CompoundInspector;
import com.coldradio.benzene.compound.CompoundReactor;

public class IsoButyl_FG extends Propyl_FG {
    public IsoButyl_FG(Atom a_atom) {
        super(a_atom);

        Atom[] c = CompoundInspector.extractSkeletonChain(super.appendAtom(), 3);

        Compound tmpCompound = CompoundReactor.chainCompound(new PointF[]{c[0].getPoint(), c[1].getPoint(), c[2].getPoint()});
        Methyl_FG methyl = new Methyl_FG(tmpCompound.getAtom(1));   // append to the middle C

        super.getCompound().addFunctionalGroupToAtom(methyl, c[1], true);
    }

    @Override
    public String getName() {
        return "iso-butyl";
    }
}
