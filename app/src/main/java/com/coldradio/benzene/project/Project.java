package com.coldradio.benzene.project;

import android.graphics.PointF;
import android.graphics.RectF;

import com.coldradio.benzene.compound.Atom;
import com.coldradio.benzene.compound.Compound;
import com.coldradio.benzene.compound.CompoundArranger;
import com.coldradio.benzene.compound.CompoundReactor;
import com.coldradio.benzene.compound.AtomicNumber;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

public class Project {
    private static final Project project = new Project();
    private List<Compound> mCompoundList = new ArrayList<>();
    private CompoundReactor mCompoundReactor = new CompoundReactor();
    private IRegionSelector mRegionSelector;    // TODO move to ElementSelector
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

    public void addCyclicToSelectedBond(int edgeNumber, boolean oppositeSite) {
        if (mElementSelector.selection() == ElementSelector.Selection.EDGE) {
            mElementSelector.getSelectedCompound().addCyclicToBond(edgeNumber, oppositeSite, mElementSelector.getSelectedEdge());
        }
    }

    public ElementSelector getElementSelector() {
        return mElementSelector;
    }

    public boolean select(PointF point) {
        return mElementSelector.select(point, Collections.unmodifiableList(mCompoundList));
    }

    public boolean tryToSelect(PointF point) {
        return mElementSelector.tryToSelect(point, Collections.unmodifiableList(mCompoundList));
    }

    public boolean removeCompound(Compound compound) {
        for (Iterator<Compound> it = mCompoundList.iterator(); it.hasNext(); ) {
            if (compound == it.next()) {
                it.remove();
                return true;
            }
        }
        return false;
    }

    public boolean deleteSelectedElement() {
        switch (mElementSelector.selection()) {
            case ATOM:
                mElementSelector.getSelectedCompound().delete(mElementSelector.getSelectedAtom());
                break;
            case EDGE:
                mElementSelector.getSelectedCompound().delete(mElementSelector.getSelectedEdge());
                break;
            case COMPOUND:
                removeCompound(mElementSelector.getSelectedCompound());
                break;
            case NONE:
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

    public boolean synthesis(PointF point) {
        mCompoundReactor.synthesis(point, Collections.unmodifiableList(mCompoundList));
        return true;
    }

    public boolean regionSelect(PointF point, int touchAction) {
        if (touchAction < 0) {
            // long pressed, initiate region selection
            mRegionSelector = new RectSelector(point);
            return true;
        } else if (mRegionSelector != null) {
            return mRegionSelector.onTouchEvent(point, touchAction);
        }

        return false;
    }

    public boolean isSelectingRegion() {
        return mRegionSelector != null;
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

    public boolean moveSelectedElement(PointF distance) {
        return mElementSelector.moveSelectedElement(distance);
    }

    public boolean hasSelectedElement() {
        return mElementSelector.hasSelectedElement();
    }

    public boolean rotateSelectedCompound(PointF point, int action) {
        return mElementSelector.rotateSelectedCompound(point, action);
    }

    public IRegionSelector getRegionSelector() {
        return mRegionSelector;
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

    public void toggleShowHydrogen() {
        if (mElementSelector.selection() == ElementSelector.Selection.ATOM) {
            CompoundArranger.toggleShowHydrogen(mElementSelector.getSelectedAtom());
        }
    }

    public void toggleLettering() {
        if (mElementSelector.selection() == ElementSelector.Selection.ATOM) {
            Atom selectedAtom = mElementSelector.getSelectedAtom();

            selectedAtom.lettering(! selectedAtom.isLettering());
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
        mCompoundList.clear();
        mCompoundList = compounds;
        mElementSelector.reset();
        mProjectFile = projectFile;
    }

    public ProjectFile getProjectFile() {
        return mProjectFile;
    }

    public boolean isEmpty() {
        return mCompoundList.isEmpty();
    }

    public RectF rectRegion() {
        RectF rect = null;

        for (Compound compound : mCompoundList) {
            RectF compoundRegion = compound.rectRegion();

            if (rect == null) {
                rect = compoundRegion;
            } else {
                rect.union(compoundRegion);
            }
        }

        return rect != null ? rect : new RectF();
    }

    public void offsetTo(float newLeft, float newTop) {
        RectF region = rectRegion();

        for (Compound compound : mCompoundList) {
            compound.offset(newLeft - region.left, newTop - region.top);
        }
    }
}