package com.coldradio.benzene.view;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PointF;

import com.coldradio.benzene.compound.Atom;
import com.coldradio.benzene.compound.Compound;
import com.coldradio.benzene.project.IRegionSelector;
import com.coldradio.benzene.project.Project;

public class SelectedRegionDrawer implements ICompoundDrawer {
    private Paint mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);

    public SelectedRegionDrawer() {
        mPaint.setStyle(Paint.Style.FILL);
        mPaint.setColor(Color.argb(255, 0, 255, 0));
    }
    @Override
    public boolean draw(Compound compound, Canvas canvas, Paint paint) {
        IRegionSelector regionSelector = Project.instance().getRegionSelector();

        if (regionSelector != null) {
            for (Atom atom : compound.getAtoms()) {
                PointF point = atom.getPoint();

                if (regionSelector.contains(point)) {
                    canvas.drawCircle(point.x, point.y, 20, mPaint);
                }
            }
        }
        return false;   // always returns false to make the other drawer draws on it
    }

    @Override
    public String getID() {
        return "SelectedRegionDrawer";
    }
}
