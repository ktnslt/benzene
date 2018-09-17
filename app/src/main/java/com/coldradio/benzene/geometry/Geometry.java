package com.coldradio.benzene.geometry;

import android.graphics.PointF;

import com.coldradio.benzene.compound.Atom;
import com.coldradio.benzene.project.Configuration;

import java.util.List;

public class Geometry {
    // TODO below two methods shall be moved to remove the dependency on Atom
    public static void cycloGeometry(List<Atom> atoms) {
        if (atoms.size() >= 3) {
            PointF currentPoint = new PointF(0, 0);
            PointF nextPoint = new PointF();
            float interiorAngle = Geometry.interiorAngleOfPolygon(atoms.size());

            if (atoms.size() == 6 || atoms.size() % 2 == 1) {
                nextPoint.set(Configuration.LINE_LENGTH * (float) Math.sin(Math.toRadians(interiorAngle / 2)),
                        Configuration.LINE_LENGTH * (float) Math.cos(Math.toRadians(interiorAngle / 2)));
            } else { // even number of sides polygon except hexagon
                nextPoint.set(Configuration.LINE_LENGTH, 0);
            }

            atoms.get(0).setInitialPoint(currentPoint);
            atoms.get(1).setInitialPoint(nextPoint);

            for (int ii = 2; ii < atoms.size(); ++ii) {
                PointF nextNextPoint = Geometry.rotatePointByDegree(currentPoint, nextPoint, 360 - interiorAngle);

                atoms.get(ii).setInitialPoint(nextNextPoint);
                currentPoint = nextPoint;
                nextPoint = nextNextPoint;
            }
        }
    }

    public static void alkaneGeometry(List<Atom> atoms, boolean upDirection) {
        PointF currentPoint = new PointF(0, 0);

        for (Atom atom : atoms) {
            atom.setInitialPoint(currentPoint);
            currentPoint = new PointF(MathConstant.ROOT_3 / 2 * Configuration.LINE_LENGTH + currentPoint.x,
                    Configuration.LINE_LENGTH / 2.0f * (upDirection ? -1 : 1) + currentPoint.y);
            upDirection = !upDirection;
        }
    }

    public static PointF rotatePointByDegree(PointF current, PointF center, float degree) {
        float currentToZeroX = current.x - center.x;
        float currentToZeroY = current.y - center.y;
        float cos_theta = (float) Math.cos(Math.toRadians(degree)), sin_theta = (float) Math.sin(Math.toRadians(degree));

        return new PointF(currentToZeroX * cos_theta - currentToZeroY * sin_theta + center.x,
                currentToZeroY * cos_theta + currentToZeroX * sin_theta + center.y);
    }

    public static float distanceFromPointToPoint(PointF p1, PointF p2) {
        return (float) Math.sqrt((p2.y - p1.y) * (p2.y - p1.y) + (p2.x - p1.x) * (p2.x - p1.x));
    }

    public static float distanceFromPointToLine(PointF p0, PointF p1, PointF p2) {
        return Math.abs((p2.y - p1.y) * p0.x - (p2.x - p1.x) * p0.y + p2.x * p1.y - p2.y * p1.x) / distanceFromPointToPoint(p1, p2);
    }

    public static PointF zoomOut(float x, float y, PointF center, float ratio) {
        float x_dot = x - center.x, y_dot = y - center.y;

        return new PointF(x_dot * ratio + center.x, y_dot * ratio + center.y);
    }

    public static boolean sameSideOfLine(PointF p1, PointF p2, PointF l1, PointF l2) {
        return whichSideOfLine(p1, l1, l2) * whichSideOfLine(p2, l1, l2) > 0;
    }

    public static float whichSideOfLine(PointF p, PointF l1, PointF l2) {
        return (p.x - l1.x) * (l2.y - l1.y) - (p.y - l1.y) * (l2.x - l1.x);
    }

    public static PointF centerPoint(PointF p1, PointF p2) {
        return new PointF((p1.x + p2.x) / 2, (p1.y + p2.y) / 2);
    }

    public static PointF symmetricPointToLine(PointF p, PointF l1, PointF l2) {
        float args[] = lineEquationFrom2Points(l1, l2);
        // https://math.stackexchange.com/questions/1013230/how-to-find-coordinates-of-reflected-point
        return new PointF((p.x * (args[1] * args[1] - args[0] * args[0]) - 2 * args[0] * (args[1] * p.y + args[2])) / (args[1] * args[1] + args[0] * args[0]),
                (p.y * (args[0] * args[0] - args[1] * args[1]) - 2 * args[1] * (args[0] * p.x + args[2])) / (args[1] * args[1] + args[0] * args[0]));
    }

    public static float[] lineEquationFrom2Points(PointF p1, PointF p2) {
        float[] args;

        if (p1 == p2) {
            args = new float[]{0, 0, 0};
        } else if (p1.x == p2.x) {
            args = new float[]{1, 0, -p1.x};
        } else if (p1.y == p2.y) {
            args = new float[]{0, 1, -p1.y};
        } else {
            args = new float[]{1, -(p1.x - p2.x) / (p1.y - p2.y), (p1.x - p2.x) / (p1.y - p2.y) * p1.y - p1.x};
        }

        return args;
    }

    public static float interiorAngleOfPolygon(int numberOfSide) {
        return (180.0f * numberOfSide - 360) / numberOfSide;
    }

    public static PointF[] regularTrianglePoint(PointF p1, PointF p2) {
        return new PointF[]{rotatePointByDegree(p1, p2, 60), rotatePointByDegree(p1, p2, -60)};
    }

    public static float degreeOfTriangle(PointF p1, PointF p2, PointF center) {
        float a = distanceFromPointToPoint(p1, center);
        float b = distanceFromPointToPoint(p2, center);
        float c = distanceFromPointToPoint(p1, p2);
        float cos_theta = (a*a + b*b - c*c) / (2*a*b);

        return (float)Math.toDegrees(Math.acos(cos_theta));

    }
}
