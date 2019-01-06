package com.coldradio.benzene.view.drawer;

import android.graphics.Canvas;
import android.graphics.Paint;

import com.coldradio.benzene.compound.Compound;
import com.coldradio.benzene.project.Project;

import java.util.ArrayList;
import java.util.List;

public class DrawerManager {
    private List<ICompoundDrawer> mPreCompoundDrawer = new ArrayList<>();
    private List<ICompoundDrawer> mPostCompoundDrawer = new ArrayList<>();

    private boolean isRegistered(ICompoundDrawer newDrawer) {
        for (ICompoundDrawer drawer : mPreCompoundDrawer) {
            if (drawer.getID().equals(newDrawer.getID()))
                return true;
        }

        for (ICompoundDrawer drawer : mPostCompoundDrawer) {
            if (drawer.getID().equals(newDrawer.getID()))
                return true;
        }
        return false;
    }

    public void addPreCompoundDrawer(ICompoundDrawer compoundDrawer) {
        if (! isRegistered(compoundDrawer))
            mPreCompoundDrawer.add(compoundDrawer);
    }

    public void addPostCompoundDrawer(ICompoundDrawer compoundDrawer) {
        if (! isRegistered(compoundDrawer))
            mPostCompoundDrawer.add(compoundDrawer);
    }

    public void draw(Canvas canvas) {
        Paint generalPaint = PaintSet.instance().paint(PaintSet.PaintType.GENERAL);

        for (Compound compound : Project.instance().getCompounds()) {
            for (ICompoundDrawer componentDrawer : mPreCompoundDrawer) {
                componentDrawer.draw(compound, canvas, generalPaint);
            }
        }
        
        for (Compound compound : Project.instance().getCompounds()) {
            GenericDrawer.draw(compound, canvas, generalPaint);
        }

        for (Compound compound : Project.instance().getCompounds()) {
            for (ICompoundDrawer componentDrawer : mPostCompoundDrawer) {
                componentDrawer.draw(compound, canvas, generalPaint);
            }
        }
    }
}
