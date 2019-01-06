package com.coldradio.benzene.project;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PointF;

import com.coldradio.benzene.compound.Atom;

import java.util.List;

public interface IRegionSelector {
    void draw(Canvas canvas, Paint paint);
    boolean onTouchEvent(PointF point, int touchAction);
    List<Atom> getSelectedAtoms();
}
