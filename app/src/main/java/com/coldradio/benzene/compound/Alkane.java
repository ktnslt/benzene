package com.coldradio.benzene.compound;

import com.coldradio.benzene.geometry.Geometry;

public class Alkane extends Compound{
    public Alkane(int carbonLength) {
        for(int ii = 0; ii < carbonLength; ++ii) {
            mAtoms.add(new Carbon());
        }
        // set up link between carbons
        mAtoms.get(0).singleBond(mAtoms.get(1));
        for(int ii = 1; ii < carbonLength - 1; ++ii) {
            mAtoms.get(ii).singleBond(mAtoms.get( ii == 0 ? carbonLength - 1 : ii - 1));
            mAtoms.get(ii).singleBond(mAtoms.get((ii+1) % carbonLength));
        }
        mAtoms.get(mAtoms.size() - 1).singleBond(mAtoms.get(mAtoms.size() - 2));

        Geometry.alkaneGeometry(mAtoms, true);
    }
}
