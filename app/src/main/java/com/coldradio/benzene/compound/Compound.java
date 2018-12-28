package com.coldradio.benzene.compound;

import android.graphics.PointF;
import android.graphics.RectF;

import com.coldradio.benzene.project.Project;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
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
            mAtoms.add(new Atom(aids[ii], atomicNumber[ii]));
        }
    }

    public Compound copy() {
        Compound that_compound = new Compound();

        for (Atom this_atom : mAtoms) {
            that_compound.mAtoms.add(this_atom.copy());
        }
        that_compound.resetAID();
        that_compound.mCenterOfRectangle = this.mCenterOfRectangle;

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

    public boolean cycleBondType(Edge edge) {
        edge.first.cycleBond(edge.second);
        edge.second.cycleBond(edge.first);
        return false;
    }

    public void merge(Compound compound) {
        mAtoms.addAll(compound.getAtoms());
        Project.instance().removeCompound(compound);
        mCenterOfRectangle = null;
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

    public void delete(Edge edge) {
        edge.first.cutBond(edge.second);
    }

    public void delete(Atom atom) {
        // cut all bonds
        for (Atom other : mAtoms) {
            if (other != atom) {
                other.cutBond(atom);
            }
        }
        // delete atom
        for (Iterator<Atom> it = mAtoms.iterator(); it.hasNext(); ) {
            if (it.next() == atom) {
                it.remove();
                break;
            }
        }
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
    }
}