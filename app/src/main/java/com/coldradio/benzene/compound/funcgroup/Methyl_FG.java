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

public class Methyl_FG implements IFunctionalGroup {
    private Compound mMethyl;
    private SingleAttachPoint mAttachPoint;
    private PointF mCurrentAttachPoint;
    private PointF mAttachAtomPoint;

    public Methyl_FG(Atom attachAtom) {
        mMethyl = new Compound(new int[]{0, 1, 2, 3}, new AtomicNumber[]{AtomicNumber.C, AtomicNumber.H, AtomicNumber.H, AtomicNumber.H});
        mMethyl.makeBond(0, 1, Bond.BondType.SINGLE);
        mMethyl.makeBond(0, 2, Bond.BondType.SINGLE);
        mMethyl.makeBond(0, 3, Bond.BondType.SINGLE);
        mMethyl.getAtom(0).setPoint(0, 0);
        mMethyl.getAtom(1).setPoint(-Configuration.LINE_LENGTH, 0);
        mMethyl.getAtom(2).setPoint(Configuration.LINE_LENGTH, 0);
        mMethyl.getAtom(3).setPoint(0, Configuration.LINE_LENGTH);
        mMethyl.offset(-Configuration.LINE_LENGTH, 0);

        RuleSet.instance().apply(mMethyl);

        mAttachPoint = new SingleAttachPoint(attachAtom);
        mAttachAtomPoint = attachAtom.getPoint();
    }

    @Override
    public Compound nextForm() {
        mCurrentAttachPoint = mAttachPoint.nextPoint(mCurrentAttachPoint);

        float angle = Geometry.cwAngle(mMethyl.getAtom(0).getPoint(), mCurrentAttachPoint, mAttachAtomPoint);

        CompoundArranger.rotate(mMethyl, mAttachAtomPoint, angle);

        return mMethyl;
    }

    @Override
    public Compound prevForm() {
        mCurrentAttachPoint = mAttachPoint.prevPoint(mCurrentAttachPoint);

        float angle = Geometry.cwAngle(mMethyl.getAtom(0).getPoint(), mCurrentAttachPoint, mAttachAtomPoint);

        CompoundArranger.rotate(mMethyl, mAttachAtomPoint, angle);

        return mMethyl;
    }

    @Override
    public String getName() {
        return "Methyl";
    }
}
