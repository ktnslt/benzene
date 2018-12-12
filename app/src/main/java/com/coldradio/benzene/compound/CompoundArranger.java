package com.coldradio.benzene.compound;

import android.graphics.PointF;

import com.coldradio.benzene.util.Geometry;
import com.coldradio.benzene.util.TreeTraveler;
import com.coldradio.benzene.project.Configuration;

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
}