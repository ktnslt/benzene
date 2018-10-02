package com.coldradio.benzene.view;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PointF;

import com.coldradio.benzene.compound.Atom;
import com.coldradio.benzene.compound.Compound;
import com.coldradio.benzene.lib.Geometry;
import com.coldradio.benzene.lib.TreeTraveler;

public class GenericDrawer {
    public static boolean draw(Compound compound, Canvas canvas, Paint paint) {
        TreeTraveler.returnFirstEdge(new TreeTraveler.IEdgeVisitor() {
            @Override
            public boolean visit(Atom a1, Atom a2, Object... args) {
                Canvas canvas = (Canvas)args[0];
                Paint paint = (Paint)args[1];
                PointF p1 = a1.getPoint(), p2 = a2.getPoint();

                canvas.drawLine(p1.x, p1.y, p2.x, p2.y, paint);
                switch (a1.getBondType(a2)) {
                    case DOUBLE:
                        DrawingLibrary.drawDoubleBond(p1.x, p1.y, p2.x, p2.y, centerForDoubleBond(a1, a2), canvas, paint);
                        break;
                    case TRIPLE:
                        PointF center = centerForDoubleBond(a1, a2);

                        DrawingLibrary.drawDoubleBond(p1.x, p1.y, p2.x, p2.y, centerForDoubleBond(a1, a2), canvas, paint);
                        DrawingLibrary.drawDoubleBond(p1.x, p1.y, p2.x, p2.y, Geometry.symmetricPointToLine(center, a1.getPoint(), a2.getPoint()), canvas, paint);
                        break;
                }
                return false;
            }
        }, compound, canvas, paint);

        return true;
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
            int centerIndex = 0;
            int carbonBound_a1 = a1.carbonBoundNumber(), carbonBound_a2 = a2.carbonBoundNumber();

            // carbonBound == 1 is the primary carbon and the chain ends. == 2 means it has one additional branch, and before_a1 != null
            if (carbonBound_a1 == 2) {
                centerIndex = Geometry.sameSideOfLine(before_a1.getPoint(), centers[0], a1p, a2p) ? 0 : 1;
            } else if (carbonBound_a2 == 2) {
                centerIndex = Geometry.sameSideOfLine(after_a2.getPoint(), centers[0], a1p, a2p) ? 0 : 1;
            } else {
                centerIndex = 0;
            }

            if (a1.isNextDoubleBond(a2)) {
                centerIndex = (centerIndex + 1) % 2;
            }
            return centers[centerIndex];
        }
    }
}
