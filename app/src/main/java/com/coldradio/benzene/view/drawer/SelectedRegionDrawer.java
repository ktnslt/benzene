package com.coldradio.benzene.view.drawer;

import android.graphics.Canvas;
import android.graphics.Paint;

import com.coldradio.benzene.compound.Compound;
import com.coldradio.benzene.project.IRegionSelector;
import com.coldradio.benzene.project.Project;

public class SelectedRegionDrawer implements ICompoundDrawer {
    @Override
    public boolean draw(Compound compound, Canvas canvas, Paint paint) {
        if (compound == Project.instance().getCompounds().get(0)) {
            // draw just once
            IRegionSelector regionSelector = Project.instance().getElementSelector().getRegionSelector();

            if (regionSelector != null) {
                regionSelector.draw(canvas, PaintSet.instance().paint(PaintSet.PaintType.GUIDE_LINE));
            }
        }
        return true;
    }
}
