package com.coldradio.benzene.compound.funcgroup;

import android.graphics.PointF;

import com.coldradio.benzene.compound.Atom;
import com.coldradio.benzene.compound.AtomicNumber;
import com.coldradio.benzene.compound.Bond;
import com.coldradio.benzene.compound.Compound;
import com.coldradio.benzene.compound.CompoundArranger;
import com.coldradio.benzene.library.rule.RuleSet;
import com.coldradio.benzene.util.Geometry;

public class Methyl_FG implements IFunctionalGroup {
    private Compound mFuncGroup;
    private AppendPointProducer mAppendPointProducer;
    private PointF mAppendPoint;
    private PointF m_a_AtomPoint;

    public Methyl_FG(Atom a_atom) {
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
        Atom c1 = mFuncGroup.getAtom(0);

        c1.setPoint(mAppendPointProducer.firstPoint());
        mAppendPoint = c1.getPoint();
        // position of H
        mFuncGroup.getAtom(1).setPoint(CompoundArranger.hydrogenPointOfEnd(c1.getPoint(), a_atom.getPoint(), 1));
        mFuncGroup.getAtom(2).setPoint(CompoundArranger.hydrogenPointOfEnd(c1.getPoint(), a_atom.getPoint(), 2));
        mFuncGroup.getAtom(3).setPoint(CompoundArranger.hydrogenPointOfEnd(c1.getPoint(), a_atom.getPoint(), 3));

        RuleSet.instance().apply(mFuncGroup);
        // since this is methane, the default HydrogenMode is LETTERING_H. So change it to HIDE_H_BOND
        c1.lettering(false);
    }

    @Override
    public Compound nextForm() {
        mAppendPoint = mAppendPointProducer.nextPoint(mAppendPoint);

        float angle = Geometry.cwAngle(appendAtom().getPoint(), mAppendPoint, m_a_AtomPoint);

        CompoundArranger.rotate(mFuncGroup, m_a_AtomPoint, angle);

        return mFuncGroup;
    }

    @Override
    public Compound prevForm() {
        mAppendPoint = mAppendPointProducer.prevPoint(mAppendPoint);

        float angle = Geometry.cwAngle(appendAtom().getPoint(), mAppendPoint, m_a_AtomPoint);

        CompoundArranger.rotate(mFuncGroup, m_a_AtomPoint, angle);

        return mFuncGroup;
    }

    @Override
    public Compound curForm() {
        return mFuncGroup;
    }

    @Override
    public String getName() {
        return "methyl";
    }

    @Override
    public Atom appendAtom() {
        return mFuncGroup.getAtom(0);
    }

    @Override
    public Bond.BondType bondType() {
        return Bond.BondType.SINGLE;
    }

    public Compound getCompound() {
        return mFuncGroup;
    }
}
