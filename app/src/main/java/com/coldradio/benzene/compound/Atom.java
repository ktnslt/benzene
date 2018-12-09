package com.coldradio.benzene.compound;

import android.graphics.PointF;

import com.coldradio.benzene.util.Helper;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

public class Atom {
    public enum HydrogenMode {
        LETTERING_H, HIDE_H_BOND, SHOW_H_BOND
    }
    private PointF mPoint = new PointF();
    private List<Bond> mBonds = new ArrayList<>();
    private int mAID;   // AID is valid only temporary. When merging two compounds into one, the uniqueness will be broken
    private AtomicNumber mAtomicNumber;
    private String mArbitraryName;
    private HydrogenMode mHydrogenMode = HydrogenMode.LETTERING_H;
    private AtomDecoration mAtomDecoration = new AtomDecoration();

    private Bond findBond(Atom atom) {
        for (Bond bond : mBonds) {
            if (bond.hasBondTo(atom)) {
                return bond;
            }
        }
        return null;
    }

    private void showAllHydrogenElementName(boolean show) {
        for (Bond bond : mBonds) {
            Atom that_atom = bond.getBoundAtom();

            if (that_atom.getAtomicNumber() == AtomicNumber.H) {
                that_atom.getAtomDecoration().setShowElementName(show);
            }
        }
    }

    public Atom(int aid, AtomicNumber atomicNumber) {
        mAID = aid;
        setAtomicNumber(atomicNumber);
    }

    public Atom copy() {
        Atom copied = new Atom(mAID, mAtomicNumber);

        copied.setPoint(mPoint);
        copied.mArbitraryName = mArbitraryName;
        copied.mHydrogenMode = mHydrogenMode;
        copied.mAtomDecoration = mAtomDecoration.copy();

        return copied;
    }

    public int getAID() {
        return mAID;
    }

    public void setAID(int aid) {
        mAID = aid;
    }

    public AtomicNumber getAtomicNumber() {
        return mAtomicNumber;
    }

    public void setArbitraryName(String atomName) {
        mAtomicNumber = AtomicNumber.TEXT;
        mArbitraryName = atomName;
    }

    public String getArbitraryName() {
        return mArbitraryName;
    }

    public void setBond(Atom atom, Bond.BondType bondType) {
        Bond bond = findBond(atom);

        if (bond == null) {
            mBonds.add(new Bond(atom, bondType));
            atom.setBond(this, bondType);
        } else if (bond.getBondType() != bondType) {
            bond.setBondType(bondType);
            atom.setBond(this, bondType);
        }
    }

    public void singleBond(Atom atom) {
        setBond(atom, Bond.BondType.SINGLE);
    }

    public void doubleBond(Atom atom) {
        setBond(atom, Bond.BondType.DOUBLE);
    }

    public void tripleBond(Atom atom) {
        setBond(atom, Bond.BondType.TRIPLE);
    }

    public void setPoint(PointF point) {
        mPoint.set(point);
    }

    public void setPoint(float x, float y) {
        mPoint.set(x, y);
    }

    public PointF getPoint() {
        return mPoint;
    }

    public boolean cutBond(Atom atom) {
        for (Iterator<Bond> it = mBonds.iterator(); it.hasNext(); ) {
            Bond bond = it.next();

            if (bond.hasBondTo(atom)) {
                it.remove();
                atom.cutBond(this);
                return true;
            }
        }
        return false;
    }

    public Atom getBoundAtomExcept(Atom atom) {
        if (mBonds.size() == 2) {
            for (Bond bond : mBonds) {
                if (bond.getBoundAtom() != atom) {
                    return bond.getBoundAtom();
                }
            }
        } else {
            // TODO: this case needs to be addressed.
        }
        return null;
    }

    public List<Bond> getBonds() {
        return Collections.unmodifiableList(mBonds);
    }

    public int bondNumber() {
        return mBonds.size();
    }

    public int bondNumber(AtomicNumber atomicNumber) {
        int cnt = 0;

        for (Bond bond : mBonds) {
            if (bond.getBoundAtom().getAtomicNumber() == atomicNumber)
                ++cnt;
        }

        return cnt;
    }

    public void cycleBond(Atom atom) {
        Bond bond = findBond(atom);

        if (bond != null) {
            bond.cycleBond();
        }
    }

    public void offset(float dx, float dy) {
        mPoint.offset(dx, dy);
    }

    public void setAtomicNumber(AtomicNumber ele) {
        mAtomicNumber = ele;
    }

    public Bond.BondType getBondType(Atom atom) {
        Bond bond = findBond(atom);

        if (bond != null) {
            return bond.getBondType();
        }
        return Bond.BondType.NONE;
    }

    public void cycleHydrogenMode() {
        setHydrogenMode(HydrogenMode.values()[(mHydrogenMode.ordinal() + 1) % HydrogenMode.values().length]);
    }

    public void setHydrogenMode(HydrogenMode hydrogenMode) {
        mHydrogenMode = hydrogenMode;
        if (mHydrogenMode == HydrogenMode.LETTERING_H) {
            mAtomDecoration.setShowElementName(true);
            showAllHydrogenElementName(false);
        } else if (mHydrogenMode == HydrogenMode.SHOW_H_BOND) {
            showAllHydrogenElementName(true);
            mAtomDecoration.setShowElementName(mAtomicNumber != AtomicNumber.C);
        } else {
            showAllHydrogenElementName(false);
            mAtomDecoration.setShowElementName(mAtomicNumber != AtomicNumber.C);
        }
    }

    public HydrogenMode getHydrogenMode() {
        return mHydrogenMode;
    }

    public boolean isVisible() {
        // returns whether the Atom is visible on the screen. Only H may be invisible
        // though AtomDecoration.ShowElementName returns false, the atom can be visible such as C
        if (getAtomicNumber() == AtomicNumber.H && bondNumber() == 1) {
            Atom boundAtom = getBonds().get(0).getBoundAtom();

            return boundAtom.getHydrogenMode() == Atom.HydrogenMode.SHOW_H_BOND;
        }
        return true;
    }

    public void preSerialization() {
        for (Bond bond : mBonds) {
            bond.preSerialization();
        }
    }

    public void postDeserialization(Compound compound) {
        for (Bond bond : mBonds) {
            bond.postDeserialization(compound);
        }
    }

    public AtomDecoration getAtomDecoration() {
        return mAtomDecoration;
    }

    public void setAtomDecoration(AtomDecoration atomDecoration) {
        mAtomDecoration = atomDecoration;
    }
}
