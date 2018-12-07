package com.coldradio.benzene.compound;

import java.util.ArrayList;
import java.util.List;

public class CompoundInspector {
    public static Atom returnCarbonIfC1Hn(Compound compound) {
        int carbon = 0;
        Atom c = null;

        for (Atom atom : compound.getAtoms()) {
            if (atom.getAtomicNumber() == AtomicNumber.C) {
                ++carbon;
                c = atom;
                if (carbon > 1) return null;
            } else if (atom.getAtomicNumber() != AtomicNumber.H) {
                return null;
            }
        }
        return c;
    }

    public static int numberOfBoundCarbon(Atom atom) {
        int boundCarbon = 0;

        for (Bond bond : atom.getBonds()) {
            if (bond.getBoundAtom().getAtomicNumber() == AtomicNumber.C)
                ++boundCarbon;
        }

        return boundCarbon;
    }

    public static List<Atom> allBoundCarbon(Atom atom) {
        List<Atom> carbons = new ArrayList<>();

        for (Bond bond : atom.getBonds()) {
            Atom that_atom = bond.getBoundAtom();

            if (that_atom.getAtomicNumber() == AtomicNumber.C)
                carbons.add(that_atom);
        }

        return carbons;
    }
}
