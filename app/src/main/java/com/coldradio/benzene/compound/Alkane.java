package com.coldradio.benzene.compound;

public class Alkane extends Compound{
    public Alkane(int carbonLength) {
        mGeometry.setupAlkaneGeometry(carbonLength, true);
    }
}
