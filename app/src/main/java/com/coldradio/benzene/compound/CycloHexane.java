package com.coldradio.benzene.compound;

import com.coldradio.benzene.geometry.Geometry;

public class CycloHexane extends Compound {
    public CycloHexane() {
        fillCarbon(6);
        // set up link between carbons
        for(int ii = 0; ii < 6; ++ii) {
            mAtoms.get(ii).singleBond(mAtoms.get((ii+1) % 6));
        }

        Geometry.cycloHexaneGeometry(mAtoms);
    }
}
