package com.coldradio.benzene.compound.funcgroup;

import com.coldradio.benzene.compound.Atom;
import com.coldradio.benzene.compound.AtomicNumber;
import com.coldradio.benzene.compound.Bond;
import com.coldradio.benzene.compound.Compound;
import com.coldradio.benzene.compound.CompoundInspector;
import com.coldradio.benzene.compound.CompoundReactor;
import com.coldradio.benzene.project.Configuration;
import com.coldradio.benzene.util.Geometry;

import java.util.List;

public class SO2Me_FG extends Ethyl_FG {
    public SO2Me_FG(Atom a_atom) {
        super(a_atom);

        Atom[] c = CompoundInspector.extractSkeletonChain(super.appendAtom(), 2);

        c[0].setAtomicNumber(AtomicNumber.S);

        List<Atom> h_of_s = CompoundInspector.allHydrogens(c[0]);
        Atom o1 = h_of_s.get(0);
        Atom o2 = h_of_s.get(1);

        o1.setAtomicNumber(AtomicNumber.O);
        o1.setBond(c[0], Bond.BondType.DOUBLE_MIDDLE);
        o2.setAtomicNumber(AtomicNumber.O);
        o2.setBond(c[0], Bond.BondType.DOUBLE_MIDDLE);

        o1.setPoint(Geometry.zoom(o1.getPoint(), c[0].getPoint(), 1/Configuration.H_BOND_LENGTH_RATIO));
        o2.setPoint(Geometry.zoom(o2.getPoint(), c[0].getPoint(), 1/Configuration.H_BOND_LENGTH_RATIO));
    }

    @Override
    public String getName() {
        return "SO2Me";
    }
}
