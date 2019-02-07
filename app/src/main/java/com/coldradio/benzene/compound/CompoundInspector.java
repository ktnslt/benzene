package com.coldradio.benzene.compound;

import android.graphics.PointF;

import com.coldradio.benzene.project.Configuration;
import com.coldradio.benzene.util.Geometry;
import com.coldradio.benzene.util.TreeTraveler;

import java.util.ArrayList;
import java.util.List;

public class CompoundInspector {
    private static Compound splitOne(Compound compound) {
        final Compound splitCompound = new Compound();

        TreeTraveler.returnFirstAtom(new TreeTraveler.AtomVisitorAlwaysTravelDown() {
            @Override
            public boolean visit(Atom atom, int distanceFromRoot, Object... args) {
                splitCompound.addAtom(atom);
                return false;
            }
        }, compound.getAtoms().get(0));

        // compound's atom cannot be deleted above since it iterates through it
        for (Atom atom : splitCompound.getAtoms()) {
            compound.removeAtom(atom);
        }

        return splitCompound;
    }

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

    public static boolean lessThanTwoSkeletonAtom(Compound compound) {
        // this is not like uniqueSkeleton(), that is for only 1 skeleton; while this is for 0 or 1
        Atom skeletonAtom = anySkeletonAtom(compound);

        return skeletonAtom == null || skeletonAtom.getSkeletonAtom() == null;
    }

    public static Atom anySkeletonAtom(Compound compound) {
        for (Atom atom : compound.getAtoms()) {
            if (atom.getAtomicNumber() != AtomicNumber.H && atom.numberOfBonds() > 0)
                return atom;
        }
        return null;
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

    public static int numberOfSkeletonAtoms(Atom atom) {
        int boundSkeleton = 0;

        for (Bond bond : atom.getBonds()) {
            if (bond.getBoundAtom().getAtomicNumber() != AtomicNumber.H)
                ++boundSkeleton;
        }

        return boundSkeleton;
    }

    public static List<Atom> boundSkeletonAtoms(Atom atom) {
        List<Atom> carbons = new ArrayList<>();

        for (Bond bond : atom.getBonds()) {
            Atom that_atom = bond.getBoundAtom();

            if (that_atom.getAtomicNumber() != AtomicNumber.H)
                carbons.add(that_atom);
        }

        return carbons;
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

    public static boolean showAnyHydrogen(Atom atom) {
        for (Bond bond : atom.getBonds()) {
            Atom h = bond.getBoundAtom();

            if (h.getAtomicNumber() == AtomicNumber.H && h.getAtomDecoration().getShowElementName()) {
                return true;
            }
        }
        return false;
    }

    public static boolean showAnyHydrogen(List<Atom> atomList) {
        for (Atom atom : atomList) {
            if (numberOfHydrogen(atom) > 0 && showAnyHydrogen(atom))
                return true;
        }
        return false;
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

    public static int numberOfBonds(Atom atom) {
        int numBonds = 0;

        for (Bond bond : atom.getBonds()) {
            Bond.BondType bondType = bond.getBondType();

            if (bondType == Bond.BondType.SINGLE) {
                numBonds++;
            } else if (bondType == Bond.BondType.DOUBLE || bondType == Bond.BondType.DOUBLE_MIDDLE || bondType == Bond.BondType.DOUBLE_OTHER_SIDE) {
                numBonds += 2;
            } else if (bondType == Bond.BondType.TRIPLE) {
                numBonds += 3;
            }
        }
        return numBonds;
    }

    public static Atom findBoundAtom(Atom atom, AtomCondition ac) {
        for (Bond bond : atom.getBonds()) {
            Atom boundAtom = bond.getBoundAtom();

            if (ac.satisfiedBy(boundAtom))
                return boundAtom;
        }
        return null;
    }

    public static List<Compound> split(Compound compound) {
        List<Compound> splitCompounds = new ArrayList<>();

        while (compound.size() > 0) {
            splitCompounds.add(splitOne(compound));
        }

        return splitCompounds;
    }

    public static boolean allBondsAreDouble(Atom atom) {
        for (Bond bond : atom.getBonds()) {
            if (bond.getBondType() != Bond.BondType.DOUBLE && bond.getBondType() != Bond.BondType.DOUBLE_OTHER_SIDE && bond.getBondType() != Bond.BondType.DOUBLE_MIDDLE) {
                return false;
            }
        }
        return true;
    }

    public static boolean isHalogen(Atom atom) {
        AtomicNumber an = atom.getAtomicNumber();

        return an == AtomicNumber.F || an == AtomicNumber.Cl || an == AtomicNumber.Br
                || an == AtomicNumber.I || an == AtomicNumber.At;
    }

    public static boolean isSkeletonAtom(Atom atom) {
        return atom.getAtomicNumber() != AtomicNumber.H;
    }

    public static boolean moreHydrogensInRight(Atom atom) {
        int right = 0;

        for (Bond bond : atom.getBonds()) {
            Atom boundAtom = bond.getBoundAtom();

            if (boundAtom.getAtomicNumber() == AtomicNumber.H) {
                right += (atom.getPoint().x <= boundAtom.getPoint().x) ? 1 : -1;
            }
        }

        return right >= 0;
    }

    public static Atom uniqueSkeletonWithDoubleBond(Atom atom) {
        int skeletonBondNumber = 0;
        Atom skeletonAtom = null;

        for (Bond bond : atom.getBonds()) {
            Atom that_atom = bond.getBoundAtom();

            if (isSkeletonAtom(that_atom)) {
                skeletonBondNumber++;
                skeletonAtom = that_atom;

                if (skeletonBondNumber > 1 || !bond.isDoubleBond()) {
                    return null;
                }
            }
        }
        return skeletonAtom;
    }

    public static Atom uniqueSkeleton(Atom atom) {
        int skeletonBondNumber = 0;
        Atom skeletonAtom = null;

        for (Bond bond : atom.getBonds()) {
            Atom that_atom = bond.getBoundAtom();

            if (isSkeletonAtom(that_atom)) {
                skeletonBondNumber++;
                skeletonAtom = that_atom;

                if (skeletonBondNumber > 1) {
                    return null;
                }
            }
        }
        return skeletonAtom;
    }

    public static boolean uniqueSkeletonOnRightSide(Atom atom) {
        Atom unique = uniqueSkeleton(atom);

        if (unique != null) {
            return Geometry.onRight(atom.getPoint(), unique.getPoint());
        }
        return false;
    }

    public static boolean pathExistsExceptDirect(final Atom from, Atom to) {
        // test whether there exists bonds 'to' ->->-> 'from
        return null != TreeTraveler.returnFirstAtom(new TreeTraveler.IAtomVisitor() {
            @Override
            public boolean visit(Atom atom, int distanceFromRoot, Object... args) {
                return atom == from && distanceFromRoot > 1;
            }

            @Override
            public boolean travelDown(Atom atom, int distanceFromRoot, Object... args) {
                return atom != from;
            }
        }, to);
    }

    public static Atom findProximityAtom(Compound compound, PointF point) {
        for (Atom atom : compound.getAtoms()) {
            if (Geometry.distanceFromPointToPoint(atom.getPoint(), point) < Configuration.ATOM_PROXIMITY) {
                return atom;
            }
        }
        return null;
    }
}
