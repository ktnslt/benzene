package com.coldradio.benzene.compound;

import android.graphics.PointF;

import com.coldradio.benzene.lib.Geometry;
import com.coldradio.benzene.lib.TreeTraveler;
import com.coldradio.benzene.project.Configuration;

import java.util.List;

public class CompoundFactory {
    public static void cyclicGeometry(List<Atom> atoms) {
        if (atoms.size() >= 3) {
            PointF currentPoint = new PointF(0, 0);
            PointF nextPoint = new PointF();
            float interiorDegree = Geometry.interiorDegreeOfPolygon(atoms.size());

            if (atoms.size() == 6 || atoms.size() % 2 == 1) {
                nextPoint.set(Configuration.LINE_LENGTH * (float) Math.sin(Math.toRadians(interiorDegree / 2)),
                        Configuration.LINE_LENGTH * (float) Math.cos(Math.toRadians(interiorDegree / 2)));
            } else { // even number of sides polygon except hexagon
                nextPoint.set(Configuration.LINE_LENGTH, 0);
            }

            atoms.get(0).setPoint(currentPoint);
            atoms.get(1).setPoint(nextPoint);

            for (int ii = 2; ii < atoms.size(); ++ii) {
                PointF nextNextPoint = Geometry.rotatePoint(currentPoint, nextPoint, (float)Math.toRadians(360 - interiorDegree));

                atoms.get(ii).setPoint(nextNextPoint);
                currentPoint = nextPoint;
                nextPoint = nextNextPoint;
            }
        }
    }

    public static void alkaneGeometry(List<Atom> atoms, boolean upDirection) {
        PointF currentPoint = new PointF(0, 0);

        for (Atom atom : atoms) {
            atom.setPoint(currentPoint);
            currentPoint = new PointF(TreeTraveler.MathConstant.ROOT_3 / 2 * Configuration.LINE_LENGTH + currentPoint.x,
                    Configuration.LINE_LENGTH / 2.0f * (upDirection ? -1 : 1) + currentPoint.y);
            upDirection = !upDirection;
        }
    }

    public static Compound alkane(int carbonNumber, float x, float y) {
        Compound c = new ChainCompound(carbonNumber);

        c.offset(x, y);
        return c;
    }

    public static Compound cyclicAlkane(int carbonNumber, float x, float y) {
        Compound c = new CyclicCompound(carbonNumber);

        c.offset(x, y);
        return c;
    }

    public static Compound conjugatedCyclicAlkane(int carbonNumber, float x, float y) {
        Compound c = new ConjuatedCyclicCompound(carbonNumber);

        c.offset(x, y);;
        return c;
    }

    public static Compound propane(float x, float y) {
        return alkane(3, x, y);
    }

    public static Compound butane(float x, float y) {
        return alkane(4, x, y);
    }

    public static Compound benzene(float x, float y) {
        return conjugatedCyclicAlkane(6, x, y);
    }
}
