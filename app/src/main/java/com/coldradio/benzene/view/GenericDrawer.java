package com.coldradio.benzene.view;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PointF;
import android.util.Log;

import com.coldradio.benzene.compound.Atom;
import com.coldradio.benzene.compound.Bond;
import com.coldradio.benzene.compound.Compound;
import com.coldradio.benzene.geometry.Geometry;

import java.util.HashMap;
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

    private void drawRecursive(Atom atom, HashMap<Atom, Atom> visitedEdge, Canvas canvas, Paint paint) {
        for(Bond bond : atom.getBonds()) {
            if(visitedEdge.get(atom) != bond.getBoundAtom() && visitedEdge.get(bond.getBoundAtom()) != atom) {
                // the edge is not visited
                drawBond(atom, bond, canvas, paint);
                visitedEdge.put(atom, bond.getBoundAtom());
                drawRecursive(bond.getBoundAtom(), visitedEdge, canvas, paint);
            }
        }
    }

    @Override
    public boolean draw(Compound compound, Canvas canvas, Paint paint) {
        List<Atom> atoms = compound.getAtoms();
        HashMap<Atom, Atom> visitedEdge = new HashMap<>();

        drawRecursive(atoms.get(0), visitedEdge, canvas, paint);

//        for (int ii = 0; ii < atoms.size(); ++ii) {
//            if (ii < atoms.size() - 1 || (ii == atoms.size() - 1 && compound.isCyclo())) {
//                Atom a1 = atoms.get(ii), a2 = atoms.get((ii + 1) % atoms.size());
//                PointF p1 = a1.getPoint(), p2 = a2.getPoint();
//
//                canvas.drawLine(p1.x, p1.y, p2.x, p2.y, paint);
//                switch (a1.getBondType(a2)) {
//                    case DOUBLE:
//                        DrawingLibrary.drawDoubleBond(p1.x, p1.y, p2.x, p2.y, centerForDoubleBond(a1, a2), canvas, paint);
//                        break;
//                    case TRIPLE:
//                        PointF center = centerForDoubleBond(a1, a2);
//
//                        DrawingLibrary.drawDoubleBond(p1.x, p1.y, p2.x, p2.y, centerForDoubleBond(a1, a2), canvas, paint);
//                        DrawingLibrary.drawDoubleBond(p1.x, p1.y, p2.x, p2.y, Geometry.symmetricPointToLine(center, a1.getPoint(), a2.getPoint()), canvas, paint);
//                        break;
//                }
//            }
//        }
        return true;
    }

    private PointF centerForDoubleBond(Atom a1, Atom a2) {
        Atom before_a1 = a1.getBoundAtomExcept(a2);
        Atom after_a2 = a2.getBoundAtomExcept(a1);
        PointF a1p = a1.getPoint(), a2p = a2.getPoint();

        if (before_a1 != null && after_a2 != null) {
            PointF before_a1p = before_a1.getPoint(), after_a2p = after_a2.getPoint();

            if (before_a1 == after_a2) {    // propane case, returns the center of the triangle
                return new PointF((a1p.x + a2p.x + before_a1p.x) / 3, (a1p.y + a2p.y + before_a1p.y) / 3);
            } else {
                if (Geometry.sameSideOfLine(before_a1p, after_a2p, a1p, a2p)) {
                    return Geometry.centerPoint(before_a1p, after_a2p);
                } else {
                    return Geometry.centerPoint(before_a1p, Geometry.symmetricPointToLine(after_a2p, a1p, a2p));
                }
            }
        } else if (before_a1 == null && after_a2 == null) {
            return null;
        } else if (before_a1 == null) {
            PointF after_a2p = after_a2.getPoint();

            return Geometry.centerPoint(Geometry.rotatePointByDegree(a2p, a1p, 120), after_a2p);
        } else { // here after_a2 is null
            PointF before_a1p = before_a1.getPoint();

            return Geometry.centerPoint(before_a1p, Geometry.rotatePointByDegree(a1p, a2p, 120));
        }
    }

    @Override
    public String getID() {
        return "GenericDrawer";
    }
}
