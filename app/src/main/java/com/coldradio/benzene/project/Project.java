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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Project {
    private static final Project project = new Project();
    private List<Compound> mCompoundList = new ArrayList<>();
    private ElementSelector mElementSelector = new ElementSelector();
    private ElementCopier mElementCopier = new ElementCopier();
    private ProjectFile mProjectFile;

    private void removeEmptyCompound() {
        // separate list for ConcurrentModificationError
        List<Compound> emptyCompounds = new ArrayList<>();

        for (Compound compound : mCompoundList) {
            if (compound.size() == 0)
                emptyCompounds.add(compound);
        }

        for (Compound compound : emptyCompounds) {
            removeCompound(compound);
        }
    }

    public static Project instance() {
        return project;
    }

    public List<Compound> getCompounds() {
        return Collections.unmodifiableList(mCompoundList);
    }

    public void addCompound(Compound compound, boolean select) {
        mCompoundList.add(compound);
        if (select) {
            mElementSelector.selectCompound(compound);
        }
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

    public ElementCopier getElementCopier() {
        return mElementCopier;
    }

    public boolean select(PointF point) {
        return mElementSelector.select(point);
    }

    public void removeCompound(Compound compound) {
        if (mElementSelector.selection() == ElementSelector.Selection.COMPOUND
                && mElementSelector.getSelectedCompound() == compound) {
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

    public Compound findCompound(long cID) {
        for (Compound compound : mCompoundList) {
            if (compound.getID() == cID)
                return compound;
        }
        return null;
    }

    public boolean deleteSelectedElement() {
        switch (mElementSelector.selection()) {
            case ATOM:
                Compound selectedCompound = mElementSelector.getSelectedCompound();

                CompoundReactor.deleteAtom(selectedCompound, mElementSelector.getSelectedAtom());
                // remove the compound if empty
                if (selectedCompound.size() == 0)
                    removeCompound(selectedCompound);
                break;
            case EDGE:
                CompoundReactor.deleteBond(mElementSelector.getSelectedCompound(), mElementSelector.getSelectedEdge());
                break;
            case COMPOUND:
                removeCompound(mElementSelector.getSelectedCompound());
                break;
            case PARTIAL:
                CompoundReactor.deleteAtoms(mElementSelector.getSelectedAsList());
                removeEmptyCompound();
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
                ProjectFileManager.instance().pushAllChangedHistory(Project.instance().getCompounds());
                CompoundReactor.makeBond(mElementSelector.getSelectedCompound(), mElementSelector.getSelectedAtom(), toAtom.second, (Atom) toAtom.first);
                return true;
            }
            Notifier.instance().notification("Atom shall be selected");
        }
        return false;
    }

    public boolean rotateSelectedCompound(PointF point, int action) {
        return mElementSelector.rotateSelectedCompound(point, action);
    }

    public boolean copySelectedCompound() {
        if (mElementSelector.hasSelected()) {
            mElementCopier.copy(mElementSelector.getSelectedAsList());
            return true;
        } else {
            return false;
        }
    }

    public void pasteSelectedCompound(PointF point) {
        if (mElementCopier.hasCopied()) {
            mElementCopier.paste(Project.instance());
        }
    }

    public void changeSelectedAtom(String atomName) {
        if (mElementSelector.selection() == ElementSelector.Selection.ATOM) {
            Atom atom = mElementSelector.getSelectedAtom();

            try {
                AtomicNumber an = AtomicNumber.valueOf(atomName);

                if (an == AtomicNumber.H && atom.numberOfBonds() > 1) {
                    Notifier.instance().notification("H cannot have two bonds");
                } else {
                    atom.setAtomicNumber(an);
                }
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

    public List<Compound> replaceAllCompounds(List<Compound> compounds) {
        List<Compound> ret = mCompoundList;

        mCompoundList = compounds;
        return ret;
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

    public void flipHydrogenForSelected() {
        if (mElementSelector.selection() == ElementSelector.Selection.ATOM) {
            CompoundArranger.flipHydrogen(mElementSelector.getSelectedAtom());
        }
    }

    public Compound replaceCompound(long cID, Compound compound) {
        Compound c = findCompound(cID);

        if (c != null) {
            removeCompound(c);
            mCompoundList.add(compound);

            return c;
        }
        return null;
    }
}