package com.coldradio.benzene.view.drawer;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PointF;
import android.graphics.Rect;

import com.coldradio.benzene.compound.Atom;
import com.coldradio.benzene.compound.AtomDecoration;
import com.coldradio.benzene.compound.AtomicNumber;
import com.coldradio.benzene.compound.Bond;
import com.coldradio.benzene.compound.Compound;
import com.coldradio.benzene.project.Configuration;
import com.coldradio.benzene.util.Geometry;
import com.coldradio.benzene.util.TreeTraveler;

public class AtomDecorationDrawer implements ICompoundDrawer {
    private static boolean isNameDrawable(Atom atom) {
        AtomicNumber an = atom.getAtomicNumber();

        if (an == AtomicNumber.C) {
            return atom.getHydrogenMode() == Atom.HydrogenMode.LETTERING_H;
        } else if (an == AtomicNumber.H && atom.bondNumber() == 1) {
            return atom.isSelectable();
        }
        return true;
    }

    private static void drawMarker(Atom atom, Canvas canvas, Paint paint) {
        PointF atomXY = atom.getPoint();
        PointF xy = new PointF(atomXY.x, atomXY.y);

        xy.offset(0, -35);
        xy = Geometry.rotatePoint(xy, atomXY, (float) Math.toRadians(45 * (atom.getAtomDecoration().getMarker().ordinal() - 1)));
        xy.offset(0, 15);

        AtomTextDrawer.draw(Character.toString(Configuration.ATOM_MARKER), xy, false, 0, canvas, paint);
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

        paint.setStyle(Paint.Style.STROKE);

        // TODO: assume this is GENERAL type, could be a bug, but what else.., one possible solution is to pass the PaintType not the paint object
        xy.offset(0, -PaintSet.instance().fontHeight(PaintSet.PaintType.GENERAL));
        xy = Geometry.rotatePoint(xy, atomXY, (float) Math.toRadians(45 * (atom.getAtomDecoration().getChargeAsCircle().ordinal() - 1)));

        // draw circle
        canvas.drawCircle(xy.x, xy.y, Configuration.CHARGE_CIRCLE_RADIUS, paint);
        // draw negative
        float positiveRadius = Configuration.CHARGE_CIRCLE_RADIUS / 2;
        canvas.drawLine(xy.x - positiveRadius, xy.y, xy.x + positiveRadius, xy.y, paint);

        if (atom.getAtomDecoration().getCharge() == 1) {
            canvas.drawLine(xy.x, xy.y - positiveRadius, xy.x, xy.y + positiveRadius, paint);
        }
    }

