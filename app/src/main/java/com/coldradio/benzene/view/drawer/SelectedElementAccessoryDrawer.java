package com.coldradio.benzene.view.drawer;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PointF;

import com.coldradio.benzene.compound.Compound;
import com.coldradio.benzene.project.Configuration;
import com.coldradio.benzene.project.ElementSelector;
import com.coldradio.benzene.project.Project;

public class SelectedElementAccessoryDrawer implements ICompoundDrawer{
    @Override
    public boolean draw(Compound compound, Canvas canvas, Paint paint) {
        ElementSelector elementSelector = Project.instance().getElementSelector();

        if (elementSelector.selection() == ElementSelector.Selection.COMPOUND && elementSelector.getSelectedCompound() == compound) {
            PointF pivot = elementSelector.getRotationPivotPoint();
            PointF center = elementSelector.getSelectedCompound().centerOfRectangle();
            Paint dashedLinePaint = PaintSet.instance().paint(PaintSet.PaintType.DASHED_LINE);

            canvas.drawCircle(pivot.x, pivot.y, Configuration.ROTATION_PIVOT_SIZE, dashedLinePaint);
            canvas.drawLine(pivot.x, pivot.y, center.x, center.y, dashedLinePaint);

            return true;
        } else
            return false;
    }

    @Override
    public String getID() {
        return "SelectedElementAccessoryDrawer";
    }
}
