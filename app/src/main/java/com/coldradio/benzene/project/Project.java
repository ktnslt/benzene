package com.coldradio.benzene.project;

import android.graphics.Canvas;
import android.graphics.PointF;
import android.graphics.RectF;
import android.view.MotionEvent;

import com.coldradio.benzene.compound.Compound;
import com.coldradio.benzene.compound.CompoundReactor;
import com.coldradio.benzene.geometry.Geometry;
import com.coldradio.benzene.view.CompoundDrawer;
import com.coldradio.benzene.view.RegionSelector;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

public class Project {
    private static final Project project = new Project();
    private List<Compound> mCompoundList = new ArrayList<>();
    private CompoundReactor mCompoundReactor = new CompoundReactor();
    private RegionSelector mRegionSelector = new RegionSelector();
    private SelectedCompound mSelectedCompound;
    private CompoundDrawer mCompoundDrawer = new CompoundDrawer();

    private Compound getSelectedCompound() {
        if (mSelectedCompound != null) {
            return mSelectedCompound.getCompound();
        } else {
            return null;
        }
    }

    public static Project instance() {
        return project;
    }

    public void drawTo(Canvas canvas) {
        for (Compound compound : mCompoundList) {
            boolean selected = mSelectedCompound != null && mSelectedCompound.getCompound() == compound;

            mCompoundDrawer.draw(compound, selected, canvas);
        }
        mCompoundDrawer.drawSynthesis(mCompoundReactor, canvas);

        if (mSelectedCompound != null) {
            // not drawn at the same time with the compound since the accessory shall be in front of everything
            mCompoundDrawer.drawSelectedCompoundAccessory(mSelectedCompound, canvas);
        }

        mRegionSelector.draw(canvas);
    }

    public void addCompound(Compound compound) {
        mCompoundList.add(compound);
    }

    public boolean selectComponent(float x, float y) {
        mSelectedCompound = null;

        for (Compound compound : mCompoundList) {
            if (mSelectedCompound == null && compound.isSelectable(x, y)) {
                mSelectedCompound = new SelectedCompound(compound);
            }
        }
        return mSelectedCompound != null;
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

    public boolean decomposition(float x, float y) {
        // TODO: in case of a ring, it will not be broken into two compounds
        for (Compound compound : mCompoundList) {
            Compound cutCompound = compound.decomposition(x, y);

            if (cutCompound != null) {
                if (compound.size() == 1) {
                    removeCompound(compound);
                }
                if (cutCompound.size() > 1) {
                    mCompoundList.add(cutCompound);
                }
                return true;
            }
        }
        return false;
    }

    public int compoundNumber() {
        return mCompoundList.size();
    }

    public boolean cycleBondType(float x, float y) {
        for (Compound compound : mCompoundList) {
            if (compound.cycleBondType(x, y)) {
                return true;
            }
        }
        return false;
    }

    public boolean synthesis(float x, float y) {
        mCompoundReactor.synthesis(x, y, Collections.unmodifiableList(mCompoundList));
        return true;
    }

    public void initiateRegionSelect(PointF point) {
        mRegionSelector.initiate(point);
    }

    public PointF centerOfAllCompounds() {
        if (compoundNumber() > 0) {
            RectF allRegion = mCompoundList.get(0).rectRegion();

            for (Compound compound : mCompoundList) {
                RectF region = compound.rectRegion();

                allRegion.union(region);
            }

            return new PointF(allRegion.centerX(), allRegion.centerY());
        } else {
            return new PointF(0, 0);    // TODO: return the center of the screen?
        }
    }

    public boolean moveSelectedCompoundBy(float distanceX, float distanceY) {
        Compound selectedCompound = getSelectedCompound();

        if (selectedCompound != null) {
            selectedCompound.offset(distanceX, distanceY);
            return true;
        }
        return false;
    }

    public boolean hasSelectedCompound() {
        return getSelectedCompound() != null;
    }

    public boolean rotateSelectedCompound(PointF point, int action) {
        if (hasSelectedCompound() && (action == MotionEvent.ACTION_DOWN || action == MotionEvent.ACTION_MOVE)) {
            return mSelectedCompound.rotate(point);
        }
        return false;
    }

    public boolean isRotatingCompound(PointF point) {
        return hasSelectedCompound() && mSelectedCompound.isPivotGrasped(point);
    }
}
