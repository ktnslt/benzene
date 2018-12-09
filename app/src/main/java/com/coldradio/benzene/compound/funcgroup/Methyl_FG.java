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
    private PointF mSelectedAtomPoint;

    public Methyl_FG(Atom selectedAtom) {
        // create Methyl
        mMethyl = new Compound(new int[]{0, 1, 2, 3}, new AtomicNumber[]{AtomicNumber.C, AtomicNumber.H, AtomicNumber.H, AtomicNumber.H});
        // the attachment point of the functional group has aid 0
        mMethyl.makeBond(0, 1, Bond.BondType.SINGLE);
        mMethyl.makeBond(0, 2, Bond.BondType.SINGLE);
        mMethyl.makeBond(0, 3, Bond.BondType.SINGLE);
        mMethyl.getAtom(0).setPoint(0, 0);

        float carbonHydrogenLength = Configuration.BOND_LENGTH / 2;

        mMethyl.getAtom(1).setPoint(-carbonHydrogenLength, 0);
        mMethyl.getAtom(2).setPoint(carbonHydrogenLength, 0);
        mMethyl.getAtom(3).setPoint(0, -carbonHydrogenLength);

        // move to above to the selectedAtom. if selectedAtom is positioned at (x, y), the compound is moved to (x, y-BOND_LENGTH)
        mMethyl.offset(selectedAtom.getPoint().x, selectedAtom.getPoint().y - Configuration.BOND_LENGTH);

        RuleSet.instance().apply(mMethyl);
        // since this is methane, the default HydrogenMode is LETTERING_H. So change it to HIDE_H_BOND
        attachAtom().setHydrogenMode(Atom.HydrogenMode.HIDE_H_BOND);

        mAttachPoint = new SingleAttachPoint(selectedAtom);
        mSelectedAtomPoint = selectedAtom.getPoint();

        nextForm();
    }

    @Override
    public Compound nextForm() {
        mCurrentAttachPoint = mAttachPoint.nextPoint(mCurrentAttachPoint);

        float angle = Geometry.cwAngle(attachAtom().getPoint(), mCurrentAttachPoint, mSelectedAtomPoint);

        CompoundArranger.rotate(mMethyl, mSelectedAtomPoint, angle);

        return mMethyl;
    }

    @Override
    public Compound prevForm() {
        mCurrentAttachPoint = mAttachPoint.prevPoint(mCurrentAttachPoint);

        float angle = Geometry.cwAngle(attachAtom().getPoint(), mCurrentAttachPoint, mSelectedAtomPoint);

        CompoundArranger.rotate(mMethyl, mSelectedAtomPoint, angle);

        return mMethyl;
    }

    @Override
    public Compound curForm() {
        return mMethyl;
    }

    @Override
    public String getName() {
        return "Methyl";
    }

    @Override
    public Atom attachAtom() {
        return mMethyl.getAtom(0);
    }
}
