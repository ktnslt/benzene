package com.coldradio.benzene.compound.funcgroup;

import android.graphics.PointF;

import com.coldradio.benzene.compound.Atom;
import com.coldradio.benzene.compound.Compound;
import com.coldradio.benzene.compound.CompoundArranger;
import com.coldradio.benzene.compound.CompoundReactor;

public class Ethyl_FG extends Methyl_FG {
    public Ethyl_FG(Atom a_atom) {
        super(a_atom);

        Atom c1 = super.appendAtom();
        // C--C(a_atom)--C
        //
        //    c1 (this is NOT connected to a_atom!!).
        // Hence a chain compound contains methyl_atom, a_atom, b_atom will be created
        /// the purpose is to set the position of the second appended methyl
        Compound tmpCompound = CompoundReactor.chainCompound(new PointF[]{
                c1.getPoint(),      // aid 0
                a_atom.getPoint(),  // aid 1
                (a_atom.getSkeletonAtom() != null) ? a_atom.getSkeletonAtom().getPoint() : null});  // aid 2
        Methyl_FG methyl = new Methyl_FG(tmpCompound.getAtom(0));

        super.getCompound().addFunctionalGroupToAtom(methyl, c1, true);

        // adjust the hydrogen position of C1 again. this is related to https://github.com/ktnslt/benzene/issues/45
        // TODO build the simple temporary compound here, and
    }

    @Override
    public String getName() {
        return "ethyl";
    }
}
