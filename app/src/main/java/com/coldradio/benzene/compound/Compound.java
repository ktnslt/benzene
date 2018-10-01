package com.coldradio.benzene.compound;

import android.graphics.PointF;
import android.graphics.RectF;

import com.coldradio.benzene.lib.AtomicNumber;
import com.coldradio.benzene.lib.Geometry;
import com.coldradio.benzene.lib.TreeTraveler;
import com.coldradio.benzene.project.Configuration;
import com.coldradio.benzene.project.Project;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Compound {
    protected List<Atom> mAtoms;
    private PointF mCenterOfRectangle;

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

    public Atom getAtom(int aid) {
        for (Atom atom : mAtoms) {
            if (atom.getAID() == aid) return atom;
        }
        return null;
    }

    public int size() {
        return mAtoms.size();
    }

    public void offset(float x, float y) {
        for (Atom atom : mAtoms) {
            atom.offset(x, y);
        }
        mCenterOfRectangle = null;
    }

    public void fillCarbon(int carbonNumber) {
        mAtoms.clear();
        for (int ii = 0; ii < carbonNumber; ++ii) {
            mAtoms.add(new Atom(ii+1, AtomicNumber.C));
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
        Atom[] edge = selectedEdge(point);

        if (edge != null) {
// Compound cutCompound = new Compound(new ArrayList<>(mAtoms.subList(leftSelectedIndex + 1, mAtoms.size())));
//
// mAtoms.subList(leftSelectedIndex + 1, mAtoms.size()).clear();
// mAtoms.get(leftSelectedIndex).cutBond(cutCompound.mAtoms.get(0));
            return null;//cutCompound;
        } else {
            return null;
        }
    }

    public boolean cycleBondType(PointF point) {
        Atom[] edge = selectedEdge(point);

        if (edge != null) {
            edge[0].cycleBond(edge[1]);
            edge[1].cycleBond(edge[0]);
            return true;
        }
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

    public Atom[] selectedEdge(PointF point) {
        return TreeTraveler.returnFirstEdge(new TreeTraveler.IEdgeVisitor() {
            @Override
            public boolean visit(Atom a1, Atom a2, Object... args) {
                PointF p1 = a1.getPoint(), p2 = a2.getPoint(), touchedPoint = (PointF) args[0];
                float lineLength = Geometry.distanceFromPointToPoint(p1, p2);

                if (Geometry.distanceFromPointToLine(touchedPoint, p1, p2) < Configuration.SELECT_RANGE
                        && Geometry.distanceFromPointToPoint(touchedPoint, p1) < (lineLength + Configuration.SELECT_RANGE)
                        && Geometry.distanceFromPointToPoint(touchedPoint, p2) < (lineLength + Configuration.SELECT_RANGE)) {
                    return true;
                } else {
                    return false;
                }
            }
        }, this, point);
    }

    public Atom selectAtom(PointF point) {
        for (Atom atom : mAtoms) {
            if (Geometry.distanceFromPointToPoint(atom.getPoint(), point) < Configuration.SELECT_RANGE) {
                return atom;
            }
        }
        return null;
    }

    public boolean isSelectable(PointF point) {
        return selectedEdge(point) != null;
    }

    public void makeBond(int aid1, int aid2, Bond.BondType bondType) {
        Atom a1 = getAtom(aid1), a2 = getAtom(aid2);

        if (a1 != null & a2 != null) {
            a1.setBond(a2, bondType);
        }
    }
}