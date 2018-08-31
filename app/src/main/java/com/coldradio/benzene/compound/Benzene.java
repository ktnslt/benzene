package com.coldradio.benzene.compound;

public class Benzene extends Compound {
    Carbon[] carbons = new Carbon[6];

    public Benzene() {
        for(int ii = 0; ii < carbons.length; ++ii) {
            carbons[ii] = new Carbon();
        }
        //TODO: set up link between carbons
    }
}
