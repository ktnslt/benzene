package com.coldradio.benzene.compound;

public class CyclicCompound extends Compound {
    public CyclicCompound(int carbonNumber) {
        fillCarbon(carbonNumber);
        // set up link between carbons
        for(int ii = 0; ii < carbonNumber; ++ii) {
            mAtoms.get(ii).singleBond(mAtoms.get((ii+1) % carbonNumber));
        }

        CompoundFactory.cyclicGeometry(mAtoms);
    }
}
