package com.coldradio.benzene.project;

import android.graphics.PointF;
import android.util.Pair;
import android.view.MotionEvent;

import com.coldradio.benzene.compound.Atom;
import com.coldradio.benzene.compound.Compound;
import com.coldradio.benzene.compound.CompoundArranger;
import com.coldradio.benzene.compound.Edge;
import com.coldradio.benzene.util.Geometry;
import com.coldradio.benzene.util.MutablePair;
import com.coldradio.benzene.util.TreeTraveler;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ElementSelector {
    public enum Selection {
        NONE, ATOM, EDGE, COMPOUND, PARTIAL
    }

    private Selection mSelection = Selection.NONE;
    private Atom mSelectedAtom;
    private Edge mSelectedEdge;
    private Compound mSelectedCompound;     // the selected compound or the compound that the selected Atom belongs to
    private PointF mRotationPivotPoint = new PointF();
    private boolean mIsRotating = false;
    private IRegionSelector mRegionSelector;
    private List<Atom> mSelectedAtomList = new ArrayList<>();
    private boolean mSelectionCancelled;

    private static Pair<Object, Float> selectEdge(PointF point, Compound compound) {
        MutablePair<Object, Float> selectedEdge = MutablePair.create(null, (float) Configuration.SELECT_RANGE);

        TreeTraveler.returnFirstEdge(new TreeTraveler.IEdgeVisitor() {
            @Override
            public boolean visit(Atom a1, Atom a2, Object... args) {
                if (a1.isVisible() && a2.isVisible()) {
                    MutablePair<Object, Float> selectedEdge = (MutablePair<Object, Float>) args[1];
                    float edgeCenterToTouchedPoint = Geometry.distanceFromPointToPoint((PointF) args[0], Geometry.centerOfLine(a1.getPoint(), a2.getPoint()));

                    if (edgeCenterToTouchedPoint < Configuration.SELECT_RANGE && edgeCenterToTouchedPoint < selectedEdge.second) {
                        selectedEdge.first = new Edge(a1, a2);
                        selectedEdge.second = edgeCenterToTouchedPoint;
                    }
                }
                return false;
            }
        }, compound, point, selectedEdge);

        return selectedEdge.first == null ? null : selectedEdge.toPair();
    }

    private static Pair<Object, Float> selectAtom(PointF point, Compound compound) {
        float minDistance = Configuration.SELECT_RANGE * 2;
        Atom minAtom = null;

        for (Atom atom : compound.getAtoms()) {
            float distance = Geometry.distanceFromPointToPoint(atom.getPoint(), point);

            if (atom.isVisible() && distance < Configuration.SELECT_RANGE && distance < minDistance) {
                minDistance = distance;
                minAtom = atom;
            }
        }

        return minAtom == null ? null : Pair.create((Object) minAtom, minDistance);
    }

    private void rotateToPoint(PointF point) {
        float angle = Geometry.angle(mRotationPivotPoint, point, mSelectedCompound.centerOfRectangle());

        CompoundArranger.rotateByCenterOfRectangle(mSelectedCompound, angle);
        mRotationPivotPoint = Geometry.cwRotate(mRotationPivotPoint, mSelectedCompound.centerOfRectangle(), angle);
    }

    private boolean isPivotGrasped(PointF point) {
        return Geometry.distanceFromPointToPoint(mRotationPivotPoint, point) < Configuration.ROTATION_PIVOT_SIZE;
    }

    private void updateSelectedAtoms() {
        if (selection() == Selection.PARTIAL) {
            mSelectedAtomList = mRegionSelector.getSelectedAtoms();
        } else if (selection() == Selection.ATOM) {
            mSelectedAtomList.clear();
            mSelectedAtomList.add(mSelectedAtom);
        } else if (selection() == Selection.EDGE) {
            mSelectedAtomList.clear();
            mSelectedAtomList.add(mSelectedEdge.first);
            mSelectedAtomList.add(mSelectedEdge.second);
        }
    }

    public static Pair<Object, Compound> getSelectedElement(PointF point) {
        Pair<Object, Float> selectedElement = Pair.create(null, (float) Configuration.SELECT_RANGE);
        Compound selectedCompound = null;

        for (Compound compound : Project.instance().getCompounds()) {
            Pair<Object, Float> candidate = selectAtom(point, compound);

            if (candidate != null && candidate.second < selectedElement.second) {
                selectedElement = candidate;
                selectedCompound = compound;
            }

            candidate = selectEdge(point, compound);

            if (candidate != null && candidate.second < selectedElement.second) {
                selectedElement = candidate;
                selectedCompound = compound;
            }
        }
        return selectedCompound == null ? null : Pair.create(selectedElement.first, selectedCompound);
    }

    public void selectCompound(Compound compound) {
        mSelectedCompound = compound;
        mSelection = Selection.COMPOUND;
        mRotationPivotPoint.set(compound.centerOfRectangle());
        mRotationPivotPoint.offset(0, -200);
    }

    public boolean select(PointF point) {
        Pair<Object, Compound> selected = getSelectedElement(point);

        if (selected != null && selected.first instanceof Atom) {
            Atom selectedAtom = (Atom) selected.first;
            if (mSelection == Selection.ATOM && selectedAtom == mSelectedAtom) {
                // selecting the same Atom
                selectCompound(selected.second);
            } else {
                mSelectedAtom = selectedAtom;
                mSelection = Selection.ATOM;
                mSelectedCompound = selected.second;
            }
        } else if (selected != null && selected.first instanceof Edge) {
            Edge selectedEdge = (Edge) selected.first;
            if (mSelection == Selection.EDGE && selectedEdge.equals(mSelectedEdge)) {
                // selecting the same Edge
                selectCompound(selected.second);
            } else {
                mSelectedEdge = selectedEdge;
                mSelection = Selection.EDGE;
                mSelectedCompound = selected.second;
            }
        } else {
            reset();
            return false;
        }

        updateSelectedAtoms();
        return true;
    }

    public boolean canSelectAny(PointF point) {
        return getSelectedElement(point) != null;
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

    public List<Atom> getSelectedAsList() {
        if (mSelection == Selection.COMPOUND) {
            return mSelectedCompound.getAtoms();
        } else {
            return Collections.unmodifiableList(mSelectedAtomList);
        }
    }

    public boolean moveSelectedElement(PointF distance) {
        if (mSelection == Selection.COMPOUND) {
            mRotationPivotPoint.offset(distance.x, distance.y);
            mSelectedCompound.offset(distance.x, distance.y);
        } else if (mSelection != Selection.NONE) {
            for (Atom atom : mSelectedAtomList) {
                CompoundArranger.offsetWithHiddenHydrogen(atom, distance.x, distance.y);
            }
        } else {
            return false;
        }
        return true;
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

    public boolean isSelected(Atom atom) {
        return mSelectedAtomList.contains(atom);
    }

    public void reset() {
        mSelection = Selection.NONE;
        mRegionSelector = null;
        mSelectedAtomList.clear();
    }

    public boolean onTouchEvent(PointF point, int touchAction) {
        boolean ret = false;

        if (mRegionSelector != null) {
            ret = mRegionSelector.onTouchEvent(point, touchAction);
            mSelectionCancelled = mRegionSelector.canceled();

            if (mSelectionCancelled) {
                reset();
            } else if (ret) {
                updateSelectedAtoms();
            }
        }

        return ret;
    }

    public void setRegionSelector(IRegionSelector regionSelector) {
        mRegionSelector = regionSelector;
        if (mRegionSelector != null) {
            mSelection = Selection.PARTIAL;
            updateSelectedAtoms();
        } else {
            reset();
        }
    }

    public IRegionSelector getRegionSelector() {
        return mRegionSelector;
    }

    public boolean selectionCancelled() {
        return mSelectionCancelled;
    }
}
