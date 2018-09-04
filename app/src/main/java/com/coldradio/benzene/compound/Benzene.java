package com.coldradio.benzene.compound;

import com.coldradio.benzene.geometry.Geometry;

public class Benzene extends Compound {
    public Benzene() {
        fillCarbon(6);
        // set up link between carbons
        for(int ii = 0; ii < 6; ++ii) {
            mAtoms.get(ii).singleBond(mAtoms.get(ii == 0 ? 5 : ii-1));
            mAtoms.get(ii).doubleBond(mAtoms.get((ii+1) % 6));
        }

        Geometry.cycloHexaneGeometry(mAtoms);
    }
}
