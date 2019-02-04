package com.coldradio.benzene.view.drawer;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PointF;

import com.coldradio.benzene.compound.Atom;
import com.coldradio.benzene.compound.Bond;
import com.coldradio.benzene.compound.Compound;
import com.coldradio.benzene.compound.CompoundInspector;
import com.coldradio.benzene.util.Geometry;
import com.coldradio.benzene.util.TreeTraveler;
import com.coldradio.benzene.project.Configuration;

public class GenericDrawer {
    private static void drawDoubleBond(PointF p1, PointF p2, PointF center, Canvas canvas, Paint paint) {
        if (center != null) {
            float distanceCenterToBond = Geometry.distanceFromPointToLine(center, p1, p2);
            float zoomOutRatio = distanceCenterToBond < Configuration.BOND_LENGTH / 2 ? 0.55f : 0.8f;

            PointF zoomed_p1 = Geometry.zoom(p1.x, p1.y, center, zoomOutRatio);
            PointF zoomed_p2 = Geometry.zoom(p2.x, p2.y, center, zoomOutRatio);

            canvas.drawLine(zoomed_p1.x, zoomed_p1.y, zoomed_p2.x, zoomed_p2.y, paint);
        }
    }

    private static Path wedgeTrianglePath(PointF p1, PointF p2, Path path) {
        PointF tri1 = Geometry.orthogonalPointToLine(p1, p2, Configuration.WEDGE_WIDTH_TO_BOND_LENGTH, true);
        PointF tri2 = Geometry.orthogonalPointToLine(p1, p2, Configuration.WEDGE_WIDTH_TO_BOND_LENGTH, false);

        path.reset();
        path.moveTo(p1.x, p1.y);
        path.lineTo(tri1.x, tri1.y);
        path.lineTo(tri2.x, tri2.y);
        path.close();

        return path;
    }

    private static Path path = new Path();
    private static void drawSingleBond(PointF p1, PointF p2, Bond.BondAnnotation bondAnnotation, Canvas canvas, Paint paint) {
        // (p1, p2) sequence has meaning; the wedge is only from p1 -> p2. one directional.
        if (bondAnnotation == Bond.BondAnnotation.WEDGE_UP) {
            canvas.drawPath(wedgeTrianglePath(p1, p2, path), paint);
        } else if (bondAnnotation == Bond.BondAnnotation.WEDGE_DOWN) {
            PointF tri1 = Geometry.orthogonalPointToLine(p1, p2, Configuration.WEDGE_WIDTH_TO_BOND_LENGTH, true);
            PointF tri2 = Geometry.orthogonalPointToLine(p1, p2, Configuration.WEDGE_WIDTH_TO_BOND_LENGTH, false);

            for (int ii = 1; ii <= 10; ++ii) {
                PointF l1 = Geometry.pointInLine(p1, tri1, 0.1f * ii);
                PointF l2 = Geometry.pointInLine(p1, tri2, 0.1f * ii);

                canvas.drawLine(l1.x, l1.y, l2.x, l2.y, paint);
            }
        } else if (bondAnnotation == Bond.BondAnnotation.WAVY) {
            Paint.Style originalStyle = paint.getStyle();

            paint.setStyle(Paint.Style.STROKE);
            for (int ii = 1; ii <= 5; ++ii) {
                PointF l1 = Geometry.pointInLine(p1, p2, 0.2f * (ii - 1));
                PointF l2 = Geometry.pointInLine(p1, p2, 0.2f * ii);

                if (ii % 2 == 0)
                    DrawingLib.drawCwArc(l1, l2, Geometry.centerOfLine(l1, l2), canvas, paint);
                else
                    DrawingLib.drawCwArc(l2, l1, Geometry.centerOfLine(l1, l2), canvas, paint);
            }
            paint.setStyle(originalStyle);
        } else {
            canvas.drawLine(p1.x, p1.y, p2.x, p2.y, paint);
        }
    }

