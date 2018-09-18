package com.coldradio.benzene.view;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PointF;

import com.coldradio.benzene.lib.Geometry;
import com.coldradio.benzene.project.Configuration;

public class DrawingLibrary {
    public static void drawDoubleBond(float x1, float y1, float x2, float y2, PointF center, Canvas canvas, Paint paint) {
        if (center != null) {
            float distanceCenterToBond = Geometry.distanceFromPointToLine(center, new PointF(x1, y1), new PointF(x2, y2));
            float zoomOutRation = distanceCenterToBond < Configuration.LINE_LENGTH / 2 ? 0.55f : 0.8f;

            PointF p1 = Geometry.zoomOut(x1, y1, center, zoomOutRation);
            PointF p2 = Geometry.zoomOut(x2, y2, center, zoomOutRation);

            canvas.drawLine(p1.x, p1.y, p2.x, p2.y, paint);
        }
    }
}
