package com.coldradio.benzene.compound.funcgroup;

import android.graphics.PointF;

import com.coldradio.benzene.compound.Atom;
import com.coldradio.benzene.compound.Compound;
import com.coldradio.benzene.compound.CompoundReactor;

public class Propyl_FG extends Ethyl_FG {
    public Propyl_FG(Atom a_atom) {
        super(a_atom);

        Atom c1 = super.appendAtom();
        Atom c2 = c1.getSkeletonAtom();

        Compound tmpCompound = CompoundReactor.chainCompound(new PointF[]{a_atom.getPoint(), c1.getPoint(), c2.getPoint()});
        Methyl_FG methyl = new Methyl_FG(tmpCompound.getAtom(2));   // append to the last C

        // delete one H in parentCompound, and adjust the positions of remained two H
        super.getCompound().addFunctionalGroupToAtom(methyl, c2, true);
    }

    @Override
    public String getName() {
        return "propyl";
    }
}
