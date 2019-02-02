package com.coldradio.benzene.compound.funcgroup;

import android.graphics.PointF;

import com.coldradio.benzene.compound.Atom;
import com.coldradio.benzene.compound.Compound;
import com.coldradio.benzene.compound.CompoundInspector;
import com.coldradio.benzene.compound.CompoundReactor;

import java.util.List;

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

        CompoundReactor.addFunctionalGroupToAtom(super.getCompound(), c1, methyl, true);

        Atom c2 = methyl.appendAtom();
        // adjust the hydrogen position of C1 again. this is related to https://github.com/ktnslt/benzene/issues/45
        tmpCompound = CompoundReactor.chainCompound(new PointF[]{a_atom.getPoint(), c1.getPoint(), c2.getPoint()});
        CompoundReactor.saturateWithHydrogen(tmpCompound, tmpCompound.getAtom(1), 4);
        // now copy the position of H to Ethyl
        List<Atom> tmpH = CompoundInspector.allHydrogens(tmpCompound.getAtom(1));
        List<Atom> c1H = CompoundInspector.allHydrogens(c1);

        c1H.get(0).setPoint(tmpH.get(0).getPoint());
        c1H.get(1).setPoint(tmpH.get(1).getPoint());
    }

    @Override
    public String getName() {
        return "ethyl";
    }
}
