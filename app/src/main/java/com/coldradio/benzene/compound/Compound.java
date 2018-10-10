package com.coldradio.benzene.compound;

import android.graphics.PointF;
import android.graphics.RectF;

import com.coldradio.benzene.lib.AtomicNumber;
import com.coldradio.benzene.lib.Geometry;
import com.coldradio.benzene.project.Project;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Compound {
    protected List<Atom> mAtoms;
    private PointF mCenterOfRectangle;

    private void resetAID() {
        int aid = 1;

        for (Atom atom : mAtoms) {
            atom.setAID(aid++);
        }
    }

    public Compound() {
        mAtoms = new ArrayList<>();
    }

    public Compound(List<Atom> atoms) {
        mAtoms = atoms;
    }

    public Compound(int[] aids, AtomicNumber[] atomicNumber) {
        mAtoms = new ArrayList<>();

        for (int ii = 0; ii < aids.length; ++ii) {
            if (atomicNumber[ii] != AtomicNumber.H) {
                mAtoms.add(new Atom(aids[ii], atomicNumber[ii]));
            }
        }
    }

    public Compound copy() {
        Compound that_compound = new Compound();
        int this_aid = 1;

        for (Atom this_atom : mAtoms) {
            Atom that_atom = new Atom(this_aid++, this_atom.getAtomicNumber());

            that_atom.setPoint(this_atom.getPoint());
            that_compound.mAtoms.add(that_atom);
        }

        that_compound.mCenterOfRectangle = this.mCenterOfRectangle;

        // now copy bonds
        resetAID();
        for (int from_aid = 1; from_aid <= size(); ++from_aid) {
            Atom from_atom = mAtoms.get(from_aid - 1);

            for (Bond bond : from_atom.getBonds()) {
                that_compound.makeBond(from_aid, bond.getBoundAtom().getAID(), bond.getBondType());
            }
        }

        return that_compound;
    }

    public Atom getAtom(int aid) {
        for (Atom atom : mAtoms) {
            if (atom.getAID() == aid) return atom;
        }
        return null;
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

    public void fillCarbon(int carbonNumber) {
        mAtoms.clear();
        for (int ii = 0; ii < carbonNumber; ++ii) {
            mAtoms.add(new Atom(ii + 1, AtomicNumber.C));
        }
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
        float left = (float) 10e10, top = (float) 10e10, right = (float) -10e10, bottom = (float) -10e10;

        for (Atom atom : mAtoms) {
            PointF p = atom.getPoint();

            left = Math.min(left, p.x);
            top = Math.min(top, p.y);
            right = Math.max(right, p.x);
            bottom = Math.max(bottom, p.y);
        }

        return new RectF(left, top, right, bottom);
    }

    public Compound decomposition(PointF point) {
        return null;
//        Atom[] edge = selectedEdge(point);
//
//        if (edge != null) {
//// Compound cutCompound = new Compound(new ArrayList<>(mAtoms.subList(leftSelectedIndex + 1, mAtoms.size())));
////
//// mAtoms.subList(leftSelectedIndex + 1, mAtoms.size()).clear();
//// mAtoms.get(leftSelectedIndex).cutBond(cutCompound.mAtoms.get(0));
//            return null;//cutCompound;
//        } else {
//            return null;
//        }
    }

    public boolean cycleBondType(PointF point) {
//        Atom[] edge = selectedEdge(point);
//
//        if (edge != null) {
//            edge[0].cycleBond(edge[1]);
//            edge[1].cycleBond(edge[0]);
//            return true;
//        }
        return false;
    }

    public void merge(Compound compound) {
        mAtoms.addAll(compound.getAtoms());
        Project.instance().removeCompound(compound);
        mCenterOfRectangle = null;
    }

    public void rotate(float angle) {
        for (Atom atom : mAtoms) {
            atom.setPoint(Geometry.rotatePoint(atom.getPoint(), centerOfRectangle(), angle));
        }
    }

    public void makeBond(int aid1, int aid2, Bond.BondType bondType) {
        Atom a1 = getAtom(aid1), a2 = getAtom(aid2);

        if (a1 != null & a2 != null) {
            a1.setBond(a2, bondType);
        }
    }
}