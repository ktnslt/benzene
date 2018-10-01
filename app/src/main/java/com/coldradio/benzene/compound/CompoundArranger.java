package com.coldradio.benzene.compound;

import android.graphics.PointF;

import com.coldradio.benzene.lib.Geometry;
import com.coldradio.benzene.lib.ScreenInfo;
import com.coldradio.benzene.project.Configuration;

public class CompoundArranger {
    private static float atomDistance(Compound compound) {
        return Geometry.distanceFromPointToPoint(compound.getAtoms().get(0).getPoint(), compound.getAtoms().get(1).getPoint());
    }

    private static void moveProperly(Compound compound) {
        PointF screenCenter = ScreenInfo.instance().centerPoint();
        PointF compoundCenter = compound.centerOfRectangle();

        compound.offset(screenCenter.x - compoundCenter.x, screenCenter.y - compoundCenter.y);
    }

    public static void zoomProperly(Compound compound) {
        if (compound.size() == 1)
            return;

        PointF center = compound.centerOfRectangle();
        float atomDistance = atomDistance(compound);

        for (Atom atom : compound.getAtoms()) {
            PointF point = atom.getPoint();

            atom.setPoint(Geometry.zoomOut(point.x, point.y, center, Configuration.LINE_LENGTH / atomDistance));
        }
    }

    public static void autoArrange(Compound compound) {
        zoomProperly(compound);
        moveProperly(compound);
    }
}