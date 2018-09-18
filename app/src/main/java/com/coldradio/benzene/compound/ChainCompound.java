package com.coldradio.benzene.compound;

public class ChainCompound extends Compound{
    public ChainCompound(int carbonLength) {
        fillCarbon(carbonLength);
        // set up link between carbons
        for(int ii = 0; ii < carbonLength - 1; ++ii) {
            mAtoms.get(ii).singleBond(mAtoms.get(ii+1));
        }

        CompoundFactory.alkaneGeometry(mAtoms, true);
    }
}
