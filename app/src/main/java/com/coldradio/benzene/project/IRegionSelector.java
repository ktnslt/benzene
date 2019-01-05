package com.coldradio.benzene.project;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PointF;

public interface IRegionSelector {
    void draw(Canvas canvas, Paint paint);
    boolean onTouchEvent(PointF point, int touchAction);
    boolean contains(PointF point);
    boolean move(float dx, float dy);
}
