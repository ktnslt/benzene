package com.coldradio.benzene.compound;

import android.graphics.PointF;

import com.coldradio.benzene.lib.AtomicNumber;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

public class Atom {
    public enum Marker {
        NONE, LEFT_TOP, RIGHT_TOP, LEFT_BOTTOM, RIGHT_BOTTOM
    }
    public enum HydrogenMode {
        LETTERING_H, HIDE_H_BOND, SHOW_H_BOND
    }

    private PointF mPoint = new PointF();
    private List<Bond> mBonds = new ArrayList<>();
    private int mAID;   // AID is only valid in CompoundLibrary during initial setup. When merging two compounds into one, the uniqueness will be broken
    private AtomicNumber mAtomicNumber;
    private Marker mMarker = Marker.NONE;
    private HydrogenMode mHydrogenMode = HydrogenMode.LETTERING_H;

    private Bond findBond(Atom atom) {
        for (Bond bond : mBonds) {
            if (bond.hasBondTo(atom)) {
                return bond;
            }
        }
        return null;
    }

    public Atom(int aid, AtomicNumber atomicNumber) {
        mAID = aid;
        mAtomicNumber = atomicNumber;

        if (mAtomicNumber == AtomicNumber.C) {
            mHydrogenMode = HydrogenMode.HIDE_H_BOND;
        }
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

    public void markWithStar() {
        mMarker = Marker.values()[(mMarker.ordinal() + 1) % Marker.values().length];
    }

    public Marker getMarker() {
        return mMarker;
    }

    public Bond.BondType getBondType(Atom atom) {
        Bond bond = findBond(atom);

        if (bond != null) {
            return bond.getBondType();
        }
        return Bond.BondType.NONE;
    }

    public boolean isHydrogenBoundTo(AtomicNumber c) {
        return mAtomicNumber == AtomicNumber.H && mBonds.size() == 1 && mBonds.get(0).getBoundAtom().getAtomicNumber() == c;
    }

    public void cycleHydrogenMode() {
        mHydrogenMode = HydrogenMode.values()[(mHydrogenMode.ordinal() + 1) % HydrogenMode.values().length];
    }

    public HydrogenMode getHydrogenMode() {
        return mHydrogenMode;
    }
}
