package com.coldradio.benzene.compound;

public class ConjuatedCyclicCompound extends CyclicCompound {
    public enum DrawingMode {
        CIRCLE, LINE
    }

    DrawingMode mDrawingMode = DrawingMode.LINE;

    public ConjuatedCyclicCompound(int carbonNumber) {
        super(carbonNumber);
        // set up link between carbons
        for (int ii = 0; ii < carbonNumber; ++ii) {
            if (ii % 2 == 0) {
                if(carbonNumber % 2 == 1 && ii == carbonNumber -1) break;   // skip the last one for odd number cyclic compound
                mAtoms.get(ii).setBond(mAtoms.get((ii+1) % carbonNumber), Bond.BondType.DOUBLE);
            }
        }
    }

    public DrawingMode getDrawingMode() {
        return mDrawingMode;
    }
}
