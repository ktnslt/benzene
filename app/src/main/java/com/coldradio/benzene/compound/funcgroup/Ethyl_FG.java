package com.coldradio.benzene.compound.funcgroup;

import android.graphics.PointF;

import com.coldradio.benzene.compound.Atom;
import com.coldradio.benzene.compound.Compound;
import com.coldradio.benzene.compound.CompoundReactor;
import com.coldradio.benzene.util.Geometry;
import com.coldradio.benzene.util.MathConstant;

public class Ethyl_FG extends Methyl_FG {
    protected PointF hydrogenPoint1of2(PointF a_atom, PointF b_atom, int h_num) {
        return Geometry.cwRotate(b_atom, a_atom, MathConstant.RADIAN_90 * h_num);
    }

    public Ethyl_FG(Atom a_atom) {
        Atom first_methyl = appendMethyl(a_atom);

        // C--C(a_atom)--C
        //
        //    first_methyl (this is NOT connected to a_atom!!).
        // Hence a chain compound contains methyl_atom, a_atom, b_atom will be created
        /// the purpose is to set the position of the second appended methyl
        Compound tmpCompound = CompoundReactor.chainCompound(new PointF[]{
                first_methyl.getPoint(),
                a_atom.getPoint(),
                a_atom.getSkeletonAtom() != null ? a_atom.getSkeletonAtom().getPoint() : null});
        Methyl_FG second_methyl = new Methyl_FG(tmpCompound.getAtom(0));

        first_methyl.singleBond(second_methyl.mFuncGroup.getAtom(0));
        mFuncGroup.merge(second_methyl.getCompound());
    }

    @Override
    public String getName() {
        return "Ethyl";
    }
}
