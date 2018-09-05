package com.coldradio.benzene.compound;

import com.coldradio.benzene.geometry.Geometry;

public class Alkane extends Compound{
    public Alkane(int carbonLength) {
        fillCarbon(carbonLength);
        // set up link between carbons
        for(int ii = 0; ii < carbonLength - 1; ++ii) {
            mAtoms.get(ii).singleBond(mAtoms.get(ii+1));
        }

        Geometry.alkaneGeometry(mAtoms, true);
    }
}
