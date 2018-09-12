package com.coldradio.benzene.view;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PointF;
import android.util.Pair;

import com.coldradio.benzene.compound.Atom;
import com.coldradio.benzene.compound.Bond;
import com.coldradio.benzene.compound.Compound;
import com.coldradio.benzene.geometry.Geometry;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

public class GenericDrawer implements CompoundDrawer.ICompoundDrawer {
    private void drawBond(Atom a1, Bond bond, Canvas canvas, Paint paint) {
        Atom a2 = bond.getBoundAtom();
        PointF p1 = a1.getPoint(), p2 = a2.getPoint();

        canvas.drawLine(p1.x, p1.y, p2.x, p2.y, paint);
        switch (bond.getBondType()) {
            case DOUBLE:
                DrawingLibrary.drawDoubleBond(p1.x, p1.y, p2.x, p2.y, centerForDoubleBond(a1, a2), canvas, paint);
                break;
            case TRIPLE:
                PointF center = centerForDoubleBond(a1, a2);

                DrawingLibrary.drawDoubleBond(p1.x, p1.y, p2.x, p2.y, centerForDoubleBond(a1, a2), canvas, paint);
                DrawingLibrary.drawDoubleBond(p1.x, p1.y, p2.x, p2.y, Geometry.symmetricPointToLine(center, a1.getPoint(), a2.getPoint()), canvas, paint);
                break;
        }
    }

    private void drawRecursive(Atom atom, HashSet<String> visitedEdge, Canvas canvas, Paint paint) {
        for (Bond bond : atom.getBonds()) {
            /*  TODO: since points are used as a key, if the two atoms have exact the same point, this DO NOT work properly, though it is quite rare.
                I have tried to use hashCode of the atom as a key, but it doesn't work.
            */
            if (!visitedEdge.contains(atom.getPoint().toString() + bond.getBoundAtom().getPoint())
                    && !visitedEdge.contains(bond.getBoundAtom().getPoint().toString() + atom.getPoint())) {
                // the edge is not visited
                drawBond(atom, bond, canvas, paint);
                visitedEdge.add(atom.getPoint().toString() + bond.getBoundAtom().getPoint());
                drawRecursive(bond.getBoundAtom(), visitedEdge, canvas, paint);
            }
        }
    }

    @Override
    public boolean draw(Compound compound, Canvas canvas, Paint paint) {
        List<Atom> atoms = compound.getAtoms();
        HashSet<String> visitedEdge = new HashSet<>();

        drawRecursive(atoms.get(0), visitedEdge, canvas, paint);

        return true;
    }

    private PointF centerForDoubleBond(Atom a1, Atom a2) {
        PointF a1p = a1.getPoint(), a2p = a2.getPoint();
        Atom before_a1 = a1.getBoundAtomExcept(a2);
        Atom after_a2 = a2.getBoundAtomExcept(a1);

        if (before_a1 != null && after_a2 != null && before_a1 == after_a2) {
            // propane case, returns the center of the triangle
            PointF before_a1p = before_a1.getPoint();

            return new PointF((a1p.x + a2p.x + before_a1p.x) / 3, (a1p.y + a2p.y + before_a1p.y) / 3);
        } else {
            PointF[] centers = Geometry.regularTrianglePoint(a1p, a2p);
            int carbonBound_a1 = a1.carbonBoundNumber(), carbonBound_a2 = a2.carbonBoundNumber();

            // carbonBound == 1 is the primary carbon and the chain ends. == 2 means it has one additional branch, and before_a1 != null
            if (carbonBound_a1 == 2) {
                return Geometry.sameSideOfLine(before_a1.getPoint(), centers[0], a1p, a2p) ? centers[0] : centers[1];
            } else if (carbonBound_a2 == 2) {
                return Geometry.sameSideOfLine(after_a2.getPoint(), centers[0], a1p, a2p) ? centers[0] : centers[1];
            } else {
                return centers[0];
            }
        }
    }

    @Override
    public String getID() {
        return "GenericDrawer";
    }
}
