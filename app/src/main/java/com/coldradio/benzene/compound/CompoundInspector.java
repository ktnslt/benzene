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

    public static Atom[] extractSkeletonChain(Atom startAtom, int maxLen) {
        // maxLen is needed for the cyclo-compound
        List<Atom> skeletons = new ArrayList<>();

        for (Atom curAtom = startAtom, prevAtom = null; curAtom != null; curAtom = curAtom.getSkeletonAtomExcept(prevAtom)) {
            prevAtom = skeletons.size() == 0 ? null : skeletons.get(skeletons.size() - 1);
            skeletons.add(curAtom);
            if (skeletons.size() >= maxLen)
                break;
        }

        return skeletons.toArray(new Atom[skeletons.size()]);
    }

    public static List<Atom> allHydrogens(Compound compound) {
        List<Atom> hydrogens = new ArrayList<>();

        for (Atom atom : compound.getAtoms()) {
            if (atom.getAtomicNumber() == AtomicNumber.H)
                hydrogens.add(atom);
        }

        return hydrogens;
    }

    public static List<Atom> allHydrogens(Atom atom) {
        List<Atom> hydrogens = new ArrayList<>();

        for (Bond bond : atom.getBonds()) {
            Atom h = bond.getBoundAtom();

            if (h.getAtomicNumber() == AtomicNumber.H)
                hydrogens.add(h);
        }

        return hydrogens;
    }

    public static int numberOfHydrogen(Atom atom) {
        int hNum = 0;

        for (Bond bond : atom.getBonds()) {
            Atom h = bond.getBoundAtom();

            if (h.getAtomicNumber() == AtomicNumber.H)
                ++hNum;
        }
        return hNum;
    }

    public static Atom findBoundAtom(Atom atom, AtomCondition ac) {
        for (Bond bond : atom.getBonds()) {
            Atom boundAtom = bond.getBoundAtom();

            if (ac.satisfiedBy(boundAtom))
                return boundAtom;
        }
        return null;
    }
}
