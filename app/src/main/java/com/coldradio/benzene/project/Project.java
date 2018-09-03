package com.coldradio.benzene.project;

import android.graphics.Canvas;

import com.coldradio.benzene.compound.Compound;
import com.coldradio.benzene.view.CompoundDrawer;
import java.util.ArrayList;
import java.util.List;

public class Project {
    private static final Project project = new Project();
    private List<Compound> mCompoundList = new ArrayList<>();

    private Project() {

    }

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
}
