package com.coldradio.benzene.util;

import android.graphics.PointF;

public class Geometry {
    public static PointF cwRotate(PointF current, PointF center, float angle) {
        float currentToZeroX = current.x - center.x;
        float currentToZeroY = current.y - center.y;
        float cos_theta = (float) Math.cos(angle), sin_theta = (float) Math.sin(angle);

        return new PointF(currentToZeroX * cos_theta - currentToZeroY * sin_theta + center.x,
                currentToZeroY * cos_theta + currentToZeroX * sin_theta + center.y);
    }

    public static float distanceFromPointToPoint(PointF p1, PointF p2) {
        return (float) Math.sqrt((p2.y - p1.y) * (p2.y - p1.y) + (p2.x - p1.x) * (p2.x - p1.x));
    }

    public static float distanceFromPointToLine(PointF p0, PointF p1, PointF p2) {
        return Math.abs((p2.y - p1.y) * p0.x - (p2.x - p1.x) * p0.y + p2.x * p1.y - p2.y * p1.x) / distanceFromPointToPoint(p1, p2);
    }

    public static PointF zoom(PointF point, PointF center, float ratio) {
        return zoom(point.x, point.y, center, ratio);
    }

    public static PointF zoom(float x, float y, PointF center, float ratio) {
        float x_dot = x - center.x, y_dot = y - center.y;

        return new PointF(x_dot * ratio + center.x, y_dot * ratio + center.y);
    }

    public static boolean sameSideOfLine(PointF p1, PointF p2, PointF l1, PointF l2) {
        return whichSideOfLine(p1, l1, l2) * whichSideOfLine(p2, l1, l2) > 0;
    }

    public static float whichSideOfLine(PointF p, PointF l1, PointF l2) {
        return (p.x - l1.x) * (l2.y - l1.y) - (p.y - l1.y) * (l2.x - l1.x);
    }

    public static PointF centerOfLine(PointF p1, PointF p2) {
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
        return (float) Math.toRadians((180.0f * numberOfSide - 360) / numberOfSide);
    }

    public static PointF[] regularTrianglePoint(PointF p1, PointF p2) {
        return new PointF[]{cwRotate(p1, p2, (float) Math.toRadians(60)), cwRotate(p1, p2, (float) Math.toRadians(-60))};
    }

    public static float angle(PointF from, PointF to, PointF center) {
        // this function returns positive or negative value depending on the rotation direction.
        // clock-wise is positive direction
        float pointFromX = from.x - center.x, pointFromY = from.y - center.y;
        float pointToX = to.x - center.x, pointToY = to.y - center.y;

        return (float) (Math.atan2(pointToY, pointToX) - Math.atan2(pointFromY, pointFromX));
    }

    public static float cwAngle(PointF from, PointF to, PointF center) {
        // this function returns always positive value
        float angle = angle(from, to, center);

        if (angle < 0)
            angle = MathConstant.RADIAN_360 + angle;
        return angle;
    }

    public static float center(float[] points) {
        float center = 0;

        for (float point : points) {
            center += point;
        }

        return center / points.length;
    }

    public static PointF[] lineOrthogonalShift(PointF l1, PointF l2, float shiftRatio, boolean up) {
        PointF[] shifted = new PointF[2];

        // rotate l2 90 Degrees against l1
        shifted[0] = cwRotate(l2, l1, MathConstant.RADIAN_90 * (up ? -1 : 1));
        shifted[0] = zoom(shifted[0].x, shifted[0].y, l1, shiftRatio);

        shifted[1] = cwRotate(l1, l2, MathConstant.RADIAN_90 * (up ? 1 : -1));
        shifted[1] = zoom(shifted[1].x, shifted[1].y, l2, shiftRatio);

        return shifted;
    }

    public static PointF cwCenterOfAngle(PointF from, PointF to, PointF c) {
        return cwRotate(from, c, cwAngle(from, to, c) / 2);
    }
}
