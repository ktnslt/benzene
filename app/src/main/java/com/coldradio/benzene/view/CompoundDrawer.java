package com.coldradio.benzene.view;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PointF;
import com.coldradio.benzene.compound.Compound;
import com.coldradio.benzene.project.Configuration;

import java.util.List;

public class CompoundDrawer {
    private Paint mPaint;
    private static CompoundDrawer instance = new CompoundDrawer();

    private CompoundDrawer() {
        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPaint.setStrokeWidth(Configuration.LINE_THICKNESS);
    }

    public static CompoundDrawer instance() {
        return instance;
    }
    public void draw(Compound compound, Canvas canvas) {
        if(compound.isSelected()) {
            mPaint.setColor(Color.GREEN);
        }
        else {
            mPaint.setColor(Color.BLACK);
        }

        List<PointF> points = compound.getGeometry().getPoints();
        PointF offset = compound.getGeometry().getOffset();

        for(int ii = 0; ii < points.size() - 1; ++ii) {
            canvas.drawLine(points.get(ii).x + offset.x, points.get(ii).y + offset.y, points.get(ii + 1).x + offset.x, points.get(ii+1).y + offset.y, mPaint);
        }
    }
}
