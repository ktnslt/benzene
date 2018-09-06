package com.coldradio.benzene.compound;

import com.coldradio.benzene.geometry.Geometry;

public class CycloCompound extends Compound {
    public CycloCompound(int carbonNumber) {
        fillCarbon(carbonNumber);
        // set up link between carbons
        for(int ii = 0; ii < carbonNumber; ++ii) {
            mAtoms.get(ii).singleBond(mAtoms.get((ii+1) % carbonNumber));
        }

        Geometry.cycloGeometry(mAtoms);
    }
}