    private static void drawChargeAsNumber(Atom atom, Canvas canvas, Paint paint) {
        int charge = atom.getAtomDecoration().getCharge();

        if (charge == 1) {

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
        TreeTraveler.returnFirstAtom(new TreeTraveler.IAtomVisitor() {
            @Override
            public boolean visit(Atom atom, Object... args) {
                Canvas canvas = (Canvas) args[0];
                Paint paint = (Paint) args[1];
                AtomDecoration atomDecoration = atom.getAtomDecoration();

                if (isNameDrawable(atom)) {
                    AtomTextDrawer.draw(atom, true, Color.WHITE, canvas, paint);
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

    @Override
    public String getID() {
        return "AtomDecorationDrawer";
    }
}

class AtomTextDrawer {
    private static boolean hydrogenPositionInRight(Atom atom) {
        int right = 0;

        for (Bond bond : atom.getBonds()) {
            Atom boundAtom = bond.getBoundAtom();

            if (boundAtom.getAtomicNumber() == AtomicNumber.H) {
                right += atom.getPoint().x <= bond.getBoundAtom().getPoint().x ? 1 : -1;
            }
        }

        return right >= 0;
    }

    private static String atomToString(Atom atom) {
        String text = atom.getAtomicNumber().toString();

        if (atom.getAtomicNumber() == AtomicNumber.TEXT) {
            text = atom.getArbitraryName();
        } else if (atom.getHydrogenMode() == Atom.HydrogenMode.LETTERING_H) {
            int hNumber = atom.bondNumber(AtomicNumber.H);
            boolean hydrogenInRight = hydrogenPositionInRight(atom);

            if (hNumber == 1) {
                text = hydrogenInRight ? text + "H" : "H" + text;
            } else if (hNumber > 1) {
                text = hydrogenInRight ? text + "H" + String.valueOf(hNumber) : "H" + String.valueOf(hNumber) + text;
            }
        }

        return text;
    }

    private static void drawChar(char c, PointF leftBottom, boolean bgOn, int bgColor, Canvas canvas, Paint paint) {
        int prevColor = paint.getColor();
        Rect bounds = new Rect();

        paint.getTextBounds(String.valueOf(c), 0, 1, bounds);
        bounds.offsetTo((int) leftBottom.x, (int) leftBottom.y - bounds.height());

        if (bgOn) {
            paint.setColor(bgColor);
            canvas.drawRect(bounds, paint);
            paint.setColor(prevColor);
        }
        canvas.drawText(String.valueOf(c), bounds.left, bounds.bottom, paint);
    }

    private static void nextLeftBottomPosition(PointF leftBottom, char c, boolean toRight, Paint paint) {
        Rect bounds = new Rect();

        paint.getTextBounds(String.valueOf(c), 0, 1, bounds);
        bounds.right += 3;

        if (toRight) {
            leftBottom.offset(bounds.width(), 0);
        } else {
            leftBottom.offset(-bounds.width(), 0);
        }
    }

    private static void drawTextToRight(String text, PointF centerOfFirstLetter, boolean bgOn, int bgColor, Canvas canvas, Paint paint) {
        Rect bounds = new Rect();
        float origTextSize = paint.getTextSize();
        PointF leftBottom = new PointF();

        // decide the left bottom position of Text
        paint.getTextBounds(text, 0, 1, bounds);
        leftBottom.offset(centerOfFirstLetter.x - bounds.width() / 2, centerOfFirstLetter.y + bounds.height() / 2);

        float subscriptDown = bounds.height() * 0.3f;
        for (int ii = 0; ii < text.length(); ++ii) {
            char c = text.charAt(ii);

            if (Character.isDigit(c)) {
                paint.setTextSize(origTextSize * Configuration.SUBSCRIPT_SIZE_RATIO);
                //leftBottom.y += subscriptDown;
                drawChar(c, leftBottom, bgOn, bgColor, canvas, paint);
                //leftBottom.y -= subscriptDown;
                paint.setTextSize(origTextSize);
            } else {
                drawChar(c, leftBottom, bgOn, bgColor, canvas, paint);
            }
            nextLeftBottomPosition(leftBottom, c, true, paint);
        }
    }

    private static void drawTextToLeft(String text, PointF centerOfFirstLetter, boolean bgOn, int bgColor, Canvas canvas, Paint paint) {
        Rect bounds = new Rect();
        float origTextSize = paint.getTextSize();
        PointF leftBottom = new PointF();

        // decide the left bottom position of Text
        paint.getTextBounds(text, text.length() - 1, text.length(), bounds);
        leftBottom.offset(centerOfFirstLetter.x - bounds.width() / 2, centerOfFirstLetter.y + bounds.height() / 2);

        float subscriptDown = bounds.height() * 0.3f;
        for (int ii = text.length() - 1; ii >= 0; --ii) {
            char c = text.charAt(ii);

            if (Character.isDigit(c)) {
                paint.setTextSize(origTextSize * Configuration.SUBSCRIPT_SIZE_RATIO);
                leftBottom.y += subscriptDown;
                drawChar(c, leftBottom, bgOn, bgColor, canvas, paint);
                leftBottom.y -= subscriptDown;
                paint.setTextSize(origTextSize);
            } else {
                drawChar(c, leftBottom, bgOn, bgColor, canvas, paint);
            }
            if (ii > 0) {
                nextLeftBottomPosition(leftBottom, text.charAt(ii - 1), false, paint);
            }
        }
    }

    public static void draw(String text, PointF centerOfFirstLetter, boolean bgOn, int bgColor, Canvas canvas, Paint paint) {
        paint.setStyle(Paint.Style.FILL);

        if (text.length() > 0 && text.charAt(0) == 'H') {
            drawTextToLeft(text, centerOfFirstLetter, bgOn, bgColor, canvas, paint);
        } else {
            drawTextToRight(text, centerOfFirstLetter, bgOn, bgColor, canvas, paint);
        }
    }

    public static void draw(Atom atom, boolean bgOn, int bgColor, Canvas canvas, Paint paint) {
        draw(atomToString(atom), atom.getPoint(), bgOn, bgColor, canvas, paint);
    }
}