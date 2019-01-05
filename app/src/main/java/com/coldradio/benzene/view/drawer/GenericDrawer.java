package com.coldradio.benzene.view.drawer;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PointF;

import com.coldradio.benzene.compound.Atom;
import com.coldradio.benzene.compound.AtomicNumber;
import com.coldradio.benzene.compound.Bond;
import com.coldradio.benzene.compound.Compound;
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

    private static int centerBias(Atom atom, PointF center, PointF l1, PointF l2) {
        int bias = 0;

        for (Bond bond : atom.getBonds()) {
            Atom boundAtom = bond.getBoundAtom();
            PointF boundAtomPoint = boundAtom.getPoint();

            // here address compare for boundAtomPoint is intentionally used instead of equals()
            if (boundAtom.getAtomicNumber() != AtomicNumber.H && boundAtomPoint != l1 && boundAtomPoint != l2) {
                bias += (Geometry.sameSideOfLine(boundAtomPoint, center, l1, l2) ? 1 : -1);
            }
        }

        return bias;
    }

    private static PointF centerForDoubleBond(Atom a1, Atom a2) {
        PointF a1p = a1.getPoint(), a2p = a2.getPoint();
        Atom before_a1 = a1.getSkeletonAtomExcept(a2);
        Atom after_a2 = a2.getSkeletonAtomExcept(a1);

        if (before_a1 != null && after_a2 != null && before_a1 == after_a2) {
            // propane case, returns the center of the triangle
            PointF before_a1p = before_a1.getPoint();

            return new PointF((a1p.x + a2p.x + before_a1p.x) / 3, (a1p.y + a2p.y + before_a1p.y) / 3);
        } else {
            PointF[] centers = Geometry.regularTrianglePoint(a1p, a2p);
            int centerBiasTo0 = 0;
            int centerIndex;

            centerBiasTo0 += centerBias(a1, centers[0], a1p, a2p);
            centerBiasTo0 += centerBias(a2, centers[0], a1p, a2p);
            centerIndex = centerBiasTo0 > 0 ? 0 : 1;

            if (a1.getBondType(a2) == Bond.BondType.DOUBLE_OTHER_SIDE) {
                centerIndex = (centerIndex + 1) % 2;
            }
            return centers[centerIndex];
        }
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
                            // p2 needs to be cut towards to p1 the Element Name (e.g., O) blocks the wedge
                            PointF adjusted_p2 = a2.isNameShown() ? Geometry.pointInLine(p1, p2, 0.8f) : p2;

                            drawSingleBond(p1, adjusted_p2, bondAnnotation, canvas, paint);
                        } else {
                            PointF adjusted_p1 = a1.isNameShown() ? Geometry.pointInLine(p2, p1, 0.8f) : p1;

                            drawSingleBond(p2, adjusted_p1, a2.getBondAnnotation(a1), canvas, paint);
                        }
                        break;
                    case DOUBLE:
                    case DOUBLE_OTHER_SIDE:
                        canvas.drawLine(p1.x, p1.y, p2.x, p2.y, paint);
                        drawDoubleBond(p1, p2, centerForDoubleBond(a1, a2), canvas, paint);
                        break;
                    case DOUBLE_MIDDLE:
                        PointF[] shifted = Geometry.lineOrthogonalShift(p1, p2, 0.05f, true);
                        canvas.drawLine(shifted[0].x, shifted[0].y, shifted[1].x, shifted[1].y, paint);

                        shifted = Geometry.lineOrthogonalShift(p1, p2, 0.05f, false);
                        canvas.drawLine(shifted[0].x, shifted[0].y, shifted[1].x, shifted[1].y, paint);
                        break;
                    case TRIPLE:
                        PointF center = centerForDoubleBond(a1, a2);

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