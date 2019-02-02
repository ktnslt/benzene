package com.coldradio.benzene.view.drawer;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PointF;
import android.graphics.Rect;

import com.coldradio.benzene.compound.Atom;
import com.coldradio.benzene.compound.AtomDecoration;
import com.coldradio.benzene.compound.AtomicNumber;
import com.coldradio.benzene.compound.Compound;
import com.coldradio.benzene.compound.CompoundInspector;
import com.coldradio.benzene.project.Configuration;
import com.coldradio.benzene.util.Geometry;
import com.coldradio.benzene.util.TreeTraveler;

public class AtomDecorationDrawer implements ICompoundDrawer {
    private static String atomToString(Atom atom) {
        String text = atom.getAtomicNumber().toString();

        if (atom.getAtomicNumber() == AtomicNumber.TEXT) {
            text = atom.getArbitraryName();
        } else if (atom.getAtomDecoration().isLettering()) {
            int hNumber = CompoundInspector.numberOfHydrogen(atom);
            boolean hydrogenInRight = CompoundInspector.moreHydrogensInRight(atom);

            if (hNumber == 1) {
                text = hydrogenInRight ? text + "H" : "H" + text;
            } else if (hNumber > 1) {
                text = hydrogenInRight ? text + "H" + String.valueOf(hNumber) : "H" + String.valueOf(hNumber) + text;
            }
        }

        return text;
    }

    private static void drawMarker(Atom atom, Canvas canvas, Paint paint) {
        PointF atomXY = atom.getPoint();
        PointF xy = new PointF(atomXY.x, atomXY.y);

        xy.offset(0, -35);
        xy = Geometry.cwRotate(xy, atomXY, (float) Math.toRadians(45 * (atom.getAtomDecoration().getMarker().ordinal() - 1)));
        xy.offset(0, 15);

        DrawingLib.drawText(Character.toString(Configuration.ATOM_MARKER), xy, false, 0, canvas, paint);
    }

    private static void drawUnsharedElectron(Atom atom, Canvas canvas, Paint paint) {
        int fontHeight = PaintSet.instance().fontHeight(PaintSet.PaintType.GENERAL);
        int fontWidth =  PaintSet.instance().fontWidth(PaintSet.PaintType.GENERAL);
        PointF xy = atom.getPoint();
        AtomDecoration atomDecoration = atom.getAtomDecoration();
        final float padding = 8;

        for (AtomDecoration.Direction direction : AtomDecoration.Direction.values()) {
            if (atomDecoration.getUnsharedElectron(direction) != AtomDecoration.UnsharedElectron.NONE) {
                float eleX = xy.x, eleY = xy.y;

                // calculate the position
                if (direction == AtomDecoration.Direction.TOP) {
                    eleY = xy.y - fontHeight / 2 - padding;
                } else if (direction == AtomDecoration.Direction.BOTTOM) {
                    // in case of RIGHT, BOTTOM, more padding is added
                    eleY = xy.y + fontHeight / 2 + padding + 3;
                } else if (direction == AtomDecoration.Direction.LEFT) {
                    eleX = xy.x - fontWidth / 2 - padding;
                } else {
                    eleX = xy.x + fontWidth / 2 + padding + 3;
                }

                // draw electron
                if (atomDecoration.getUnsharedElectron(direction) == AtomDecoration.UnsharedElectron.SINGLE) {
                    canvas.drawCircle(eleX, eleY, Configuration.ELECTRON_RADIUS, paint);
                } else {
                    if (direction == AtomDecoration.Direction.TOP || direction == AtomDecoration.Direction.BOTTOM) {
                        canvas.drawCircle(eleX - fontWidth / 4, eleY, Configuration.ELECTRON_RADIUS, paint);
                        canvas.drawCircle(eleX + fontWidth / 4, eleY, Configuration.ELECTRON_RADIUS, paint);
                    } else {
                        canvas.drawCircle(eleX, eleY - fontHeight / 4, Configuration.ELECTRON_RADIUS, paint);
                        canvas.drawCircle(eleX, eleY + fontHeight / 4, Configuration.ELECTRON_RADIUS, paint);
                    }
                }
            }
        }
    }

