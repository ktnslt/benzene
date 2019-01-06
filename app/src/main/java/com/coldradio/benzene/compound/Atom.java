package com.coldradio.benzene.compound;

import android.graphics.PointF;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

public class Atom {
    private PointF mPoint = new PointF();
    private List<Bond> mBonds = new ArrayList<>();
    private int mAID;   // AID is valid only temporary. When merging two compounds into one, the uniqueness will be broken
    private AtomicNumber mAtomicNumber;
    private String mArbitraryName;
    private AtomDecoration mAtomDecoration = new AtomDecoration();

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
        setAtomicNumber(atomicNumber);
    }

    public Atom copy() {
        Atom copied = new Atom(mAID, mAtomicNumber);

        copied.setPoint(mPoint);
        copied.mArbitraryName = mArbitraryName;
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

    public void setBondAnnotation(Atom atom, Bond.BondAnnotation bondAnnotation) {
        Bond bond = findBond(atom);

        if (bond != null && bond.getBondType() == Bond.BondType.SINGLE) {
            bond.setBondAnnotation(bondAnnotation);
        }
    }

    public Bond.BondAnnotation getBondAnnotation(Atom atom) {
        Bond bond = findBond(atom);

        if (bond != null && bond.getBondType() == Bond.BondType.SINGLE) {
            return bond.getBondAnnotation();
        }
        return Bond.BondAnnotation.NONE;
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

    public boolean hasBondType(Bond.BondType bondType) {
        for (Bond bond : mBonds) {
            if (bond.getBondType() == bondType)
                return true;
        }
        return false;
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

    public Atom getSkeletonAtomExcept(Atom atom) {
        for (Bond bond : mBonds) {
            Atom boundAtom = bond.getBoundAtom();

            if (boundAtom != atom && boundAtom.getAtomicNumber() != AtomicNumber.H) {
                return boundAtom;
            }
        }

        return null;
    }

    public Atom getSkeletonAtom() {
        for (Bond bond : mBonds) {
            Atom boundAtom = bond.getBoundAtom();

            if (boundAtom.getAtomicNumber() != AtomicNumber.H) {
                return boundAtom;
            }
        }

        return null;
    }

    public Atom getHydrogen() {
        for (Bond bond : mBonds) {
            if (bond.getBoundAtom().getAtomicNumber() == AtomicNumber.H)
                return bond.getBoundAtom();
        }
        return null;
    }

    public List<Bond> getBonds() {
        return Collections.unmodifiableList(mBonds);
    }

    public int numberOfBonds() {
        return mBonds.size();
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

        if (ele == AtomicNumber.C) {
            mAtomDecoration.setShowElementName(false);
        } else if (ele != AtomicNumber.H) {
            mAtomDecoration.setShowElementName(true);
        }
    }

    public Bond.BondType getBondType(Atom atom) {
        Bond bond = findBond(atom);

        if (bond != null) {
            return bond.getBondType();
        }
        return Bond.BondType.NONE;
    }

    public boolean isVisible() {
        // returns whether the Atom is visible on the screen. Only H may be invisible
        // though AtomDecoration.ShowElementName returns false, the atom can be visible such as C
        // invisible atom cannot be selected by click
        if (getAtomicNumber() == AtomicNumber.H && numberOfBonds() == 1) {
            // TODO. when the parent is lettering, this atom may not be seen even for C
            return mAtomDecoration.getShowElementName();
        }
        return true;
    }

    public boolean isNameShown() {
        return mAtomDecoration.isLettering() || mAtomDecoration.getShowElementName();
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
