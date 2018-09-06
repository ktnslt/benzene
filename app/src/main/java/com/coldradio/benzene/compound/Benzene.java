package com.coldradio.benzene.compound;

public class Benzene extends CycloCompound {
    public enum DrawingMode {
        CIRCLE, LINE
    }

    DrawingMode mDrawingMode = DrawingMode.LINE;

    public Benzene() {
        super(6);
        // set up link between carbons
        for (int ii = 0; ii < 6; ++ii) {
            if (ii % 2 == 0) {
                mAtoms.get(ii).setBond(mAtoms.get(ii + 1), Bond.BondType.DOUBLE);
            }
        }
    }

    public DrawingMode getDrawingMode() {
        return mDrawingMode;
    }
}
