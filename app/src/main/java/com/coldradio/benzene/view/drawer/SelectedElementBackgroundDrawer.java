package com.coldradio.benzene.view.drawer;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PointF;

import com.coldradio.benzene.compound.Atom;
import com.coldradio.benzene.compound.Compound;
import com.coldradio.benzene.compound.Edge;
import com.coldradio.benzene.project.ElementSelector;
import com.coldradio.benzene.project.Project;
import com.coldradio.benzene.util.TreeTraveler;

public class SelectedElementBackgroundDrawer implements ICompoundDrawer {
    @Override
    public boolean draw(Compound compound, Canvas canvas, Paint paint) {
        final ElementSelector elementSelector = Project.instance().getElementSelector();

        if (elementSelector.selection() == ElementSelector.Selection.NONE)
            return false;
        if (elementSelector.selection() != ElementSelector.Selection.PARTIAL && elementSelector.getSelectedCompound() != compound)
            return false;

        Paint thickPaint = PaintSet.instance().paint(PaintSet.PaintType.THICK);

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
            case PARTIAL:
                TreeTraveler.IEdgeVisitor condition = new TreeTraveler.IEdgeVisitor() {
                    @Override
                    public boolean visit(Atom a1, Atom a2, Object... args) {
                        return elementSelector.isSelected(a1) && elementSelector.isSelected(a2);
                    }
                };
                GenericDrawer.draw(compound, condition, canvas, thickPaint);
                break;
        }

        return true;
    }

    @Override
    public String getID() {
        return "SelectedElementBackgroundDrawer";
    }
}
