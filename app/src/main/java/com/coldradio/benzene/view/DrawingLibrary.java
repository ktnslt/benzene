package com.coldradio.benzene.view;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PointF;

import com.coldradio.benzene.geometry.Geometry;

public class DrawingLibrary {
    public static void drawDoubleBond(float x1, float y1, float x2, float y2, PointF benzeneCenter, Canvas canvas, Paint paint) {
        if (benzeneCenter != null) {
            PointF p1 = Geometry.zoomOut(x1, y1, benzeneCenter, 0.8f);
            PointF p2 = Geometry.zoomOut(x2, y2, benzeneCenter, 0.8f);

            canvas.drawLine(p1.x, p1.y, p2.x, p2.y, paint);
        }
    }
}
