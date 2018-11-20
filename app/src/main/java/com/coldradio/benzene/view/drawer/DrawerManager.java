package com.coldradio.benzene.view.drawer;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.Paint;
import android.graphics.PointF;
import android.graphics.Typeface;

import com.coldradio.benzene.compound.Compound;
import com.coldradio.benzene.compound.Edge;
import com.coldradio.benzene.project.Configuration;
import com.coldradio.benzene.project.ElementSelector;
import com.coldradio.benzene.project.Project;

import java.util.ArrayList;
import java.util.List;

public class DrawerManager {
    private List<ICompoundDrawer> mCompoundDrawer = new ArrayList<>();

    private void drawSelectedCompoundAccessory(ElementSelector elementSelector, Canvas canvas) {
        PointF pivot = elementSelector.getRotationPivotPoint();
        PointF center = elementSelector.getSelectedCompound().centerOfRectangle();
        Paint dashedLinePaint = PaintSet.instance().dashedLine();

        canvas.drawCircle(pivot.x, pivot.y, Configuration.ROTATION_PIVOT_SIZE, dashedLinePaint);
        canvas.drawLine(pivot.x, pivot.y, center.x, center.y, dashedLinePaint);
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
        Paint generalPaint = PaintSet.instance().general();
        Paint thickPaint = PaintSet.instance().thick();

        switch (elementSelector.selection()) {
            case ATOM:
                PointF p = elementSelector.getSelectedAtom().getPoint();

                canvas.drawCircle(p.x, p.y, 10, thickPaint);
                break;
            case EDGE:
                Edge edge = elementSelector.getSelectedEdge();
                PointF p1 = edge.first.getPoint();
                PointF p2 = edge.second.getPoint();

                canvas.drawLine(p1.x, p1.y, p2.x, p2.y, thickPaint);
                break;
            case COMPOUND:
                GenericDrawer.draw(elementSelector.getSelectedCompound(), canvas, thickPaint);
                break;
        }

        for (Compound compound : Project.instance().getCompounds()) {
            boolean drawn = false;
            for (ICompoundDrawer componentDrawer : mCompoundDrawer) {
                if (drawn = componentDrawer.draw(compound, canvas, generalPaint)) {
                    break;
                }
            }
            if (! drawn) {
                GenericDrawer.draw(compound, canvas, generalPaint);
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
