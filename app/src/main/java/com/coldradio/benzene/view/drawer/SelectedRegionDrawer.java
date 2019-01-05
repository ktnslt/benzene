package com.coldradio.benzene.view.drawer;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PointF;

import com.coldradio.benzene.compound.Atom;
import com.coldradio.benzene.compound.Compound;
import com.coldradio.benzene.project.ElementSelector;
import com.coldradio.benzene.project.IRegionSelector;
import com.coldradio.benzene.project.Project;

public class SelectedRegionDrawer implements ICompoundDrawer {
    @Override
    public boolean draw(Compound compound, Canvas canvas, Paint paint) {
        IRegionSelector regionSelector = Project.instance().getElementSelector().getRegionSelector();

        if (regionSelector != null) {
            regionSelector.draw(canvas, PaintSet.instance().paint(PaintSet.PaintType.GUIDE_LINE));
        }
        return true;
    }

    @Override
    public String getID() {
        return "SelectedRegionDrawer";
    }
}
