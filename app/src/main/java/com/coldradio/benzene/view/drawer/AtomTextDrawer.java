package com.coldradio.benzene.view.drawer;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.PointF;
import android.graphics.Rect;
import android.graphics.RectF;

import com.coldradio.benzene.compound.Atom;
import com.coldradio.benzene.compound.AtomicNumber;
import com.coldradio.benzene.compound.Bond;
import com.coldradio.benzene.project.Configuration;

public class AtomTextDrawer {
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
        bounds.offsetTo((int)leftBottom.x, (int)leftBottom.y - bounds.height());

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
                leftBottom.y += subscriptDown;
                drawChar(c, leftBottom, bgOn, bgColor, canvas, paint);
                leftBottom.y -= subscriptDown;
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
