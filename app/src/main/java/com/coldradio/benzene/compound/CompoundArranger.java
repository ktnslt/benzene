package com.coldradio.benzene.compound;

import android.graphics.PointF;

import com.coldradio.benzene.lib.Geometry;
import com.coldradio.benzene.lib.ScreenInfo;
import com.coldradio.benzene.project.Configuration;

public class CompoundArranger {
    private static float atomDistance(Compound compound) {
        if (compound.size() == 1) {
            return Configuration.LINE_LENGTH;
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
        zoom(compound, Configuration.LINE_LENGTH / atomDistance(compound) * ratio);

        return compound;
    }
}