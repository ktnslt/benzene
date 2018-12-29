package com.coldradio.benzene.compound;

import android.graphics.PointF;
import android.util.Pair;

import com.coldradio.benzene.compound.funcgroup.IFunctionalGroup;
import com.coldradio.benzene.util.Geometry;
import com.coldradio.benzene.util.MathConstant;

import java.util.ArrayList;
import java.util.List;

public class CompoundReactor {
    private Pair<Compound, Atom> mSynthesisSource = null;

    private Pair<Compound, Atom> selectSource(PointF point, List<Compound> compounds) {
        return null;
//        Compound selectedCompound = null;
//        Atom selectedAtom = null;
//
//        for (Compound compound : compounds) {
//            selectedAtom = compound.selectAtom(point);
//            if (selectedAtom != null) {
//                selectedCompound = compound;
//                break;
//            }
//        }
//
//        if(selectedAtom != null) {
//            return new Pair<>(selectedCompound, selectedAtom);
//        } else {
//            return null;
//        }
    }

    private void synthesis(Pair<Compound, Atom> source, Pair<Compound, Atom> destination) {
        if (source.first != destination.first) {
            destination.first.merge(source.first);
            destination.second.singleBond(source.second);
        } else {    // this will be cyclic
            destination.second.singleBond(source.second);
        }

        mSynthesisSource = null;
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

    public boolean synthesis(PointF point, List<Compound> compounds) {
        // TODO: when trying to synthesize two neighboring atoms that already have a bond, it is the case that changes the bond type (single -> double)
        // so. 1. block this case, or 2. redirect this case to "BOND"
        Pair<Compound, Atom> selectedSource = selectSource(point, compounds);

        if (mSynthesisSource == null) {
            mSynthesisSource = selectedSource;
        } else {
            if (selectedSource == null) {
                mSynthesisSource = null;
            } else {
                synthesis(mSynthesisSource, selectedSource);
            }
        }
        return true;
    }

    public PointF getSynthesisSourcePoint() {
        if (mSynthesisSource != null) {
            return mSynthesisSource.second.getPoint();
        }
        return null;
    }

    public static void alkaneToCyclo(Compound compound, Atom[] atoms, PointF lookingPoint) {
        // lookingPoint resides outside of polygon, and polygon is arranged to look at it
        int lastAtom = atoms.length - 1;

        atoms[0].singleBond(atoms[lastAtom]);

        // delete H at first and the last
        compound.delete(atoms[0].getHydrogen());
        compound.delete(atoms[lastAtom].getHydrogen());
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

            compound.delete(atoms[ii].getHydrogen());
            compound.delete(atoms[nextIndex].getHydrogen());

            CompoundArranger.adjustHydrogenPosition(atoms[ii]);
            CompoundArranger.adjustHydrogenPosition(atoms[nextIndex]);
        }
    }

    public static void deleteAllHydrogen(Compound compound) {
        List<Atom> hydrogens = CompoundInspector.allHydrogens(compound);

        for (Atom h : hydrogens) {
            compound.delete(h);
        }
    }

    public static void deleteAllHydrogen(Compound compound, Atom atom) {
        List<Atom> hydrogens = CompoundInspector.allHydrogens(atom);

        for (Atom h : hydrogens) {
            compound.delete(h);
        }
    }

    public static void deleteHydrogen(Compound compound, Atom atom, int hDeleteNum) {
        List<Atom> hydrogens = CompoundInspector.allHydrogens(atom);

        int deleteNum = 0;

        for (Atom h : hydrogens) {
            if (deleteNum >= hDeleteNum)
                break;
            compound.delete(h);
            ++deleteNum;
        }
    }

    public static void saturateWithHydrogen(Compound compound, Atom atom, int maxBounds) {
        int curBounds = atom.getBonds().size();

        for (int ii = curBounds + 1; ii <= maxBounds; ++ii) {
            compound.addAtom(atom, Bond.BondType.SINGLE, new Atom(-1, AtomicNumber.H));
        }

        CompoundArranger.adjustHydrogenPosition(atom);
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
            compound.delete(H);
            CompoundArranger.adjustHydrogenPosition(atom);
        }
    }
}
