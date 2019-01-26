package com.coldradio.benzene.compound;

import android.graphics.PointF;

import com.coldradio.benzene.compound.funcgroup.IFunctionalGroup;
import com.coldradio.benzene.library.rule.LetteringIfCompoundNotSeenRule;
import com.coldradio.benzene.library.rule.RuleSet;
import com.coldradio.benzene.project.Project;
import com.coldradio.benzene.util.Geometry;
import com.coldradio.benzene.util.MathConstant;
import com.coldradio.benzene.util.Notifier;

import java.util.ArrayList;
import java.util.List;

public class CompoundReactor {
    private static LetteringIfCompoundNotSeenRule mLetteringIfCompoundNotSeenRule = new LetteringIfCompoundNotSeenRule();

    private static void splitAndUpdateProject(Compound compound) {
        Project.instance().addCompounds(RuleSet.instance().apply(CompoundInspector.split(compound), mLetteringIfCompoundNotSeenRule));
        Project.instance().removeCompound(compound);
    }

    private static void deleteAtomWithoutSplitting(Compound compound, Atom atom) {
        CompoundReactor.deleteAllHydrogen(compound, atom);
        // cut all bonds - iterating bonds doesn't work due to the concurrent modification
        // hence make a copy of it for iteration
        //        for (Bond bond : atom.getBonds())
        //            atom.cutBond(bond.getBoundAtom());
        for (Bond bond : new ArrayList<>(atom.getBonds())) {
            atom.cutBond(bond.getBoundAtom());
        }
        // delete atom
        compound.removeAtom(atom);
    }

    public static Compound chainCompound(PointF[] points) {
        List<Atom> atoms = new ArrayList<>();

        for (int ii = 0; ii < points.length; ++ii) {
            if (points[ii] != null) {
                Atom atom = new Atom(ii, AtomicNumber.C);
                atom.setPoint(points[ii]);
                atoms.add(atom);
            }
        }

        Compound compound = new Compound(atoms);

        for (int ii = 0; ii < points.length - 1; ++ii) {
            compound.makeBond(ii, ii + 1, Bond.BondType.SINGLE);
        }

        return compound;
    }

    public static void makeBond(Compound c1, Atom a1, Compound c2, Atom a2) {
        if (a1.getAtomicNumber() == AtomicNumber.H || a2.getAtomicNumber() == AtomicNumber.H) {
            Notifier.instance().notification("Can NOT make bond to H");
        } else if (a1.getBondType(a2) == Bond.BondType.NONE) {
            // only make the bond if there is none
            a1.setBond(a2, Bond.BondType.SINGLE);
            if (c1 != c2) {
                merge(c1, c2);
            }
        } else {
            Notifier.instance().notification("Already has a bond");
        }
    }

    public static void alkaneToCyclo(Compound compound, Atom[] atoms, PointF lookingPoint) {
        // lookingPoint resides outside of polygon, and polygon is arranged to look at it
        int lastAtom = atoms.length - 1;

        atoms[0].singleBond(atoms[lastAtom]);

        // delete H at first and the last
        CompoundReactor.deleteAtom(compound, atoms[0].getHydrogen());
        CompoundReactor.deleteAtom(compound, atoms[lastAtom].getHydrogen());
        // adjust position of C
        float interiorAngleOfPolygon = Geometry.interiorAngleOfPolygon(atoms.length);

        atoms[1].setPoint(Geometry.cwRotate(lookingPoint, atoms[0].getPoint(), (MathConstant.RADIAN_360 - interiorAngleOfPolygon) / 2));
        for (int ii = 2; ii < atoms.length; ++ii) {
            atoms[ii].setPoint(Geometry.cwRotate(atoms[ii - 2].getPoint(), atoms[ii - 1].getPoint(), MathConstant.RADIAN_360 - interiorAngleOfPolygon));
        }

        // adjust position of H
        for (int ii = 1; ii < atoms.length; ++ii) {
            CompoundArranger.adjustHydrogenPosition(atoms[ii]);
        }
    }

    public static void alkaneToConjugated(Compound compound, Atom[] atoms, int conjStartIndex) {
        for (int ii = conjStartIndex; ii < atoms.length; ii += 2) {
            int nextIndex = (ii + 1) % atoms.length;

            atoms[ii].setBond(atoms[nextIndex], Bond.BondType.DOUBLE);

            CompoundReactor.deleteAtom(compound, atoms[ii].getHydrogen());
            CompoundReactor.deleteAtom(compound, atoms[nextIndex].getHydrogen());

            CompoundArranger.adjustHydrogenPosition(atoms[ii]);
            CompoundArranger.adjustHydrogenPosition(atoms[nextIndex]);
        }
    }

    public static void deleteAllHydrogen(Compound compound) {
        // cannot iterate through compound.getAtoms() due to Concurrent Modification
        List<Atom> hydrogens = CompoundInspector.allHydrogens(compound);

        for (Atom h : hydrogens) {
            if (h.numberOfBonds() == 1) {
                // hydrogen with only one bond is deleted. this is because that If H has more than one bond, it can split the compound
                h.cutBond(h.getSkeletonAtom());
                compound.removeAtom(h);
            }
        }
    }

