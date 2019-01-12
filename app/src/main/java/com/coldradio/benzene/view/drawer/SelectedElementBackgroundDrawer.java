package com.coldradio.benzene.view.drawer;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PointF;

import com.coldradio.benzene.compound.Atom;
import com.coldradio.benzene.compound.Compound;
import com.coldradio.benzene.compound.Edge;
import com.coldradio.benzene.project.Configuration;
import com.coldradio.benzene.project.ElementSelector;
import com.coldradio.benzene.project.Project;
import com.coldradio.benzene.util.TreeTraveler;

import java.util.List;

public class SelectedElementBackgroundDrawer implements ICompoundDrawer {
    public void drawAtomAsCircle(List<Atom> atomList, Compound compound, Canvas canvas, Paint paint) {
        for (Atom atom : atomList) {
            if (compound.getAtoms().contains(atom))
                canvas.drawCircle(atom.getPoint().x, atom.getPoint().y, Configuration.SELECTED_ATOM_BG_CIRCLE_RADIUS, paint);
        }
    }

    @Override
    public boolean draw(Compound compound, Canvas canvas, Paint paint) {
        final ElementSelector elementSelector = Project.instance().getElementSelector();

        if (! elementSelector.hasSelected())
            return false;
        if (elementSelector.selection() != ElementSelector.Selection.PARTIAL && elementSelector.getSelectedCompound() != compound)
            return false;

        Paint thickPaint = PaintSet.instance().paint(PaintSet.PaintType.THICK);

        switch (elementSelector.selection()) {
            case ATOM:
                PointF p = elementSelector.getSelectedAtom().getPoint();

                canvas.drawCircle(p.x, p.y, Configuration.SELECTED_ATOM_BG_CIRCLE_RADIUS, thickPaint);
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
                // if only one atom is inside the region, it will not be drawn by GenericDrawer.draw()
                drawAtomAsCircle(elementSelector.getSelectedAsList(), compound, canvas, thickPaint);
                break;
        }

        return true;
    }

    @Override
    public String getID() {
        return "SelectedElementBackgroundDrawer";
    }
}
