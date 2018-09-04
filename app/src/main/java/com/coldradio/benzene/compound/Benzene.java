package com.coldradio.benzene.compound;

import com.coldradio.benzene.geometry.Geometry;
import com.coldradio.benzene.view.BenzeneDrawer;
import com.coldradio.benzene.view.CompoundDrawer;

public class Benzene extends Compound {
    public enum DrawingMode {
        CIRCLE, ODD_LINE, EVEN_LINE
    }
    DrawingMode mDrawingMode = DrawingMode.CIRCLE;

    public Benzene() {
        fillCarbon(6);
        // set up link between carbons
        for(int ii = 0; ii < 6; ++ii) {
            mAtoms.get(ii).singleBond(mAtoms.get(ii == 0 ? 5 : ii-1));
            mAtoms.get(ii).doubleBond(mAtoms.get((ii+1) % 6));
        }

        Geometry.cycloHexaneGeometry(mAtoms);

        CompoundDrawer.instance().addComponentDrawer(new BenzeneDrawer());
    }
    public DrawingMode getDrawingMode() {
        return mDrawingMode;
    }
}