    private static Atom boundAtomWithRing(Atom atom, Atom edgeAtom) {
        // Edge (atom, edgeAtom)
        for (Bond bond : atom.getBonds()) {
            Atom that_atom = bond.getBoundAtom();

            if (that_atom != edgeAtom && CompoundInspector.isSkeletonAtom(that_atom) && CompoundInspector.pathExistsExceptDirect(atom, that_atom))
                return that_atom;
        }
        return null;
    }

    public static void drawBondSingleOrDoubleMiddle(PointF p1, PointF p2, Bond.BondType bondType, Canvas canvas, Paint paint) {
        switch (bondType) {
            case SINGLE:
                canvas.drawLine(p1.x, p1.y, p2.x, p2.y, paint);
                break;
            case DOUBLE_MIDDLE:
                PointF[] shifted = Geometry.lineOrthogonalShift(p1, p2, 0.05f, true);
                canvas.drawLine(shifted[0].x, shifted[0].y, shifted[1].x, shifted[1].y, paint);

                shifted = Geometry.lineOrthogonalShift(p1, p2, 0.05f, false);
                canvas.drawLine(shifted[0].x, shifted[0].y, shifted[1].x, shifted[1].y, paint);
                break;
        }
    }

    public static boolean draw(Compound compound, Canvas canvas, Paint paint) {
        // draw edges
        return draw(compound, null, canvas, paint);
    }

    public static boolean draw(Compound compound, TreeTraveler.IEdgeVisitor condition, Canvas canvas, Paint paint) {
        // draw edges
        TreeTraveler.returnFirstEdge(new TreeTraveler.IEdgeVisitor() {
            @Override
            public boolean visit(Atom a1, Atom a2, Object... args) {
                TreeTraveler.IEdgeVisitor condition = (TreeTraveler.IEdgeVisitor) args[0];

                if (! a1.isVisible() || ! a2.isVisible())
                    return false;
                if (condition != null && ! condition.visit(a1, a2))
                    return false;

                Canvas canvas = (Canvas) args[1];
                Paint paint = (Paint) args[2];
                PointF p1 = a1.getPoint(), p2 = a2.getPoint();

                switch (a1.getBondType(a2)) {
                    case SINGLE:
                        Bond.BondAnnotation bondAnnotation = a1.getBondAnnotation(a2);
                        if (bondAnnotation != Bond.BondAnnotation.NONE) {
                            // (p1, p2) order shall be aligned with bondAnnotation direction (p1 -> p2)
                            drawSingleBond(p1, p2, bondAnnotation, canvas, paint);
                        } else {
                            // here annotation direction is p2 -> p1 or no annotation
                            drawSingleBond(p2, p1, a2.getBondAnnotation(a1), canvas, paint);
                        }
                        break;
                    case DOUBLE:
                    case DOUBLE_OTHER_SIDE:
                        canvas.drawLine(p1.x, p1.y, p2.x, p2.y, paint);
                        drawDoubleBond(p1, p2, DrawingLib.centerForDoubleBond(a1, a2, a1.getBondType(a2) == Bond.BondType.DOUBLE_OTHER_SIDE), canvas, paint);
                        break;
                    case DOUBLE_MIDDLE:
                        PointF[] shifted = Geometry.lineOrthogonalShift(p1, p2, 0.05f, true);
                        canvas.drawLine(shifted[0].x, shifted[0].y, shifted[1].x, shifted[1].y, paint);

                        shifted = Geometry.lineOrthogonalShift(p1, p2, 0.05f, false);
                        canvas.drawLine(shifted[0].x, shifted[0].y, shifted[1].x, shifted[1].y, paint);
                        break;
                    case TRIPLE:
                        PointF center = DrawingLib.centerForDoubleBond(a1, a2, a1.getBondType(a2) == Bond.BondType.DOUBLE_OTHER_SIDE);

                        canvas.drawLine(p1.x, p1.y, p2.x, p2.y, paint);
                        drawDoubleBond(p1, p2, center, canvas, paint);
                        drawDoubleBond(p1, p2, Geometry.symmetricToLine(center, a1.getPoint(), a2.getPoint()), canvas, paint);
                        break;
                }
                return false;
            }
        }, compound, condition, canvas, paint);

        return true;
    }
}