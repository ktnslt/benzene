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
        Methyl_FG second_methyl = new Methyl_FG(tmpCompound.getAtom(0));
        Atom c2 = second_methyl.appendAtom();

        // delete one H in parentCompound, and adjust the positions of remained two H
        Compound parentCompound = super.getCompound();

        parentCompound.delete(parentCompound.getAtom(3));   // aid 1, 2, 3 are H. delete the last one
        parentCompound.getAtom(1).setPoint(CompoundArranger.hydrogenPointOfBentForm(c1.getPoint(), c2.getPoint(), a_atom.getPoint(), 1));
        parentCompound.getAtom(2).setPoint(CompoundArranger.hydrogenPointOfBentForm(c1.getPoint(), c2.getPoint(), a_atom.getPoint(), 2));

        c1.singleBond(second_methyl.appendAtom());
        parentCompound.merge(second_methyl.getCompound());
    }

    @Override
    public String getName() {
        return "Ethyl";
    }
}
