package com.coldradio.benzene.geometry;

import android.graphics.PointF;
import com.coldradio.benzene.project.Configuration;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Geometry {
    private List<PointF> mPoints = new ArrayList<>();

    public void offset(float x, float y) {
        for(PointF point : mPoints) {
            point.offset(x, y);
        }
    }

    public void setCycloHexaneGeometry() {
        PointF center = new PointF(0, Configuration.LINE_LENGTH);
        PointF currentPoint = new PointF(0, 0);
        // the first point shall be always (0, 0), and will be interpreted according to the mStartingPoint

        mPoints.clear();
        for(int ii = 0; ii < 6; ++ii) {
            mPoints.add(currentPoint);
            currentPoint = rotatePointByDegree(currentPoint, center, 60);
        }
        // in case of cyclo compound, the starting point is duplicated once to the end
        mPoints.add(new PointF(0, 0));
    }

    public void setupAlkaneGeometry(int carbonLength, boolean upDirection) {
        PointF currentPoint = new PointF(0, 0);

        mPoints.clear();
        for(int ii = 0; ii < carbonLength; ++ii) {
            mPoints.add(currentPoint);
            currentPoint = new PointF(MathConstant.ROOT_3 / 2 * Configuration.LINE_LENGTH + currentPoint.x,
                                        Configuration.LINE_LENGTH / 2.0f * (upDirection ? -1 : 1) + currentPoint.y);
            upDirection = !upDirection;
        }
    }

    private PointF rotatePointByDegree(PointF current, PointF center, float degree) {
        float currentToZeroX = current.x - center.x;
        float currentToZeroY = current.y - center.y;
        float cos_theta = (float)Math.cos(Math.toRadians(degree)), sin_theta = (float)Math.sin(Math.toRadians(degree));

        return new PointF(currentToZeroX * cos_theta - currentToZeroY * sin_theta + center.x,
                currentToZeroY * cos_theta + currentToZeroX * sin_theta + center.y);
    }

    public List<PointF> getPoints() {
        return Collections.unmodifiableList(mPoints);
    }

    public float distanceFromPointToPoint(PointF p1, PointF p2) {
        return (float)Math.sqrt((p2.y - p1.y)*(p2.y - p1.y) + (p2.x - p1.x)*(p2.x - p1.x));
    }

    public float distanceFromPointToLine(PointF p0, PointF p1, PointF p2) {
        return Math.abs((p2.y - p1.y) * p0.x - (p2.x - p1.x) * p0.y + p2.x * p1.y - p2.y * p1.x) / distanceFromPointToPoint(p1, p2);
    }

    public boolean select(float x, float y) {
        PointF touchedPoint = new PointF(x, y);

        for(int ii = 0; ii < mPoints.size() - 1; ++ii) {
            float distanceToLine = distanceFromPointToLine(touchedPoint, mPoints.get(ii), mPoints.get(ii+1));

            if(distanceToLine < Configuration.SELECT_RANGE
                    && distanceFromPointToPoint(touchedPoint, mPoints.get(ii)) < (Configuration.LINE_LENGTH + Configuration.SELECT_RANGE)
                    && distanceFromPointToPoint(touchedPoint, mPoints.get(ii+1)) < (Configuration.LINE_LENGTH + Configuration.SELECT_RANGE)) {
                return true;
            }
        }

        return false;
    }
}
