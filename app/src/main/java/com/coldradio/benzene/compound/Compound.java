package com.coldradio.benzene.compound;

import android.graphics.PointF;
import android.graphics.RectF;
import android.util.Log;

import com.coldradio.benzene.lib.Geometry;
import com.coldradio.benzene.lib.TreeTraveler;
import com.coldradio.benzene.project.Configuration;
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

    public boolean isCyclc() {
        return mAtoms.get(0).getBondType(mAtoms.get(mAtoms.size() - 1)) != Bond.BondType.NONE;
    }

    public PointF centerOfAtoms() {
        PointF center = new PointF();

        for (Atom atom : mAtoms) {
            PointF atomPoint = atom.getPoint();

            center.x += atomPoint.x;
            center.y += atomPoint.y;
        }
        center.x /= mAtoms.size();
        center.y /= mAtoms.size();

        return center;
    }

    public PointF centerOfRectangle() {
        RectF region = rectRegion();

        return new PointF(region.centerX(), region.centerY());
    }

    public RectF rectRegion() {
        float left = (float) 10e10, top = (float) 10e10, right = (float)-10e10, bottom = (float)-10e10;

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
//            Compound cutCompound = new Compound(new ArrayList<>(mAtoms.subList(leftSelectedIndex + 1, mAtoms.size())));
//
//            mAtoms.subList(leftSelectedIndex + 1, mAtoms.size()).clear();
//            mAtoms.get(leftSelectedIndex).cutBond(cutCompound.mAtoms.get(0));
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
    }

    public float rotateToPoint(PointF point) {
        PointF center = centerOfRectangle();
        PointF rotationZeroPoint = new PointF(center.x, center.y - 300);
        float angle = 0;//Geometry.cwAngleFromPositiveXAxis(rotationZeroPoint, point, center);

        Log.d("++++", "Rotation Degree " + Math.toDegrees(angle) + " " + rotationZeroPoint.toString() + " " + point.toString() + " " + center.toString());

        for (Atom atom : mAtoms) {
            atom.setPoint(Geometry.rotatePoint(atom.getInitialPoint(), center, angle));
        }

        return angle;
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
}
