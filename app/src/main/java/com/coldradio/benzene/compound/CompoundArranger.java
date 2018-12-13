package com.coldradio.benzene.compound;

import android.graphics.PointF;

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
            Atom a1 = compound.getAtoms().get(0);
            Atom a2 = a1.getBonds().get(0).getBoundAtom();

            return Geometry.distanceFromPointToPoint(a1.getPoint(), a2.getPoint());
        }
    }

    public static Compound alignCenter(Compound compound, PointF center) {
        PointF compoundCenter = compound.centerOfRectangle();

        compound.offset(center.x - compoundCenter.x, center.y - compoundCenter.y);

        return compound;
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
                if (a1.getBondType(a2) == Bond.BondType.DOUBLE && (a1.bondNumber() == 1 || a2.bondNumber() == 1)) {
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
        return Geometry.cwRotate(zoomed_b, a_atom, h_num == 1 ? MathConstant.RADIAN_110 : MathConstant.RADIAN_130);
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
        int boundSkeleton = atom.numberOfBoundSkeletonAtoms();
        List<Atom> hydrogens = atom.boundHydrogens();

        if (boundSkeleton == 1) {
            // END of chain case
            PointF b_atom = atom.getSkeletonAtom().getPoint();
            int hNum = 1;

            for (Atom h : hydrogens) {
                h.setPoint(hydrogenPointOfEnd(atom.getPoint(), b_atom, hNum++));
            }
        } else if (boundSkeleton == 2) {
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
        }
    }
}