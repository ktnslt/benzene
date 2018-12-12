package com.coldradio.benzene.compound.funcgroup;

import android.graphics.PointF;

import com.coldradio.benzene.compound.Atom;
import com.coldradio.benzene.compound.AtomicNumber;
import com.coldradio.benzene.compound.Bond;
import com.coldradio.benzene.compound.Compound;
import com.coldradio.benzene.compound.CompoundArranger;
import com.coldradio.benzene.library.rule.RuleSet;
import com.coldradio.benzene.project.Configuration;
import com.coldradio.benzene.util.Geometry;
import com.coldradio.benzene.util.MathConstant;

public class Methyl_FG implements IFunctionalGroup {
    protected Compound mFuncGroup;
    protected AppendPointProducer mAppendPointProducer;
    protected PointF mAppendPoint;
    protected PointF m_a_AtomPoint;

    protected PointF hydrogenPoint1of3(PointF a_atom, PointF b_atom, int h_num) {
        PointF zoomed_b_atom = Geometry.zoom(b_atom, a_atom, Configuration.H_BOND_LENGTH_RATIO);

        return Geometry.cwRotate(zoomed_b_atom, a_atom, MathConstant.RADIAN_90 * h_num);
    }

    protected Atom appendMethyl(Atom a_atom) {
        // a_atom is the selected atom, and the attach Atom. append atom is attach to attach-atom
        mAppendPointProducer = new AppendPointProducer(a_atom);
        m_a_AtomPoint = a_atom.getPoint();

        // create Methyl
        mFuncGroup = new Compound(new int[]{0, 1, 2, 3}, new AtomicNumber[]{AtomicNumber.C, AtomicNumber.H, AtomicNumber.H, AtomicNumber.H});
        // the attachment point of the functional group has aid 0
        mFuncGroup.makeBond(0, 1, Bond.BondType.SINGLE);
        mFuncGroup.makeBond(0, 2, Bond.BondType.SINGLE);
        mFuncGroup.makeBond(0, 3, Bond.BondType.SINGLE);
        // position of C
        Atom carbon = mFuncGroup.getAtom(0);

        carbon.setPoint(mAppendPointProducer.firstPoint());
        mAppendPoint = carbon.getPoint();
        // position of H
        mFuncGroup.getAtom(1).setPoint(hydrogenPoint1of3(carbon.getPoint(), a_atom.getPoint(), 1));
        mFuncGroup.getAtom(2).setPoint(hydrogenPoint1of3(carbon.getPoint(), a_atom.getPoint(), 2));
        mFuncGroup.getAtom(3).setPoint(hydrogenPoint1of3(carbon.getPoint(), a_atom.getPoint(), 3));

        RuleSet.instance().apply(mFuncGroup);
        // since this is methane, the default HydrogenMode is LETTERING_H. So change it to HIDE_H_BOND
        carbon.setHydrogenMode(Atom.HydrogenMode.HIDE_H_BOND);

        return carbon;
    }

    protected Methyl_FG() {
    }

    public Methyl_FG(Atom a_atom) {
        appendMethyl(a_atom);
    }

    @Override
    public Compound nextForm() {
        mAppendPoint = mAppendPointProducer.nextPoint(mAppendPoint);

        float angle = Geometry.cwAngle(attachAtom().getPoint(), mAppendPoint, m_a_AtomPoint);

        CompoundArranger.rotate(mFuncGroup, m_a_AtomPoint, angle);

        return mFuncGroup;
    }

    @Override
    public Compound prevForm() {
        mAppendPoint = mAppendPointProducer.prevPoint(mAppendPoint);

        float angle = Geometry.cwAngle(attachAtom().getPoint(), mAppendPoint, m_a_AtomPoint);

        CompoundArranger.rotate(mFuncGroup, m_a_AtomPoint, angle);

        return mFuncGroup;
    }

    @Override
    public Compound curForm() {
        return mFuncGroup;
    }

    @Override
    public String getName() {
        return "Methyl";
    }

    @Override
    public Atom attachAtom() {
        return mFuncGroup.getAtom(0);
    }

    public Compound getCompound() {
        return mFuncGroup;
    }
}
