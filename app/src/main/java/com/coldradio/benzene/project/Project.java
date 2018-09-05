package com.coldradio.benzene.project;

import android.graphics.Canvas;

import com.coldradio.benzene.compound.Compound;
import com.coldradio.benzene.view.CompoundDrawer;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class Project {
    private static final Project project = new Project();
    private List<Compound> mCompoundList = new ArrayList<>();

    public static Project instance() {
        return project;
    }
    public void drawTo(Canvas canvas) {
        for(Compound compound : mCompoundList) {
            CompoundDrawer.instance().draw(compound, canvas);
        }
    }
    public void addCompound(Compound compound) {
        mCompoundList.add(compound);
    }
    public boolean selectComponent(float x, float y) {
        boolean anySelected = false;

        for(Compound compound : mCompoundList) {
            if(anySelected == false) {
                anySelected = compound.select(x, y);
            }
            else {
                compound.setSelected(false);
            }
        }
        return anySelected;
    }
    public boolean removeCompound(Compound compound) {
        for(Iterator<Compound> it = mCompoundList.iterator(); it.hasNext(); ) {
            if(compound == it.next()) {
                it.remove();
                return true;
            }
        }
        return false;
    }
    public boolean decomposition(float x, float y) {
        // TODO: in case of a ring, it will not be broken into two compounds
        for(Compound compound : mCompoundList) {
            Compound cutCompound = compound.decomposition(x, y);

            if(cutCompound != null) {
                if(compound.size() == 1) {
                    removeCompound(compound);
                }
                if(cutCompound.size() > 1) {
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
}
