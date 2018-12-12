package com.coldradio.benzene.project;

import android.graphics.PointF;
import android.view.MotionEvent;

import com.coldradio.benzene.compound.Atom;
import com.coldradio.benzene.compound.Compound;
import com.coldradio.benzene.compound.CompoundArranger;
import com.coldradio.benzene.compound.Edge;
import com.coldradio.benzene.util.Geometry;
import com.coldradio.benzene.util.TreeTraveler;

import java.util.List;

public class ElementSelector {
    public enum Selection {
        NONE, ATOM, EDGE, COMPOUND
    }

    private Selection mSelection = Selection.NONE;
    private Atom mSelectedAtom;
    private Edge mSelectedEdge;
    private Compound mSelectedCompound;     // the selected compound or the compound that the selected Atom belongs to
    private PointF mRotationPivotPoint = new PointF();
    private boolean mIsRotating = false;

    private Edge selectEdge(PointF point, Compound compound) {
        return TreeTraveler.returnFirstEdge(new TreeTraveler.IEdgeVisitor() {
            @Override
            public boolean visit(Atom a1, Atom a2, Object... args) {
                if (!a1.isVisible() || !a2.isVisible())
                    return false;
                PointF p1 = a1.getPoint(), p2 = a2.getPoint(), touchedPoint = (PointF) args[0];
                float lineLength = Geometry.distanceFromPointToPoint(p1, p2);

                return Geometry.distanceFromPointToLine(touchedPoint, p1, p2) < Configuration.SELECT_RANGE
                        && Geometry.distanceFromPointToPoint(touchedPoint, p1) < (lineLength + Configuration.SELECT_RANGE)
                        && Geometry.distanceFromPointToPoint(touchedPoint, p2) < (lineLength + Configuration.SELECT_RANGE);
            }
        }, compound, point);
    }

    private Atom selectAtom(PointF point, Compound compound) {
        for (Atom atom : compound.getAtoms()) {
            if (atom.isVisible() && Geometry.distanceFromPointToPoint(atom.getPoint(), point) < Configuration.SELECT_RANGE) {
                return atom;
            }
        }
        return null;
    }

    private void rotateToPoint(PointF point) {
        float angle = Geometry.angle(mRotationPivotPoint, point, mSelectedCompound.centerOfRectangle());

        CompoundArranger.rotateByCenterOfRectangle(mSelectedCompound, angle);
        mRotationPivotPoint = Geometry.cwRotate(mRotationPivotPoint, mSelectedCompound.centerOfRectangle(), angle);
    }

    private boolean isPivotGrasped(PointF point) {
        return Geometry.distanceFromPointToPoint(mRotationPivotPoint, point) < Configuration.ROTATION_PIVOT_SIZE;
    }

    public void selectCompound(Compound compound) {
        mSelectedCompound = compound;
        mSelection = Selection.COMPOUND;
        mRotationPivotPoint.set(compound.centerOfRectangle());
        mRotationPivotPoint.offset(0, -200);
    }

    public boolean select(PointF point, List<Compound> compoundList) {
        for (Compound compound : compoundList) {
            // try to select Atom first
            Atom atom = selectAtom(point, compound);
            mSelectedCompound = compound;

            if (atom != null) {
                if (mSelection == Selection.ATOM && atom == mSelectedAtom) {
                    // selecting the same Atom
                    selectCompound(compound);
                } else {
                    mSelectedAtom = atom;
                    mSelection = Selection.ATOM;
                }
                return true;
            }
            // try to select Edge next
            Edge edge = selectEdge(point, compound);

            if (edge != null) {
                if (mSelection == Selection.EDGE && edge.equals(mSelectedEdge)) {
                    // selecting the same Edge
                    selectCompound(compound);
                } else {
                    mSelectedEdge = edge;
                    mSelection = Selection.EDGE;
                }
                return true;
            }
        }
        mSelection = Selection.NONE;
        return false;
    }

    public boolean tryToSelect(PointF point, List<Compound> compoundList) {
        for (Compound compound : compoundList) {
            if (selectAtom(point, compound) != null || selectEdge(point, compound) != null) {
                return true;
            }
        }

        return false;
    }

    public PointF getRotationPivotPoint() {
        return mRotationPivotPoint;
    }

    public Compound getSelectedCompound() {
        // In case that Atom or Edge is selected, this returns the Compound that contains it.
        return mSelectedCompound;
    }

    public Edge getSelectedEdge() {
        return mSelectedEdge;
    }

    public Atom getSelectedAtom() {
        return mSelectedAtom;
    }

    public boolean moveSelectedElement(PointF distance) {
        // TODO: shall move both the Atom and Edge or anything selected
        if (mSelection == Selection.COMPOUND) {
            mRotationPivotPoint.offset(distance.x, distance.y);
            mSelectedCompound.offset(distance.x, distance.y);
            return true;
        } else {
            return false;
        }
    }

    public boolean rotateSelectedCompound(PointF point, int action) {
        if (mSelection != Selection.COMPOUND)
            return false;

        if (action == MotionEvent.ACTION_DOWN && isPivotGrasped(point)) {
            mIsRotating = true;
        } else if (action == MotionEvent.ACTION_MOVE && mIsRotating) {
            rotateToPoint(point);
        } else if (action == MotionEvent.ACTION_UP && mIsRotating) {
            mIsRotating = false;
        } else {
            return false;
        }
        return true;
    }

    public Selection selection() {
        return mSelection;
    }

    public boolean hasSelectedElement() {
        return mSelection != Selection.NONE;
    }

    public void reset() {
        mSelection = Selection.NONE;
    }
}
