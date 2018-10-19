package com.coldradio.benzene.view.drawer;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.Paint;
import android.graphics.PointF;

import com.coldradio.benzene.compound.Compound;
import com.coldradio.benzene.lib.Edge;
import com.coldradio.benzene.project.Configuration;
import com.coldradio.benzene.project.ElementSelector;
import com.coldradio.benzene.project.Project;

import java.util.ArrayList;
import java.util.List;

public class DrawerManager {
    private Paint mPaint;
    private Paint mThickPaint;
    private Paint mDashedLinePaint;
    private List<ICompoundDrawer> mCompoundDrawer = new ArrayList<>();

    private void drawSelectedCompoundAccessory(ElementSelector elementSelector, Canvas canvas) {
        PointF pivot = elementSelector.getRotationPivotPoint();
        PointF center = elementSelector.getSelectedCompound().centerOfRectangle();

        canvas.drawCircle(pivot.x, pivot.y, Configuration.ROTATION_PIVOT_SIZE, mDashedLinePaint);
        canvas.drawLine(pivot.x, pivot.y, center.x, center.y, mDashedLinePaint);
    }

    public DrawerManager() {
        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPaint.setStrokeWidth(Configuration.LINE_THICKNESS);
        mPaint.setTextSize(Configuration.FONT_SIZE);
        mPaint.setStyle(Paint.Style.FILL);

        mThickPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mThickPaint.setStrokeWidth(Configuration.LINE_THICKNESS * 7);
        mThickPaint.setStrokeCap(Paint.Cap.ROUND);
        mThickPaint.setStyle(Paint.Style.STROKE);
        mThickPaint.setColor(Color.rgb(140, 180, 250));

        mDashedLinePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mDashedLinePaint.setStrokeWidth(4);
        mDashedLinePaint.setColor(Color.rgb(60, 60, 60));
        mDashedLinePaint.setStyle(Paint.Style.STROKE);
        mDashedLinePaint.setPathEffect(new DashPathEffect(new float[]{5, 5},0));
    }

    public void addCompoundDrawer(ICompoundDrawer newCompoundDrawer) {
        boolean unregistered = true;

        for (ICompoundDrawer componentDrawer : mCompoundDrawer) {
            if (componentDrawer.getID().equals(newCompoundDrawer.getID())) {
                unregistered = false;
                break;
            }
        }
        if (unregistered) {
            mCompoundDrawer.add(newCompoundDrawer);
        }
    }

    public void draw(Canvas canvas) {
        ElementSelector elementSelector = Project.instance().getElementSelector();

        switch (elementSelector.selection()) {
            case ATOM:
                PointF p = elementSelector.getSelectedAtom().getPoint();

                canvas.drawCircle(p.x, p.y, 10, mThickPaint);
                break;
            case EDGE:
                Edge edge = elementSelector.getSelectedEdge();
                PointF p1 = edge.first.getPoint();
                PointF p2 = edge.second.getPoint();

                canvas.drawLine(p1.x, p1.y, p2.x, p2.y, mThickPaint);
                break;
            case COMPOUND:
                GenericDrawer.draw(elementSelector.getSelectedCompound(), canvas, mThickPaint);
                break;
        }

        for (Compound compound : Project.instance().getCompounds()) {
            boolean drawn = false;
            for (ICompoundDrawer componentDrawer : mCompoundDrawer) {
                if (drawn = componentDrawer.draw(compound, canvas, mPaint)) {
                    break;
                }
            }
            if (! drawn) {
                GenericDrawer.draw(compound, canvas, mPaint);
            }
        }

        if (elementSelector.hasSelectedCompound()) {
            // not drawn at the same time with the compound since the accessory shall be in front of everything
            drawSelectedCompoundAccessory(elementSelector, canvas);
        }

//        if (mRegionSelector != null) {
//            mRegionSelector.draw(canvas);
//        }
    }
}
