package com.coldradio.benzene.compound;

import java.util.ArrayList;
import java.util.List;

public class Atom {
    private List<Bond> mBonds = new ArrayList<>();

    public void doubleBond(Atom bondTo) {
        mBonds.add(new Bond(bondTo, Bond.BondType.DOUBLE_BOND));
    }

    public void singleBond(Atom bondTo) {
        mBonds.add(new Bond(bondTo, Bond.BondType.SINGLE_BOND));
    }
}
