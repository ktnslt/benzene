package com.coldradio.benzene.view.drawer;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PointF;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;

import com.coldradio.benzene.compound.Compound;
import com.coldradio.benzene.compound.CompoundArranger;
import com.coldradio.benzene.compound.Edge;
import com.coldradio.benzene.project.Configuration;
import com.coldradio.benzene.project.ElementSelector;
import com.coldradio.benzene.project.Project;
import com.coldradio.benzene.project.ProjectFileManager;
import com.coldradio.benzene.util.Geometry;

public class SelectedElementAccessoryDrawer implements ICompoundDrawer {
    private boolean mShowFlipBondGuideLine;
    private PointF mGuideLine_ToEdgeSecond = new PointF(), mGuideLine_ToEdgeFirst = new PointF();
    private Rect mFlipBound1 = new Rect(), mFlipBound2 = new Rect();
    private Drawable mFlipIcon;

    private void drawFlipButton(Edge edge, Canvas canvas) {
        PointF bondCenter = Geometry.centerOfLine(edge.first.getPoint(), edge.second.getPoint());
        int iconW = mFlipIcon.getMinimumWidth() / 2, iconH = mFlipIcon.getMinimumHeight() / 2;

        PointF c1 = Geometry.centerOfLine(bondCenter, mGuideLine_ToEdgeSecond);
        mFlipBound1.set((int) c1.x - iconW, (int) c1.y - iconH, iconW + (int) c1.x, iconH + (int) c1.y);
        mFlipIcon.setBounds(mFlipBound1);
        mFlipIcon.draw(canvas);

        PointF c2 = Geometry.centerOfLine(bondCenter, mGuideLine_ToEdgeFirst);
        mFlipBound2.set((int) c2.x - iconW, (int) c2.y - iconH, iconW + (int) c2.x, iconH + (int) c2.y);
        mFlipIcon.setBounds(mFlipBound2);
        mFlipIcon.draw(canvas);
    }

    private void drawRotationPivot(PointF pivot, PointF center, String text, Canvas canvas) {
        canvas.drawCircle(pivot.x, pivot.y, Configuration.ROTATION_PIVOT_SIZE, PaintSet.instance().paint(PaintSet.PaintType.GUIDE_LINE));
        canvas.drawLine(pivot.x, pivot.y, center.x, center.y, PaintSet.instance().paint(PaintSet.PaintType.GUIDE_LINE));
        if (text != null && ! text.isEmpty()) {
            DrawingLib.drawText(text, pivot, false, 0, canvas, PaintSet.instance().paint(PaintSet.PaintType.GENERAL));
        }
    }

    public SelectedElementAccessoryDrawer(Drawable flipIcon) {
        mFlipIcon = flipIcon;
    }

    @Override
    public boolean draw(Compound compound, Canvas canvas, Paint paint) {
        ElementSelector elementSelector = Project.instance().getElementSelector();

        if (elementSelector.selection() == ElementSelector.Selection.COMPOUND && elementSelector.getSelectedCompound() == compound) {
            // draw ration pivot
            drawRotationPivot(elementSelector.getRotationPivotPoint(true), elementSelector.getSelectedCompound().centerOfRectangle(), null, canvas);
            drawRotationPivot(elementSelector.getRotationPivotPoint(false), elementSelector.getSelectedCompound().centerOfRectangle(), "10", canvas);
        } else if (elementSelector.selection() == ElementSelector.Selection.EDGE && elementSelector.getSelectedCompound() == compound && mShowFlipBondGuideLine) {
            // draw guideline
            Edge edge = elementSelector.getSelectedEdge();
            Geometry.pointInLine(edge.first.getPoint(), edge.second.getPoint(), 3, mGuideLine_ToEdgeSecond);
            Geometry.pointInLine(edge.second.getPoint(), edge.first.getPoint(), 3, mGuideLine_ToEdgeFirst);

            canvas.drawLine(mGuideLine_ToEdgeSecond.x, mGuideLine_ToEdgeSecond.y, mGuideLine_ToEdgeFirst.x, mGuideLine_ToEdgeFirst.y, PaintSet.instance().paint(PaintSet.PaintType.GUIDE_LINE));
            // draw flip icon
            drawFlipButton(edge, canvas);
        } else if (elementSelector.selection() != ElementSelector.Selection.EDGE) {
            mShowFlipBondGuideLine = false;
        }

        return true;
    }

    public void showFlipBondGuideLine(boolean show) {
        mShowFlipBondGuideLine = show;
    }

    public boolean flipBond(PointF clickedPoint) {
        if (mShowFlipBondGuideLine) {
            Edge edge = Project.instance().getElementSelector().getSelectedEdge();

            if (mFlipBound1.contains((int) clickedPoint.x, (int) clickedPoint.y)) {
                // Bound1 towards edge.second
                ProjectFileManager.instance().pushForChange();
                CompoundArranger.flipBond(edge.second, edge.first, Project.instance().findCompound(edge.second));
            } else if (mFlipBound2.contains((int) clickedPoint.x, (int) clickedPoint.y)) {
                // Bond2 towards edge.first
                ProjectFileManager.instance().pushForChange();
                CompoundArranger.flipBond(edge.first, edge.second, Project.instance().findCompound(edge.second));
            } else {
                return false;
            }
            return true;
        }
        return false;
    }

    @Override
    public String getID() {
        return "SelectedElementAccessoryDrawer";
    }
}
