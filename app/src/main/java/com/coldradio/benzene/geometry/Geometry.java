package com.coldradio.benzene.geometry;

import android.graphics.PointF;
import com.coldradio.benzene.compound.Atom;
import com.coldradio.benzene.compound.Compound;
import com.coldradio.benzene.project.Configuration;
import java.util.List;

public class Geometry {
    public static void cycloHexaneGeometry(List<Atom> atoms) {
        PointF center = new PointF(0, Configuration.LINE_LENGTH);
        PointF currentPoint = new PointF(0, 0);
        // the first point shall be always (0, 0), and will be interpreted according to the mStartingPoint

        for(Atom atom : atoms) {
            atom.setPoint(currentPoint);
            currentPoint = rotatePointByDegree(currentPoint, center, 60);
        }
    }
    public static void alkaneGeometry(List<Atom> atoms, boolean upDirection) {
        PointF currentPoint = new PointF(0, 0);

        for(Atom atom : atoms) {
            atom.setPoint(currentPoint);
            currentPoint = new PointF(MathConstant.ROOT_3 / 2 * Configuration.LINE_LENGTH + currentPoint.x,
                                        Configuration.LINE_LENGTH / 2.0f * (upDirection ? -1 : 1) + currentPoint.y);
            upDirection = !upDirection;
        }
    }
    private static PointF rotatePointByDegree(PointF current, PointF center, float degree) {
        float currentToZeroX = current.x - center.x;
        float currentToZeroY = current.y - center.y;
        float cos_theta = (float)Math.cos(Math.toRadians(degree)), sin_theta = (float)Math.sin(Math.toRadians(degree));

        return new PointF(currentToZeroX * cos_theta - currentToZeroY * sin_theta + center.x,
                currentToZeroY * cos_theta + currentToZeroX * sin_theta + center.y);
    }
    private static float distanceFromPointToPoint(PointF p1, PointF p2) {
        return (float)Math.sqrt((p2.y - p1.y)*(p2.y - p1.y) + (p2.x - p1.x)*(p2.x - p1.x));
    }
    private static float distanceFromPointToLine(PointF p0, PointF p1, PointF p2) {
        return Math.abs((p2.y - p1.y) * p0.x - (p2.x - p1.x) * p0.y + p2.x * p1.y - p2.y * p1.x) / distanceFromPointToPoint(p1, p2);
    }
    public static boolean select(float x, float y, Compound compound) {
        PointF touchedPoint = new PointF(x, y);
        List<Atom> atoms = compound.getAtoms();

        for(int ii = 0; ii < atoms.size(); ++ii) {
            if(ii < atoms.size() - 1 || (ii == atoms.size() - 1 && compound.isCyclo())) {
                PointF p1 = atoms.get(ii).getPoint(), p2 = atoms.get((ii + 1) % atoms.size()).getPoint();
                float distanceToLine = distanceFromPointToLine(touchedPoint, p1, p2);

                if (distanceToLine < Configuration.SELECT_RANGE
                        && distanceFromPointToPoint(touchedPoint, p1) < (Configuration.LINE_LENGTH + Configuration.SELECT_RANGE)
                        && distanceFromPointToPoint(touchedPoint, p2) < (Configuration.LINE_LENGTH + Configuration.SELECT_RANGE)) {
                    return true;
                }
            }
        }
        return false;
    }
    public static PointF zoomOut(float x, float y, PointF center, float ratio) {
        float x_dot = x - center.x, y_dot = y - center.y;

        return new PointF(x_dot * ratio + center.x, y_dot * ratio + center.y);
    }
}