    private static void drawChargeAsCircle(Atom atom, Canvas canvas, Paint paint) {
        PointF atomXY = atom.getPoint();
        PointF xy = new PointF(atomXY.x, atomXY.y);
        Paint.Style origPaintStyle = paint.getStyle();

        paint.setStyle(Paint.Style.STROKE);

        // TODO: assume this is GENERAL type, could be a bug, but what else.., one possible solution is to pass the PaintType not the paint object
        xy.offset(0, -PaintSet.instance().fontHeight(PaintSet.PaintType.GENERAL));
        xy = Geometry.cwRotate(xy, atomXY, (float) Math.toRadians(45 * (atom.getAtomDecoration().getChargeAsCircle().ordinal() - 1)));

        // draw circle
        canvas.drawCircle(xy.x, xy.y, Configuration.CHARGE_CIRCLE_RADIUS, paint);
        // draw negative
        float positiveRadius = Configuration.CHARGE_CIRCLE_RADIUS / 2;
        canvas.drawLine(xy.x - positiveRadius, xy.y, xy.x + positiveRadius, xy.y, paint);

        if (atom.getAtomDecoration().getCharge() == 1) {
            canvas.drawLine(xy.x, xy.y - positiveRadius, xy.x, xy.y + positiveRadius, paint);
        }

        paint.setStyle(origPaintStyle);
    }

    private static Rect rect = new Rect();
    private static void drawChargeAsNumber(Atom atom, Canvas canvas, Paint paint) {
        int charge = atom.getAtomDecoration().getCharge();
        PointF xy = atom.getPoint();
        String atomName = atom.getAtomicNumber().toString();

        if (charge == 1) {
            DrawingLib.drawTextSuperscript("+", DrawingLib.atomEnclosingRect(atomName, xy, rect), false, 0, canvas, paint);
        } else if (charge == -1) {
            DrawingLib.drawTextSuperscript("-", DrawingLib.atomEnclosingRect(atomName, xy, rect), false, 0, canvas, paint);
        } else if (charge > 1) {
            DrawingLib.drawTextSuperscript(String.valueOf(charge) + "+", DrawingLib.atomEnclosingRect(atomName, xy, rect), false, 0, canvas, paint);
        } else if (charge < -1) {
            DrawingLib.drawTextSuperscript(String.valueOf(-charge) + "-", DrawingLib.atomEnclosingRect(atomName, xy, rect), false, 0, canvas, paint);
        }
    }

    private static void drawCharge(Atom atom, Canvas canvas, Paint paint) {
        AtomDecoration atomDecoration = atom.getAtomDecoration();
        int charge = atomDecoration.getCharge();

        if (charge * charge == 1 && atomDecoration.getChargeAsCircle() != AtomDecoration.Marker.NONE) {
            drawChargeAsCircle(atom, canvas, paint);
        } else {
            drawChargeAsNumber(atom, canvas, paint);
        }
    }

    @Override
    public boolean draw(Compound compound, Canvas canvas, Paint paint) {
        TreeTraveler.returnFirstAtom(new TreeTraveler.AtomVisitorAlwaysTravelDown() {
            @Override
            public boolean visit(Atom atom, int distanceFromRoot, Object... args) {
                Canvas canvas = (Canvas) args[0];
                Paint paint = (Paint) args[1];
                AtomDecoration atomDecoration = atom.getAtomDecoration();

                if (atomDecoration.getShowElementName() || atomDecoration.isLettering()
                        || atom.getAtomicNumber() == AtomicNumber.TEXT) {
                    DrawingLib.drawText(atomToString(atom), atom.getPoint(), true, Color.WHITE, canvas, paint);
                }
                if (atomDecoration.getMarker() != AtomDecoration.Marker.NONE) {
                    drawMarker(atom, canvas, paint);
                }
                if (atomDecoration.getCharge() != 0) {
                    drawCharge(atom, canvas, paint);
                }
                drawUnsharedElectron(atom, canvas, paint);

                return false;
            }
        }, compound, canvas, paint);
        return true;
    }
}
