package com.coldradio.benzene.compound.funcgroup;

import android.graphics.PointF;

import com.coldradio.benzene.compound.Atom;
import com.coldradio.benzene.compound.Compound;
import com.coldradio.benzene.compound.CompoundInspector;
import com.coldradio.benzene.compound.CompoundReactor;

public class Butyl_FG extends Propyl_FG {
    public Butyl_FG(Atom a_atom) {
        super(a_atom);

        Atom[] c = CompoundInspector.extractSkeletonChain(super.appendAtom(), 3);

        Compound tmpCompound = CompoundReactor.chainCompound(new PointF[]{c[0].getPoint(), c[1].getPoint(), c[2].getPoint()});
        Methyl_FG methyl = new Methyl_FG(tmpCompound.getAtom(2));

        super.getCompound().addFunctionalGroupToAtom(methyl, c[2], true);
    }

    @Override
    public String getName() {
        return "butyl";
    }
}
