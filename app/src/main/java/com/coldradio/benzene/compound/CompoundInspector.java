package com.coldradio.benzene.compound;

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

    public static int numberOfSkeleton(Compound compound) {
        int skeleton = 0;

        for (Atom atom : compound.getAtoms()) {
            if (atom.getAtomicNumber() != AtomicNumber.H)
                ++skeleton;
        }
        return skeleton;
    }

    public static Atom[] extractSkeleton(Compound compound) {
        Atom[] atoms = new Atom[numberOfSkeleton(compound)];

        for (Atom atom : compound.getAtoms()) {

        }

        return atoms;
    }
}
