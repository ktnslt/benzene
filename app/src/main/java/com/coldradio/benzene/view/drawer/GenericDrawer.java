package com.coldradio.benzene.view.drawer;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PointF;
import android.graphics.Rect;

import com.coldradio.benzene.compound.Atom;
import com.coldradio.benzene.compound.Bond;
import com.coldradio.benzene.compound.Compound;
import com.coldradio.benzene.lib.AtomicNumber;
import com.coldradio.benzene.lib.Geometry;
import com.coldradio.benzene.lib.TreeTraveler;
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

    private static int indexSameSideOfLineBondAndCenter(Atom atom, Atom lineAtom, PointF[] centers) {
        for (Bond bond : atom.getBonds()) {
            Atom boundAtom = bond.getBoundAtom();

            if (boundAtom != lineAtom) {
                if (Geometry.sameSideOfLine(boundAtom.getPoint(), centers[0], atom.getPoint(), lineAtom.getPoint())) {
                    return 0;
                } else if (Geometry.sameSideOfLine(boundAtom.getPoint(), centers[1], atom.getPoint(), lineAtom.getPoint())) {
                    return 1;
                }
            }
        }
        return 0;
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
            int centerIndex;
            int bondNumber_a1 = a1.bondNumber(), bondNumber_a2 = a2.bondNumber();

            if (bondNumber_a1 == 0 && bondNumber_a2 == 0) {
                centerIndex = 0;
            } else if (bondNumber_a1 >= bondNumber_a2) {
                centerIndex = indexSameSideOfLineBondAndCenter(a1, a2, centers);
            } else {
                centerIndex = indexSameSideOfLineBondAndCenter(a2, a1, centers);
            }

            if (a1.isNextDoubleBond(a2)) {
                centerIndex = (centerIndex + 1) % 2;
            }
            return centers[centerIndex];
        }
    }

    private static void drawTextToCenter(PointF center, String text, boolean bgOn, int bgColor, Canvas canvas, Paint paint) {
        int prevColor = paint.getColor();
        Rect bounds = new Rect();

        paint.getTextBounds(text, 0, text.length(), bounds);

        float textWidth = (bounds.right - bounds.left) + 10, textHeight = (bounds.bottom - bounds.top) + 10;

        if (bgOn) {
            paint.setColor(bgColor);
            canvas.drawRect(center.x - textWidth / 2, center.y - textHeight / 2, center.x + textWidth / 2, center.y + textHeight / 2, paint);
            paint.setColor(prevColor);
        }
        canvas.drawText(text, center.x - textWidth / 2, center.y + textHeight / 2 + 2, paint);
    }

    private static void drawMarker(Atom atom, Canvas canvas, Paint paint) {
        PointF xy = new PointF(atom.getPoint().x, atom.getPoint().y);
        int offset = 20;

        switch (atom.getMarker()) {
            case LEFT_TOP:
                xy.offset(-offset, -offset);
                break;
            case RIGHT_TOP:
                xy.offset(offset, -offset);
                break;
            case LEFT_BOTTOM:
                xy.offset(-offset, offset);
                break;
            case RIGHT_BOTTOM:
                xy.offset(offset, offset);
                break;
        }
        drawTextToCenter(xy, Character.toString(Configuration.ATOM_MARKER), false, 0, canvas, paint);
    }

    public static boolean draw(Compound compound, Canvas canvas, Paint paint) {
        // draw edges
        TreeTraveler.returnFirstEdge(new TreeTraveler.IEdgeVisitor() {
            @Override
            public boolean visit(Atom a1, Atom a2, Object... args) {
                Canvas canvas = (Canvas) args[0];
                Paint paint = (Paint) args[1];
                PointF p1 = a1.getPoint(), p2 = a2.getPoint();

                canvas.drawLine(p1.x, p1.y, p2.x, p2.y, paint);
                switch (a1.getBondType(a2)) {
                    case DOUBLE:
                        drawDoubleBond(p1.x, p1.y, p2.x, p2.y, centerForDoubleBond(a1, a2), canvas, paint);
                        break;
                    case TRIPLE:
                        PointF center = centerForDoubleBond(a1, a2);

                        drawDoubleBond(p1.x, p1.y, p2.x, p2.y, centerForDoubleBond(a1, a2), canvas, paint);
                        drawDoubleBond(p1.x, p1.y, p2.x, p2.y, Geometry.symmetricPointToLine(center, a1.getPoint(), a2.getPoint()), canvas, paint);
                        break;
                }
                return false;
            }
        }, compound, canvas, paint);

        // draw Atom name when it is NOT carbon. draw this later unless it will be overwritten by the edge
        TreeTraveler.returnFirstAtom(new TreeTraveler.IAtomVisitor() {
            @Override
            public boolean visit(Atom atom, Object... args) {
                Canvas canvas = (Canvas) args[0];
                Paint paint = (Paint) args[1];

                if (atom.getAtomicNumber() != AtomicNumber.C) {
                    drawTextToCenter(atom.getPoint(), atom.getAtomicNumber().toString(), true, Color.WHITE, canvas, paint);
                }
                if (atom.getMarker() != Atom.Marker.NONE) {
                    drawMarker(atom, canvas, paint);
                }
                return false;
            }
        }, compound, canvas, paint);

        return true;
    }
}