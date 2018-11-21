package com.coldradio.benzene.compound;

import android.graphics.PointF;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

public class Atom {
    public enum Marker {
        NONE, MARKER_1, MARKER_2, MARKER_3, MARKER_4, MARKER_5, MARKER_6, MARKER_7, MARKER_8
    }
    public enum HydrogenMode {
        LETTERING_H, HIDE_H_BOND, SHOW_H_BOND
    }
    public enum UnsharedElectron {
        NONE, SINGLE, DOUBLE
    }
    public enum Direction {
        TOP, BOTTOM, LEFT, RIGHT
    }

    private PointF mPoint = new PointF();
    private List<Bond> mBonds = new ArrayList<>();
    private int mAID;   // AID is valid only temporary. When merging two compounds into one, the uniqueness will be broken
    private AtomicNumber mAtomicNumber;
    private String mArbitraryName;
    private Marker mMarker = Marker.NONE;
    private HydrogenMode mHydrogenMode = HydrogenMode.LETTERING_H;
    private UnsharedElectron[] mUnsharedElectron = new UnsharedElectron[4];
    private int mCharge;
    private boolean mShowElement;

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
        } else {
            mShowElement = true;
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

    public void setMarker(Marker marker) {
        mMarker = marker;
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

    public boolean isSelectable() {
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

    public UnsharedElectron getUnsharedElectron(Direction direction) {
        return mUnsharedElectron[direction.ordinal()];
    }

    public void setUnsharedElectron(Direction direction, UnsharedElectron unsharedElectron) {
        mUnsharedElectron[direction.ordinal()] = unsharedElectron;
    }

    public void setCharge(int charge) {
        mCharge = charge;
    }

    public int getCharge() {
        return mCharge;
    }

    public boolean showElement() {
        return mShowElement;
    }

    public void setShowElement(boolean showElement) {
        mShowElement = showElement;
    }
}
