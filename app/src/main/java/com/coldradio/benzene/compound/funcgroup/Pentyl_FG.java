package com.coldradio.benzene.compound.funcgroup;

import android.graphics.PointF;

import com.coldradio.benzene.compound.Atom;
import com.coldradio.benzene.compound.Compound;
import com.coldradio.benzene.compound.CompoundInspector;
import com.coldradio.benzene.compound.CompoundReactor;

public class Pentyl_FG extends Butyl_FG {
    public Pentyl_FG(Atom a_atom) {
        super(a_atom);

        Atom[] c = CompoundInspector.extractSkeletonChain(super.appendAtom(), 4);
        Compound tmpCompound = CompoundReactor.chainCompound(new PointF[]{c[1].getPoint(), c[2].getPoint(), c[3].getPoint()});
        Methyl_FG methyl = new Methyl_FG(tmpCompound.getAtom(2));   // append to the last

        CompoundReactor.addFunctionalGroupToAtom(super.getCompound(), c[3], methyl, true);
    }

    @Override
    public String getName() {
        return "pentyl";
    }
}
