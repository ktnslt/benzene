package com.coldradio.benzene.compound;

import android.gesture.GestureLibraries;
import android.graphics.PointF;
import android.graphics.RectF;

import com.coldradio.benzene.geometry.Geometry;
import com.coldradio.benzene.project.Project;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Compound {
    protected List<Atom> mAtoms;

    public Compound() {
        mAtoms = new ArrayList<>();
    }

    public Compound(List<Atom> atoms) {
        mAtoms = atoms;
    }

    public int size() {
        return mAtoms.size();
    }

    public void offset(float x, float y) {
        for (Atom atom : mAtoms) {
            atom.offset(x, y);
        }
    }

    public void fillCarbon(int carbonNumber) {
        mAtoms.clear();
        for (int ii = 0; ii < carbonNumber; ++ii) {
            mAtoms.add(new Carbon());
        }
    }

    public List<Atom> getAtoms() {
        return Collections.unmodifiableList(mAtoms);
    }

    public boolean isCyclo() {
        return mAtoms.get(0).getBondType(mAtoms.get(mAtoms.size() - 1)) != Bond.BondType.NONE;
    }

    public PointF center() {
        RectF region = rectRegion();

        return new PointF((region.left + region.right) / 2, (region.top + region.bottom) / 2);
    }

    public RectF rectRegion() {
        float left = (float) 10e10, top = (float) 10e10, right = 0, bottom = 0;

        for (Atom atom : mAtoms) {
            PointF p = atom.getPoint();

            left = Math.min(left, p.x);
            top = Math.min(top, p.y);
            right = Math.max(right, p.x);
            bottom = Math.max(bottom, p.y);
        }

        return new RectF(left, top, right, bottom);
    }

    public Compound decomposition(float x, float y) {
        Atom[] edge = Geometry.selectedEdge(x, y, this);

        if (edge != null) {
//            Compound cutCompound = new Compound(new ArrayList<>(mAtoms.subList(leftSelectedIndex + 1, mAtoms.size())));
//
//            mAtoms.subList(leftSelectedIndex + 1, mAtoms.size()).clear();
//            mAtoms.get(leftSelectedIndex).cutBond(cutCompound.mAtoms.get(0));
            return null;//cutCompound;
        } else {
            return null;
        }
    }

    public boolean cycleBondType(float x, float y) {
        Atom[] edge = Geometry.selectedEdge(x, y, this);

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
    }

    public void rotate(float degree) {
        PointF center = Geometry.centerOfCompound(this);

        for (Atom atom : mAtoms) {
            atom.setPoint(Geometry.rotatePointByDegree(atom.getPoint(), center, degree));
        }
    }
}