    public static void deleteAllHydrogen(Compound compound, Atom atom) {
        // cannot iterating through atom.getBonds(). Concurrent Modification error since it calls cutBond();
        List<Atom> hydrogens = CompoundInspector.allHydrogens(atom);

        for (Atom h : hydrogens) {
            if (h.numberOfBonds() == 1) {
                // hydrogen with only one bond is deleted. this is because that If H has more than one bond, it can split the compound
                h.cutBond(h.getSkeletonAtom());
                compound.removeAtom(h);
            }
        }
    }

    public static void deleteHydrogen(Compound compound, Atom atom, int hDeleteNum) {
        List<Atom> hydrogens = CompoundInspector.allHydrogens(atom);

        int deleteNum = 0;

        for (Atom h : hydrogens) {
            if (deleteNum >= hDeleteNum)
                break;
            CompoundReactor.deleteAtom(compound, h);
            ++deleteNum;
        }
    }

    public static void saturateWithHydrogen(Compound compound, Atom atom, int maxBounds) {
        int curBounds = CompoundInspector.numberOfBonds(atom);

        if (curBounds > maxBounds) {
            deleteHydrogen(compound, atom, curBounds - maxBounds);
        } else {
            for (int ii = curBounds + 1; ii <= maxBounds; ++ii) {
                Atom H = new Atom(-1, AtomicNumber.H);

                H.getAtomDecoration().setShowElementName(false);
                compound.addAtom(atom, Bond.BondType.SINGLE, H);
            }
        }

        CompoundArranger.adjustHydrogenPosition(atom);
    }

    public static void saturateWithHydrogen(Compound compound, AtomicNumber an, int maxBounds) {
        List<Atom> targetAtom = new ArrayList<>();

        for (Atom atom : compound.getAtoms()) {
            if (atom.getAtomicNumber() == an)
                targetAtom.add(atom);
        }
        // separated list is needed since saturateWithHydrogen() modifies the iterating list, that leads to undefined behavior
        for (Atom atom : targetAtom) {
            saturateWithHydrogen(compound, atom, maxBounds);
        }
    }

    public static Compound saturateWithHydrogen(Compound compound) {
        saturateWithHydrogen(compound, AtomicNumber.C, 4);
        saturateWithHydrogen(compound, AtomicNumber.O, 2);
        saturateWithHydrogen(compound, AtomicNumber.N, 3);

        return compound;
    }

    public static void addCyclicToBond(Compound compound, Edge edge, int edgeNumber, boolean oppositeSite, boolean deleteHydrogenBeforeAdd, boolean saturateWithHydrogen) {
        float interiorAngle = Geometry.interiorAngleOfPolygon(edgeNumber) * (oppositeSite ? -1 : 1);
        Atom centerAtom = edge.atomInUpperDirection();  // the upper Atom in x axis is the center of the rotation
        Atom rotatingAtom = (centerAtom == edge.first) ? edge.second : edge.first;
        Atom lastAtom = rotatingAtom;

        if (deleteHydrogenBeforeAdd) {
            deleteHydrogen(compound, edge.first, 1);
            deleteHydrogen(compound, edge.second, 1);
        }

        List<Atom> newAtomList = new ArrayList<>();

        for (int ii = 0; ii < edgeNumber - 2; ++ii) {
            Atom newAtom = new Atom(-1, AtomicNumber.C);

            newAtomList.add(newAtom);

            newAtom.setPoint(Geometry.cwRotate(rotatingAtom.getPoint(), centerAtom.getPoint(), interiorAngle));
            newAtom.singleBond(centerAtom);
            newAtom.getAtomDecoration().setShowElementName(false);
            compound.addAtom(newAtom);

            rotatingAtom = centerAtom;
            centerAtom = newAtom;
        }

        centerAtom.singleBond(lastAtom);

        if (saturateWithHydrogen) {
            // this shall be called after all bonds are made
            for (Atom newAtom : newAtomList) {
                saturateWithHydrogen(compound, newAtom, 4);
                CompoundArranger.showAllHydrogen(newAtom, false);
            }
        }
    }

    public static void addFunctionalGroupToAtom(Compound compound, Atom atom, IFunctionalGroup funcGroup, boolean deleteHOfSelectedAtom) {
        merge(compound, funcGroup.curForm());
        atom.setBond(funcGroup.appendAtom(), funcGroup.bondType());

        Atom H = atom.getHydrogen();

        if (deleteHOfSelectedAtom && H != null) {
            deleteAtom(compound, H);
            CompoundArranger.adjustHydrogenPosition(atom);
        }
    }

    public static void deleteBond(Compound compound, Edge edge) {
        edge.first.cutBond(edge.second);
        splitAndUpdateProject(compound);
    }

    public static void deleteAtom(Compound compound, Atom atom) {
        int origBondNumber = atom.numberOfBonds();

        deleteAtomWithoutSplitting(compound, atom);
        // when deleting a atom, the compound may be split into multiple compound
        if (origBondNumber > 1) {
            splitAndUpdateProject(compound);
        }
    }

    public static void deleteAtoms(List<Atom> atoms) {
        for (Atom atom : atoms) {
            Compound compound = Project.instance().findCompound(atom);

            if (compound != null) {
                // compound can be null if it is H and deleted with C
                deleteAtom(compound, atom);
            }
        }
    }

    public static void merge(Compound merger, Compound mergee) {
        merger.addAtoms(mergee);
        Project.instance().removeCompound(mergee);
    }
}
