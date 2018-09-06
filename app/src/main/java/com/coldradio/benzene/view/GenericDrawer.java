package com.coldradio.benzene.view;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PointF;

import com.coldradio.benzene.compound.Atom;
import com.coldradio.benzene.compound.Compound;
import com.coldradio.benzene.geometry.Geometry;

import java.util.List;

public class GenericDrawer implements CompoundDrawer.IComponentDrawer {
    @Override
    public boolean draw(Compound compound, Canvas canvas, Paint paint) {
        List<Atom> atoms = compound.getAtoms();

        for (int ii = 0; ii < atoms.size(); ++ii) {
            if (ii < atoms.size() - 1 || (ii == atoms.size() - 1 && compound.isCyclo())) {
                Atom a1 = atoms.get(ii), a2 = atoms.get((ii + 1) % atoms.size());
                PointF p1 = a1.getPoint(), p2 = a2.getPoint();

                canvas.drawLine(p1.x, p1.y, p2.x, p2.y, paint);
                switch (a1.getBondType(a2)) {
                    case DOUBLE:
                        DrawingLibrary.drawDoubleBond(p1.x, p1.y, p2.x, p2.y, centerForDoubleBond(a1, a2), canvas, paint);
                        break;
                    case TRIPLE:
                        canvas.drawLine(p1.x, p1.y + 10, p2.x, p2.y + 10, paint);
                        canvas.drawLine(p1.x, p1.y - 10, p2.x, p2.y - 10, paint);
                        break;
                }
            }
        }
        return true;
    }

    private PointF centerForDoubleBond(Atom a1, Atom a2) {
        Atom before_a1 = a1.getBoundAtomExcept(a2);
        Atom after_a2 = a2.getBoundAtomExcept(a1);
        PointF a1p = a1.getPoint(), a2p = a2.getPoint();

        if (before_a1 != null && after_a2 != null) {
            PointF before_a1p = before_a1.getPoint(), after_a2p = after_a2.getPoint();

            if (Geometry.sameSideOfLine(before_a1p, after_a2p, a1p, a2p)) {
                return Geometry.centerPoint(before_a1p, after_a2p);
            } else {
                return Geometry.centerPoint(before_a1p, Geometry.symmetricPointToLine(after_a2p, a1p, a2p));
            }
        } else if (before_a1 == null && after_a2 == null) {
            return null;
        } else if (before_a1 == null) {
            PointF after_a2p = after_a2.getPoint();

            return Geometry.centerPoint(Geometry.rotatePointByDegree(a2p, a1p, 120), after_a2p);
        } else if (after_a2 == null) {
            PointF before_a1p = before_a1.getPoint();

            return Geometry.centerPoint(before_a1p, Geometry.rotatePointByDegree(a1p, a2p, 120));
        }
        return null;
    }

    @Override
    public String getID() {
        return "GenericDrawer";
    }
}
