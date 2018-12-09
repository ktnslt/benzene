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

    public static int numberOfBoundSkeletonAtom(Atom atom) {
        int boundSkeleton = 0;

        for (Bond bond : atom.getBonds()) {
            if (bond.getBoundAtom().getAtomicNumber() != AtomicNumber.H)
                ++boundSkeleton;
        }

        return boundSkeleton;
    }

    public static List<Atom> boundSkeletonAtom(Atom atom) {
        List<Atom> carbons = new ArrayList<>();

        for (Bond bond : atom.getBonds()) {
            Atom that_atom = bond.getBoundAtom();

            if (that_atom.getAtomicNumber() != AtomicNumber.H)
                carbons.add(that_atom);
        }

        return carbons;
    }
}
