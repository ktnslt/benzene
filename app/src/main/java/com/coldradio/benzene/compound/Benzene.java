package com.coldradio.benzene.compound;

public class Benzene extends Compound {
    Carbon[] carbons = new Carbon[6];

    public Benzene() {
        for(int ii = 0; ii < carbons.length; ++ii) {
            carbons[ii] = new Carbon();
        }
        // set up link between carbons
        carbons[0].doubleBond(carbons[1]);
        carbons[0].singleBond(carbons[5]);
        for(int ii = 1; ii < carbons.length; ++ii) {
            carbons[ii].doubleBond(carbons[(ii+1) % carbons.length]);
            carbons[ii].singleBond(carbons[ii-1]);
        }

        mGeometry.setCycloHexaneGeometry();
    }
}
