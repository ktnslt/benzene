package com.coldradio.benzene.compound;

import android.graphics.PointF;
import android.graphics.RectF;

import com.coldradio.benzene.util.Geometry;
import com.coldradio.benzene.util.MathConstant;
import com.coldradio.benzene.util.TreeTraveler;
import com.coldradio.benzene.project.Configuration;

import java.util.List;

public class CompoundArranger {
    private static float atomDistance(Compound compound) {
        if (compound.size() == 1) {
            return Configuration.BOND_LENGTH;
        } else {
            Atom a1 = CompoundInspector.anySkeletonAtom(compound);

            if (a1 != null) {
                Atom a2 = a1.getBonds().get(0).getBoundAtom();

                return Geometry.distanceFromPointToPoint(a1.getPoint(), a2.getPoint());
            } else {
                return Configuration.BOND_LENGTH;
            }
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

    public static Compound zoomToStandard(Compound compound, float ratio) {
        zoom(compound, Configuration.BOND_LENGTH / atomDistance(compound) * ratio);

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
        for (Atom atom : compound.getAtoms())
            atom.setPoint(Geometry.cwRotate(atom.getPoint(), compound.centerOfRectangle(), angle));

        return compound;
    }

    public static Compound rotate(Compound compound, PointF center, float angle) {
        for (Atom atom : compound.getAtoms())
            atom.setPoint(Geometry.cwRotate(atom.getPoint(), center, angle));

        return compound;
    }

    public static PointF hydrogenPointOfEnd(PointF a_atom, PointF b_atom, int h_num) {
        PointF zoomed_b_atom = Geometry.zoom(b_atom, a_atom, Configuration.H_BOND_LENGTH_RATIO);

        return Geometry.cwRotate(zoomed_b_atom, a_atom, MathConstant.RADIAN_90 * h_num);
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
            int hNum = 1;
            PointF initPoint = new PointF(atom.getPoint().x, atom.getPoint().y + Configuration.BOND_LENGTH);

            for (Atom h : hydrogens) {
                h.setPoint(hydrogenPointOfEnd(atom.getPoint(), initPoint, hNum++));
            }
        } else {
            // END of chain case, flat triangle, etc.
            PointF b_atom = atom.getSkeletonAtom().getPoint();
            int hNum = 1;

            for (Atom h : hydrogens) {
                h.setPoint(hydrogenPointOfEnd(atom.getPoint(), b_atom, hNum++));
            }
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
            TreeTraveler.travelIfTrue(new TreeTraveler.IAtomVisitor() {
                @Override
                public boolean visit(Atom atom, Object... args) {
                    if (atom != edgeAtom) {
                        atom.setPoint(Geometry.symmetricToLine(atom.getPoint(), l1, l2));
                        return true;
                    } else {
                        return false;
                    }
                }
            }, fromAtom);

            compound.positionModified();
        }
    }

    public static void flipHydrogen(Atom atom) {
        PointF center = atom.getPoint();

        for (Bond bond : atom.getBonds()) {
            Atom h = bond.getBoundAtom();

            if (h.getPoint().x == atom.getPoint().x) {
                // in equal case, this h counts for right side. make it + 0.01f since it is flipped to other side by symmetricToPoint();
                h.getPoint().x += 0.001f;
            }
            if (h.getAtomicNumber() == AtomicNumber.H) {
                h.setPoint(Geometry.symmetricToPoint(h.getPoint(), center));
            }
        }
    }
}