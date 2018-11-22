package com.coldradio.benzene.view.drawer;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PointF;

import com.coldradio.benzene.compound.Atom;
import com.coldradio.benzene.compound.Bond;
import com.coldradio.benzene.compound.Compound;
import com.coldradio.benzene.util.Geometry;
import com.coldradio.benzene.util.TreeTraveler;
import com.coldradio.benzene.project.Configuration;

public class GenericDrawer {
    private static void drawDoubleBond(float x1, float y1, float x2, float y2, PointF center, Canvas canvas, Paint paint) {
        if (center != null) {
            float distanceCenterToBond = Geometry.distanceFromPointToLine(center, new PointF(x1, y1), new PointF(x2, y2));
            float zoomOutRation = distanceCenterToBond < Configuration.LINE_LENGTH / 2 ? 0.55f : 0.8f;

            PointF p1 = Geometry.zoom(x1, y1, center, zoomOutRation);
            PointF p2 = Geometry.zoom(x2, y2, center, zoomOutRation);

            canvas.drawLine(p1.x, p1.y, p2.x, p2.y, paint);
        }
    }

    private static int centerBias(Atom atom, PointF center, PointF l1, PointF l2) {
        int bias = 0;

        for (Bond bond : atom.getBonds()) {
            bias += (Geometry.sameSideOfLine(bond.getBoundAtom().getPoint(), center, l1, l2) ? 1 : -1);
        }

        return bias;
    }

    private static PointF centerForDoubleBond(Atom a1, Atom a2) {
        PointF a1p = a1.getPoint(), a2p = a2.getPoint();
        Atom before_a1 = a1.getBoundAtomExcept(a2);
        Atom after_a2 = a2.getBoundAtomExcept(a1);

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

    public static boolean draw(Compound compound, Canvas canvas, Paint paint) {
        return draw(compound, canvas, paint, 0, 0);
    }

    public static boolean draw(Compound compound, Canvas canvas, Paint paint, float dx, float dy) {
        canvas.translate(dx, dy);
        // draw edges
        TreeTraveler.returnFirstEdge(new TreeTraveler.IEdgeVisitor() {
            @Override
            public boolean visit(Atom a1, Atom a2, Object... args) {
                if (! a1.isSelectable() || ! a2.isSelectable())
                    return false;

                Canvas canvas = (Canvas) args[0];
                Paint paint = (Paint) args[1];
                PointF p1 = a1.getPoint(), p2 = a2.getPoint();

                switch (a1.getBondType(a2)) {
                    case SINGLE:
                        canvas.drawLine(p1.x, p1.y, p2.x, p2.y, paint);
                        break;
                    case DOUBLE:
                    case DOUBLE_OTHER_SIDE:
                        canvas.drawLine(p1.x, p1.y, p2.x, p2.y, paint);
                        drawDoubleBond(p1.x, p1.y, p2.x, p2.y, centerForDoubleBond(a1, a2), canvas, paint);
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
                        drawDoubleBond(p1.x, p1.y, p2.x, p2.y, centerForDoubleBond(a1, a2), canvas, paint);
                        drawDoubleBond(p1.x, p1.y, p2.x, p2.y, Geometry.symmetricPointToLine(center, a1.getPoint(), a2.getPoint()), canvas, paint);
                        break;
                }
                return false;
            }
        }, compound, canvas, paint);

        return true;
    }
}