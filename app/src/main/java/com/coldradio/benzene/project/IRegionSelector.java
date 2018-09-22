package com.coldradio.benzene.project;

import android.graphics.Canvas;
import android.graphics.PointF;

public interface IRegionSelector {
    void draw(Canvas canvas);
    boolean onTouchEvent(PointF point, int touchAction);
    boolean contains(PointF point);
}
