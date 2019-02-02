package com.coldradio.benzene.compound;

import android.graphics.PointF;
import android.graphics.RectF;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Compound {
    private List<Atom> mAtoms;
    private PointF mCenterOfRectangle;
    private RectF mRegion = new RectF();
    private short mCID;
    private static short msCIDGen;

    private void resetAID() {
        int aid = 1;

        for (Atom atom : mAtoms) {
            atom.setAID(aid++);
        }
    }

    public Compound() {
        mAtoms = new ArrayList<>();
        mCID = msCIDGen++;
    }

    public Compound(List<Atom> atoms) {
        mAtoms = atoms;
        mCID = msCIDGen++;
    }

    public Compound(int[] aids, AtomicNumber[] atomicNumber) {
        mAtoms = new ArrayList<>();

        for (int ii = 0; ii < aids.length; ++ii) {
            mAtoms.add(new Atom(aids[ii], atomicNumber[ii]));
        }
        mCID = msCIDGen++;
    }

    public Compound copy() {
        Compound that_compound = new Compound();
        that_compound.mCID = this.mCID;

        for (Atom this_atom : mAtoms) {
            that_compound.mAtoms.add(this_atom.copy());
        }
        that_compound.resetAID();
        that_compound.mCenterOfRectangle = null;    // this will recalculated when needed

        // now copy bonds
        resetAID(); // at this point, AIDs of that_compound and this_compound are aligned
        for (int from_aid = 1; from_aid <= size(); ++from_aid) {
            Atom from_atom = mAtoms.get(from_aid - 1);

            for (Bond bond : from_atom.getBonds()) {
                that_compound.makeBond(from_aid, bond.getBoundAtom().getAID(), bond.getBondType(), bond.getBondAnnotation());
            }
        }

        return that_compound;
    }

    public Compound copyAsNew() {
        Compound copied = copy();

        copied.mCID = msCIDGen++;

        return copied;
    }

    public Atom getAtom(int aid) {
        for (Atom atom : mAtoms) {
            if (atom.getAID() == aid) return atom;
        }
        return null;
    }

    public short getID() {
        return mCID;
    }

    public int size() {
        return mAtoms.size();
    }

    public Compound offset(float x, float y) {
        for (Atom atom : mAtoms) {
            atom.offset(x, y);
        }
        mCenterOfRectangle = null;

        return this;
    }

    public List<Atom> getAtoms() {
        return Collections.unmodifiableList(mAtoms);
    }

    public PointF centerOfRectangle() {
        if (mCenterOfRectangle == null) {
            RectF region = rectRegion();

            mCenterOfRectangle = new PointF(region.centerX(), region.centerY());
        }
        return mCenterOfRectangle;
    }

    public RectF rectRegion() {
        // TODO update only when adding or removing Atom
        mRegion.set(10e10f, 10e10f, -10e10f, -10e10f);

        for (Atom atom : mAtoms) {
            PointF p = atom.getPoint();

            mRegion.left = Math.min(mRegion.left, p.x);
            mRegion.top = Math.min(mRegion.top, p.y);
            mRegion.right = Math.max(mRegion.right, p.x);
            mRegion.bottom = Math.max(mRegion.bottom, p.y);
        }

        if (mRegion.left == 10e10f) {   // when mAtoms is empty. Bug if it really happens
            mRegion.set(0, 0, 0, 0);
        }

        return mRegion;
    }

    public boolean cycleBondType(Edge edge) {
        edge.first.cycleBond(edge.second);
        edge.second.cycleBond(edge.first);
        return false;
    }

    public void makeBond(int aid1, int aid2, Bond.BondType bondType) {
        makeBond(aid1, aid2, bondType, Bond.BondAnnotation.NONE);
    }

    public void makeBond(int aid1, int aid2, Bond.BondType bondType, Bond.BondAnnotation bondAnnotation) {
        Atom a1 = getAtom(aid1), a2 = getAtom(aid2);

        if (a1 != null & a2 != null) {
            a1.setBond(a2, bondType);
            a1.setBondAnnotation(a2, bondAnnotation);
        }
    }

    public void setBondAnnotation(int aidFrom, int aidTo, Bond.BondAnnotation bondAnnotation) {
        Atom a1 = getAtom(aidFrom), a2 = getAtom(aidTo);

        if (a1 != null) {
            a1.setBondAnnotation(a2, bondAnnotation);
        }
    }

    public void removeAtom(Atom atom) {
        mAtoms.remove(atom);
    }

    public void preSerialization() {
        resetAID();

        for (Atom atom : mAtoms) {
            atom.preSerialization();
        }
    }

    public void postDeserialization() {
        for (Atom atom : mAtoms) {
            atom.postDeserialization(this);
        }
    }

    public void addAtom(Atom to, Bond.BondType bondType, Atom newAtom) {
        mAtoms.add(newAtom);
        to.setBond(newAtom, bondType);
        mCenterOfRectangle = null;
    }

    public void addAtom(Atom atom) {
        mAtoms.add(atom);
        mCenterOfRectangle = null;
    }

    public void addAtoms(Compound compound) {
        mAtoms.addAll(compound.getAtoms());
        mCenterOfRectangle = null;
    }

    public void positionModified() {
        mCenterOfRectangle = null;
    }
}