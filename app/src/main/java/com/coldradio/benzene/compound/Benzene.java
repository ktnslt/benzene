package com.coldradio.benzene.compound;

import com.coldradio.benzene.geometry.Geometry;
import com.coldradio.benzene.view.BenzeneDrawer;
import com.coldradio.benzene.view.CompoundDrawer;

public class Benzene extends Compound {
    public enum DrawingMode {
        CIRCLE, LINE
    }
    DrawingMode mDrawingMode = DrawingMode.LINE;

    public Benzene() {
        fillCarbon(6);
        // set up link between carbons
        for(int ii = 0; ii < 6; ++ii) {
            if(ii % 2 == 0) {
                mAtoms.get(ii).doubleBond(mAtoms.get((ii+1) % 6));
            } else {
                mAtoms.get(ii).singleBond(mAtoms.get((ii+1) % 6));
            }
        }

        Geometry.cycloHexaneGeometry(mAtoms);
        CompoundDrawer.instance().addComponentDrawer(new BenzeneDrawer());
    }
    public DrawingMode getDrawingMode() {
        return mDrawingMode;
    }
}
