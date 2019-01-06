package com.coldradio.benzene.project;

import android.graphics.PointF;
import android.graphics.RectF;
import android.util.Pair;

import com.coldradio.benzene.compound.Atom;
import com.coldradio.benzene.compound.AtomicNumber;
import com.coldradio.benzene.compound.Bond;
import com.coldradio.benzene.compound.Compound;
import com.coldradio.benzene.compound.CompoundArranger;
import com.coldradio.benzene.compound.CompoundReactor;
import com.coldradio.benzene.compound.Edge;
import com.coldradio.benzene.util.Geometry;
import com.coldradio.benzene.util.Notifier;
import com.coldradio.benzene.util.TreeTraveler;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Project {
    private static final Project project = new Project();
    private List<Compound> mCompoundList = new ArrayList<>();
    private ElementSelector mElementSelector = new ElementSelector();
    private Compound mCopiedCompound;
    private ProjectFile mProjectFile;

    public static Project instance() {
        return project;
    }

    public List<Compound> getCompounds() {
        return Collections.unmodifiableList(mCompoundList);
    }

    public void addCompoundAsSelected(Compound compound) {
        mCompoundList.add(compound);
        mElementSelector.selectCompound(compound);
    }

    public void addCompounds(List<Compound> compounds) {
        mCompoundList.addAll(compounds);
    }

    public void addCyclicToSelectedBond(int edgeNumber, boolean oppositeSite, boolean deleteHydrogenBeforeAdd, boolean saturateWithHydrogen) {
        if (mElementSelector.selection() == ElementSelector.Selection.EDGE) {
            CompoundReactor.addCyclicToBond(mElementSelector.getSelectedCompound(), mElementSelector.getSelectedEdge(), edgeNumber, oppositeSite, deleteHydrogenBeforeAdd, saturateWithHydrogen);
        }
    }

    public ElementSelector getElementSelector() {
        return mElementSelector;
    }

    public boolean select(PointF point) {
        return mElementSelector.select(point);
    }

    public void removeCompound(Compound compound) {
        if (mElementSelector.getSelectedCompound() == compound) {
            mElementSelector.reset();
        }
        mCompoundList.remove(compound);
    }

    public Compound findCompound(Atom atom) {
        for (Compound compound : mCompoundList) {
            if (compound.getAtoms().contains(atom))
                return compound;
        }
        return null;
    }

    public boolean deleteSelectedElement() {
        switch (mElementSelector.selection()) {
            case ATOM:
                CompoundReactor.deleteAtom(mElementSelector.getSelectedCompound(), mElementSelector.getSelectedAtom());
                break;
            case EDGE:
                CompoundReactor.deleteBond(mElementSelector.getSelectedCompound(), mElementSelector.getSelectedEdge());
                break;
            case COMPOUND:
                removeCompound(mElementSelector.getSelectedCompound());
                break;
            case PARTIAL:
                CompoundReactor.deleteAtoms(mElementSelector.getSelectedAsList());
                break;
            default:
                return false;
        }
        mElementSelector.reset();

        return true;
    }

    public boolean cycleBondType() {
        if (mElementSelector.selection() == ElementSelector.Selection.EDGE) {
            mElementSelector.getSelectedCompound().cycleBondType(mElementSelector.getSelectedEdge());
            return true;
        } else {
            return false;
        }
    }

    public boolean synthesize(PointF point) {
        if (mElementSelector.selection() == ElementSelector.Selection.ATOM) {
            Pair<Object, Compound> toAtom = ElementSelector.getSelectedElement(point);

            if (toAtom != null && toAtom.first instanceof Atom) {
                CompoundReactor.makeBond(mElementSelector.getSelectedCompound(), mElementSelector.getSelectedAtom(), toAtom.second, (Atom) toAtom.first);
                return true;
            }
            Notifier.instance().notification("Atom shall be selected");
        }
        return false;
    }

    public PointF centerOfAllCompounds() {
        if (mCompoundList.size() > 0) {
            RectF allRegion = mCompoundList.get(0).rectRegion();

            for (Compound compound : mCompoundList) {
                RectF region = compound.rectRegion();

                allRegion.union(region);
            }

            return new PointF(allRegion.centerX(), allRegion.centerY());
        } else {
            return new PointF(0, 0); // TODO: return the center of the screen?
        }
    }

    public boolean rotateSelectedCompound(PointF point, int action) {
        return mElementSelector.rotateSelectedCompound(point, action);
    }

    public void copySelectedCompound() {
        mCopiedCompound = mElementSelector.getSelectedCompound().copy();
    }

    public void pasteSelectedCompound(PointF point) {
        if (mCopiedCompound != null) {
            Compound compound = mCopiedCompound.copy();

            CompoundArranger.alignCenter(compound, point);
            addCompoundAsSelected(compound);
        }
    }

    public boolean hasCopiedCompound() {
        return mCopiedCompound != null;
    }

    public void changeSelectedAtom(String atomName) {
        if (mElementSelector.selection() == ElementSelector.Selection.ATOM) {
            Atom atom = mElementSelector.getSelectedAtom();

            try {
                atom.setAtomicNumber(AtomicNumber.valueOf(atomName));
            } catch (IllegalArgumentException iae) {
                atom.setArbitraryName(atomName);
            }
        }
    }

    public void preSerialization() {
        for (Compound compound : mCompoundList) {
            compound.preSerialization();
        }
    }

    public void postDeserialization() {
        for (Compound compound : mCompoundList) {
            compound.postDeserialization();
        }
    }

    public void createNew(ProjectFile projectFile) {
        mProjectFile = projectFile;
        mCompoundList.clear();
        mElementSelector.reset();
    }

    public void createFromFile(List<Compound> compounds, ProjectFile projectFile) {
        createNew(projectFile);
        mCompoundList = compounds;
    }

    public ProjectFile getProjectFile() {
        return mProjectFile;
    }

    public boolean isEmpty() {
        return mCompoundList.isEmpty();
    }

    public RectF rectRegion() {
        RectF rect = new RectF();

        for (Compound compound : mCompoundList) {
            RectF compoundRegion = compound.rectRegion();

            if (rect.height() == 0) {
                rect = compoundRegion;
            } else {
                rect.union(compoundRegion);
            }
        }

        Geometry.enlarge(rect, 20);

        return rect;
    }

    public void offsetTo(float newLeft, float newTop) {
        RectF region = rectRegion();

        for (Compound compound : mCompoundList) {
            compound.offset(newLeft - region.left, newTop - region.top);
        }
    }

    public void bondAnnotation(boolean wedgeUp) {
        if (mElementSelector.selection() == ElementSelector.Selection.EDGE) {
            Edge edge = mElementSelector.getSelectedEdge();

            if (edge.first.getBondType(edge.second) == Bond.BondType.SINGLE) {
                mElementSelector.getSelectedEdge().cycleBondAnnotation(wedgeUp);
            } else {
                Notifier.instance().notification("Shall be single bond");
            }
        }
    }

    public void flipBond(Atom fromAtom, final Atom edgeAtom) {
        final PointF l1 = fromAtom.getPoint(), l2 = edgeAtom.getPoint();

        // fromAtom and edgeAtom shall have a bond. All atoms linked to fromAtom will be flipped here
        TreeTraveler.travelIfTrue(new TreeTraveler.IAtomVisitor() {
            @Override
            public boolean visit(Atom atom, Object... args) {
                if (atom != edgeAtom) {
                    atom.setPoint(Geometry.symmetricToLine(atom.getPoint(), l1, l2));
                    return true;
                } else {
                    return false;
                }
            }
        }, fromAtom);
    }

    public void showHydrogenForSelectedElement(boolean show) {
        for (Atom atom : mElementSelector.getSelectedAsList()) {
            CompoundArranger.showAllHydrogen(atom, show);
        }
    }

    public void saturateSelectedWithHydrogen(AtomicNumber an, int maxH) {
        Compound compound = mElementSelector.getSelectedCompound();

        if (mElementSelector.selection() == ElementSelector.Selection.COMPOUND) {
            CompoundReactor.saturateWithHydrogen(compound, an, maxH);
        } else if (mElementSelector.selection() == ElementSelector.Selection.ATOM) {
            Atom atom = mElementSelector.getSelectedAtom();

            if (atom.getAtomicNumber() == an)
                CompoundReactor.saturateWithHydrogen(compound, atom, maxH);
        }
    }
}