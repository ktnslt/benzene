package com.coldradio.benzene.view.drawer;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PointF;
import android.graphics.Rect;
import android.graphics.RectF;

import com.coldradio.benzene.util.Geometry;

public class DrawingLib {
    public static Rect atomEnclosingRect(PointF atomXY) {
        Rect charBounds = new Rect(0, 0, PaintSet.instance().fontWidth(PaintSet.PaintType.GENERAL), PaintSet.instance().fontHeight(PaintSet.PaintType.GENERAL));

        charBounds.offsetTo((int) atomXY.x - charBounds.width() / 2, (int) atomXY.y - charBounds.height() / 2);

        return charBounds;
    }

    private static RectF msRectF = new RectF();
    public static void drawCwArc(PointF start, PointF end, PointF center, Canvas canvas, Paint paint) {
        // https://stackoverflow.com/questions/4196749/draw-arc-with-2-points-and-center-of-the-circle
        float r = Geometry.distanceFromPointToPoint(start, center);
        float startAngle = (float) (180 / Math.PI * Math.atan2(start.y - center.y, start.x - center.x));
        float endAngle_minus_startAngle = (float) (180 / Math.PI * Math.atan2(end.y - center.y, end.x - center.x)) - startAngle;

        if (endAngle_minus_startAngle < 0) {
            endAngle_minus_startAngle += 360;
        }
        msRectF.set(center.x - r, center.y - r, center.x + r, center.y + r);
        canvas.drawArc(msRectF, startAngle, endAngle_minus_startAngle, false, paint);
    }
}
