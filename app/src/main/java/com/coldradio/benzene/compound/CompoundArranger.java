package com.coldradio.benzene.compound;

import android.graphics.PointF;
import android.graphics.RectF;

import com.coldradio.benzene.util.Geometry;
import com.coldradio.benzene.util.MathConstant;
import com.coldradio.benzene.util.TreeTraveler;
import com.coldradio.benzene.project.Configuration;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class CompoundArranger {
    private static PointF msTempPoint = new PointF();

    private static PointF hydrogenPointOfEnd(PointF center, PointF b_atom, int h_num, float degree) {
        PointF zoomed_b_atom = Geometry.zoom(b_atom, center, Configuration.H_BOND_LENGTH_RATIO);

        return Geometry.cwRotate(zoomed_b_atom, center, degree * h_num);
    }

    private static void setHydrogenPoint(List<Atom> hydrogens, PointF center, PointF rotationStart, float degree) {
        int hNum = 1;

        for (Atom h : hydrogens) {
            h.setPoint(hydrogenPointOfEnd(center, rotationStart, hNum++, degree));
        }
    }

    public static Compound alignCenter(Compound compound, PointF center) {
        PointF compoundCenter = compound.centerOfRectangle();

        compound.offset(center.x - compoundCenter.x, center.y - compoundCenter.y);

        return compound;
    }

    public static PointF center(List<Compound> compoundList) {
        if (compoundList.size() > 0) {
            RectF allRegion = compoundList.get(0).rectRegion();

            for (Compound compound : compoundList) {
                RectF region = compound.rectRegion();

                allRegion.union(region);
            }

            return new PointF(allRegion.centerX(), allRegion.centerY());
        } else {
            return new PointF(0, 0); // TODO: return the center of the screen?
        }
    }

    public static Compound zoom(Compound compound, float ratio) {
        if (compound.size() > 1) {
            PointF center = compound.centerOfRectangle();

            for (Atom atom : compound.getAtoms()) {
                PointF point = atom.getPoint();

                atom.setPoint(Geometry.zoom(point.x, point.y, center, ratio));
            }
        }
        return compound;
    }

    public static Compound adjustDoubleBondType(Compound compound) {
        TreeTraveler.returnFirstEdge(new TreeTraveler.IEdgeVisitor() {
            @Override
            public boolean visit(Atom a1, Atom a2, Object... args) {
                if (a1.getBondType(a2) == Bond.BondType.DOUBLE && (a1.numberOfBonds() == 1 || a2.numberOfBonds() == 1)) {
                    // C==O case
                    a1.setBond(a2, Bond.BondType.DOUBLE_MIDDLE);
                } else if (a1.numberOfBonds() == 2 && CompoundInspector.allBondsAreDouble(a1)) {
                    // CH2=C=CH2 case
                    a1.setBond(a2, Bond.BondType.DOUBLE_MIDDLE);
                } else if (a2.numberOfBonds() == 2 && CompoundInspector.allBondsAreDouble(a2)) {
                    a1.setBond(a2, Bond.BondType.DOUBLE_MIDDLE);
                }
                return false;
            }
        }, compound);

        return compound;
    }

    public static Compound rotateByCenterOfRectangle(Compound compound, float angle) {
        for (Atom atom : compound.getAtoms()) {
            atom.setPoint(Geometry.cwRotate(atom.getPoint(), compound.centerOfRectangle(), angle));
        }
        // here putAllHydrogenOppositeToSkeleton() cannot be called.
        // basic idea was to align hydrogens for every rotation since rotation can change the alignment (all hydrogens are opposite to the skeleton atoms)
        // if putAllHydrogenOppositeToSkeleton(), the position of H merges to each other. that is the problem
        return compound;
    }

    public static Compound rotate(Compound compound, PointF center, float angle) {
        for (Atom atom : compound.getAtoms())
            atom.setPoint(Geometry.cwRotate(atom.getPoint(), center, angle));

        return compound;
    }

    public static PointF hydrogenPointOfEnd(PointF a_atom, PointF b_atom, int h_num) {
        return hydrogenPointOfEnd(a_atom, b_atom, h_num, MathConstant.RADIAN_90);
    }

    public static PointF hydrogenPointOfBentForm(PointF a_atom, PointF b1_atom, PointF b2_atom, int h_num) {
        // C---C---C    append to a_atom
        // b1  a   b2
        float b1b2Angle = Geometry.cwAngle(b1_atom, b2_atom, a_atom);

        PointF zoomed_b;
        if (b1b2Angle > MathConstant.RADIAN_180) {
            zoomed_b = Geometry.zoom(b1_atom, a_atom, Configuration.H_BOND_LENGTH_RATIO);
        } else {
            zoomed_b = Geometry.zoom(b2_atom, a_atom, Configuration.H_BOND_LENGTH_RATIO);
        }
        return Geometry.cwRotate(zoomed_b, a_atom, h_num == 1 ? MathConstant.RADIAN_90 : MathConstant.RADIAN_150);
    }

    public static PointF hydrogenUniquePointOfBentForm(PointF a_atom, PointF b1_atom, PointF b2_atom) {
        // TODO: instead of receiving point, it would be better to receive Atom, Then this and the above function can be merged
        // C---C---C    append to a_atom. Since this is unique point
        // b1  a   b2
        float b1b2Angle = Geometry.cwAngle(b1_atom, b2_atom, a_atom);

        PointF zoomed_b;
        if (b1b2Angle > MathConstant.RADIAN_180) {
            zoomed_b = Geometry.zoom(b1_atom, a_atom, Configuration.H_BOND_LENGTH_RATIO);
        } else {
            zoomed_b = Geometry.zoom(b2_atom, a_atom, Configuration.H_BOND_LENGTH_RATIO);
        }
        return Geometry.cwRotate(zoomed_b, a_atom, MathConstant.RADIAN_120);
    }

    public static void adjustHydrogenPosition(Atom atom) {
        int boundSkeleton = CompoundInspector.numberOfSkeletonAtoms(atom);
        List<Atom> hydrogens = CompoundInspector.allHydrogens(atom);
        boolean notAdjusted = false;

        if (boundSkeleton == 2) {
            // Bent form
            Atom b1_atom = atom.getSkeletonAtom();
            Atom b2_atom = atom.getSkeletonAtomExcept(b1_atom);
            int hNum = 1;
            boolean hasDoubleOrTripleBond = atom.hasBondType(Bond.BondType.DOUBLE) || atom.hasBondType(Bond.BondType.TRIPLE);

            for (Atom h : hydrogens) {
                if (hasDoubleOrTripleBond) {
                    h.setPoint(hydrogenUniquePointOfBentForm(atom.getPoint(), b1_atom.getPoint(), b2_atom.getPoint()));
                } else {
                    h.setPoint(hydrogenPointOfBentForm(atom.getPoint(), b1_atom.getPoint(), b2_atom.getPoint(), hNum++));
                }
            }
        } else if (boundSkeleton == 0) {
            // CH4
            PointF initPoint = new PointF(atom.getPoint().x, atom.getPoint().y + Configuration.BOND_LENGTH);

            setHydrogenPoint(hydrogens, atom.getPoint(), initPoint, MathConstant.RADIAN_90);
        } else if (boundSkeleton == 1) {
            Atom boundAtom = atom.getSkeletonAtom();
            Bond bond = atom.findBond(boundAtom);

            if (bond.getBondType() == Bond.BondType.TRIPLE && hydrogens.size() == 1) {
                setHydrogenPoint(hydrogens, atom.getPoint(), boundAtom.getPoint(), MathConstant.RADIAN_180);
            } else if (hydrogens.size() <= 2) {
                setHydrogenPoint(hydrogens, atom.getPoint(), boundAtom.getPoint(), MathConstant.RADIAN_120);
            } else {
                notAdjusted = true;
            }
        } else {
            notAdjusted = true;
        }

        if (notAdjusted) {
            // apply default. number of skeleton atom is 1, 3, etc
            setHydrogenPoint(hydrogens, atom.getPoint(), atom.getSkeletonAtom().getPoint(), MathConstant.RADIAN_90);
        }
    }

    public static void toggleShowHydrogen(Atom atom) {
        showAllHydrogen(atom, ! CompoundInspector.showAnyHydrogen(atom));
    }

    public static void showAllHydrogen(Atom atom, boolean show) {
        for (Bond bond : atom.getBonds()) {
            Atom h = bond.getBoundAtom();

            if (h.getAtomicNumber() == AtomicNumber.H)
                h.getAtomDecoration().setShowElementName(show);
        }
    }

    public static void offsetWithHiddenHydrogen(Atom atom, float dx, float dy) {
        atom.offset(dx, dy);
        for (Bond bond : atom.getBonds()) {
            Atom boundAtom = bond.getBoundAtom();

            if (boundAtom.getAtomicNumber() == AtomicNumber.H && ! boundAtom.isVisible()) {
                boundAtom.offset(dx, dy);
            }
        }
    }

    public static void flipBond(Atom fromAtom, final Atom edgeAtom, Compound compound) {
        // fromAtom and edgeAtom has an bond
        if (compound != null) {
            final PointF l1 = fromAtom.getPoint(), l2 = edgeAtom.getPoint();

            // fromAtom and edgeAtom shall have a bond. All atoms linked to fromAtom will be flipped here
            TreeTraveler.returnFirstAtom(new TreeTraveler.IAtomVisitor() {
                @Override
                public boolean visit(Atom atom, int distanceFromRoot, Object... args) {
                    atom.setPoint(Geometry.symmetricToLine(atom.getPoint(), l1, l2));
                    return false;
                }

                @Override
                public boolean travelDown(Atom atom, int distanceFromRoot, Object... args) {
                    return atom != edgeAtom;
                }
            }, fromAtom);

            compound.positionModified();
        }
    }

    public static void flipHydrogen(Atom atom) {
        // TODO: there are exceptional cases; e.g., if NH2 would have one H in right, the other in left, this function has no effects. one H and three Hs are OK
        PointF l1 = atom.getPoint();
        PointF l2 = msTempPoint;

        l2.set(l1);
        l2.offset(0, 100);

        for (Bond bond : atom.getBonds()) {
            Atom h = bond.getBoundAtom();

            if (h.getAtomicNumber() == AtomicNumber.H) {
                h.setPoint(Geometry.symmetricToLine(h.getPoint(), l1, l2));
            }
        }
    }

    public static void putAllHydrogenOppositeToSkeleton(Atom atom) {
        if (CompoundInspector.numberOfSkeletonAtoms(atom) == 1) {
            Atom boundSkeletonAtom = atom.getSkeletonAtom();

            if (boundSkeletonAtom != null) {
                PointF allHShallBeOppositeToThisPoint = boundSkeletonAtom.getPoint();
                PointF l1 = atom.getPoint();
                PointF l2 = msTempPoint;

                l2.set(l1);
                l2.offset(0, -100);

                for (Bond bond : atom.getBonds()) {
                    Atom h = bond.getBoundAtom();

                    if (h.getAtomicNumber() == AtomicNumber.H) {
                        if (h.getPoint().x == atom.getPoint().x) {
                            h.getPoint().x += 0.001f;
                        }
                        if (Geometry.sameSideOfLine(allHShallBeOppositeToThisPoint, h.getPoint(), l1, l2)) {
                            h.setPoint(Geometry.symmetricToLine(h.getPoint(), l1, l2));
                        }
                    }
                }
            }
        }
    }
}