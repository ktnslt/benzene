package com.coldradio.benzene.compound;

import android.graphics.PointF;

import com.coldradio.benzene.compound.funcgroup.IFunctionalGroup;
import com.coldradio.benzene.library.rule.RuleSet;
import com.coldradio.benzene.project.Project;
import com.coldradio.benzene.util.Geometry;
import com.coldradio.benzene.util.MathConstant;
import com.coldradio.benzene.util.Notifier;

import java.util.ArrayList;
import java.util.List;

public class CompoundReactor {
    private static void splitAndUpdateProject(Compound compound) {
        Project.instance().addCompounds(RuleSet.instance().apply(CompoundInspector.split(compound)));
        Project.instance().removeCompound(compound);
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
        if (a1.getBondType(a2) == Bond.BondType.NONE) {
            // only make the bond if there is none
            a1.setBond(a2, Bond.BondType.SINGLE);
            if (c1 != c2) {
                c1.merge(c2);
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
        CompoundReactor.deleteAndCutBonds(compound, atoms[0].getHydrogen());
        CompoundReactor.deleteAndCutBonds(compound, atoms[lastAtom].getHydrogen());
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

            CompoundReactor.deleteAndCutBonds(compound, atoms[ii].getHydrogen());
            CompoundReactor.deleteAndCutBonds(compound, atoms[nextIndex].getHydrogen());

            CompoundArranger.adjustHydrogenPosition(atoms[ii]);
            CompoundArranger.adjustHydrogenPosition(atoms[nextIndex]);
        }
    }

    public static void deleteAllHydrogen(Compound compound) {
        List<Atom> hydrogens = CompoundInspector.allHydrogens(compound);

        for (Atom h : hydrogens) {
            CompoundReactor.deleteAndCutBonds(compound, h);
        }
    }

    public static void deleteAllHydrogen(Compound compound, Atom atom) {
        List<Atom> hydrogens = CompoundInspector.allHydrogens(atom);

        for (Atom h : hydrogens) {
            CompoundReactor.deleteAndCutBonds(compound, h);
        }
    }

    public static void deleteHydrogen(Compound compound, Atom atom, int hDeleteNum) {
        List<Atom> hydrogens = CompoundInspector.allHydrogens(atom);

        int deleteNum = 0;

        for (Atom h : hydrogens) {
            if (deleteNum >= hDeleteNum)
                break;
            CompoundReactor.deleteAndCutBonds(compound, h);
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

        if (curBounds != maxBounds) {
            CompoundArranger.adjustHydrogenPosition(atom);
        }
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
        compound.merge(funcGroup.curForm());
        atom.setBond(funcGroup.appendAtom(), funcGroup.bondType());

        Atom H = atom.getHydrogen();

        if (deleteHOfSelectedAtom && H != null) {
            deleteAndCutBonds(compound, H);
            CompoundArranger.adjustHydrogenPosition(atom);
        }
    }

    public static void deleteBond(Compound compound, Edge edge) {
        edge.first.cutBond(edge.second);
        splitAndUpdateProject(compound);
    }

    public static void deleteAndCutBonds(Compound compound, Atom atom) {
        int origBondNumber = atom.bondNumber();

        CompoundReactor.deleteAllHydrogen(compound, atom);
        // cut all bonds - iterating bonds doesn't work due to the concurrent modification
        //        for (Bond bond : atom.getBonds())
        //            atom.cutBond(bond.getBoundAtom());
        for (Atom other : compound.getAtoms()) {
            if (other != atom) {
                other.cutBond(atom);
            }
        }
        // delete atom
        compound.removeAtom(atom);
        // when deleting a atom, the compound may be split into multiple compound
        if (origBondNumber > 1) {
            splitAndUpdateProject(compound);
        }
    }
}
