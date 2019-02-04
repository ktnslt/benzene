package com.coldradio.benzene.view.drawer;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PointF;
import android.graphics.Rect;
import android.graphics.RectF;

import com.coldradio.benzene.compound.Atom;
import com.coldradio.benzene.compound.AtomicNumber;
import com.coldradio.benzene.compound.Bond;
import com.coldradio.benzene.project.Configuration;
import com.coldradio.benzene.util.Geometry;

public class DrawingLib {
    private static Rect drawTextSubscript(String txt, int start, int end, Rect prevBounds, boolean toRight, boolean bgOn, int bgColor, Canvas canvas, Paint paint) {
        float origTextSize = paint.getTextSize();

        paint.setTextSize(origTextSize * Configuration.SUBSCRIPT_SIZE_RATIO);
        // when subscripting, the char is overlapped with the previous char. so give some padding
        prevBounds.offset(toRight ? 2 : 0, 0);

        Rect myBounds = drawChar(txt, start, end, prevBounds, toRight, bgOn, bgColor, canvas, paint);
        paint.setTextSize(origTextSize);

        return myBounds;
    }

    private static Rect drawChar(String txt, int start, int end, Rect prevBounds, boolean toRight, boolean bgOn, int bgColor, Canvas canvas, Paint paint) {
        int prevColor = paint.getColor();
        Rect myBounds = new Rect();

        // calc my bounds
        paint.getTextBounds(txt, start, end, myBounds);
        if (toRight) {
            myBounds.offsetTo(prevBounds.right, prevBounds.bottom - myBounds.height());
        } else {
            myBounds.offsetTo(prevBounds.left, prevBounds.bottom - myBounds.height());
            myBounds.offset(-myBounds.width(), 0);
        }

        if (bgOn) {
            paint.setColor(bgColor);
            canvas.drawRect(myBounds, paint);
            paint.setColor(prevColor);
        }
        paint.setStyle(Paint.Style.FILL);
        canvas.drawText(txt, start, end, myBounds.left, myBounds.bottom, paint);

        return myBounds;
    }

    private static Rect charBounds = new Rect();
    private static void drawText(String text, PointF centerOfFirstLetter, boolean toRight, boolean bgOn, int bgColor, Canvas canvas, Paint paint) {
        charBounds = DrawingLib.atomEnclosingRect("C", centerOfFirstLetter, charBounds);
        int topOrigTextLine = charBounds.top;

        if (toRight) {
            // 2. offset to left by the character's width
            charBounds.offset(-charBounds.width(), 0);
        } else {
            charBounds.offset(charBounds.width(), 0);
        }

        for (int ii = (toRight ? 0 : text.length() - 1); ii < text.length() && ii >= 0; ii = ii + (toRight ? 1 : -1)) {
            if (Character.isDigit(text.charAt(ii))) {
                charBounds = drawTextSubscript(text, ii, ii + 1, charBounds, toRight, bgOn, bgColor, canvas, paint);
                charBounds.top = topOrigTextLine;
            } else if (text.charAt(ii) == '+' || text.charAt(ii) == '-') {
                charBounds = drawTextSuperscript(text, ii, ii + 1, charBounds, toRight, bgOn, bgColor, canvas, paint);
                charBounds.top = topOrigTextLine;
            } else {
                charBounds = drawChar(text, ii, ii + 1, charBounds, toRight, bgOn, bgColor, canvas, paint);
            }
            // give some padding after the first letter
            charBounds.offset(toRight ? 3 : -3, 0);
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

    static Rect atomEnclosingRect(String atomName, PointF atomXY, Rect result) {
        int oneCharWidth = PaintSet.instance().fontWidth(PaintSet.PaintType.GENERAL), oneCharHeight = PaintSet.instance().fontHeight(PaintSet.PaintType.GENERAL);

        result.set(0, 0, oneCharWidth * atomName.length(), oneCharHeight);
        result.offsetTo((int) atomXY.x - oneCharWidth / 2, (int) atomXY.y - oneCharHeight / 2);

        return result;
    }

    private static RectF msRectF = new RectF();
    static void drawCwArc(PointF start, PointF end, PointF center, Canvas canvas, Paint paint) {
        // https://stackoverflow.com/questions/4196749/draw-arc-with-2-points-and-center-of-the-circle
        float r = Geometry.distanceFromPointToPoint(start, center);
        float startAngle = (float) (180 / Math.PI * Math.atan2(start.y - center.y, start.x - center.x));
        float endAngle_minus_startAngle = (float) (180 / Math.PI * Math.atan2(end.y - center.y, end.x - center.x)) - startAngle;

        if (endAngle_minus_startAngle < 0) {
            endAngle_minus_startAngle += 360;
        }
        msRectF.set(center.x - r, center.y - r, center.x + r, center.y + r);
        canvas.drawArc(msRectF, startAngle, endAngle_minus_startAngle, false, paint);
    }

    static Rect drawTextSuperscript(String txt, Rect prevBounds, boolean bgOn, int bgColor, Canvas canvas, Paint paint) {
        return drawTextSuperscript(txt, 0, txt.length(), prevBounds, true, bgOn, bgColor, canvas, paint);
    }

    static Rect drawTextSuperscript(String txt, int start, int end, Rect prevBounds, boolean toRight, boolean bgOn, int bgColor, Canvas canvas, Paint paint) {
        float origTextSize = paint.getTextSize();

        paint.setTextSize(origTextSize * Configuration.SUPERSCRIPT_SIZE_RATIO);
        // when subscripting, the char is overlapped with the previous char. so give some padding
        prevBounds.offset(toRight ? 2 : 0, 0);

        prevBounds.offset(0, -prevBounds.height() / 2);
        Rect myBounds = drawChar(txt, start, end, prevBounds, toRight, bgOn, bgColor, canvas, paint);
        myBounds.offset(0, prevBounds.height() / 2);

        paint.setTextSize(origTextSize);

        return myBounds;
    }

    static void drawText(String text, PointF centerOfFirstLetter, boolean bgOn, int bgColor, Canvas canvas, Paint paint) {
        if (text.length() > 0 && text.charAt(0) == 'H') {
            drawText(text, centerOfFirstLetter, false, bgOn, bgColor, canvas, paint);
        } else {
            drawText(text, centerOfFirstLetter, true, bgOn, bgColor, canvas, paint);
        }
    }

    public static PointF centerForDoubleBond(Atom a1, Atom a2, boolean opposite) {
        PointF a1p = a1.getPoint(), a2p = a2.getPoint();
        Atom before_a1 = a1.getSkeletonAtomExcept(a2);
        Atom after_a2 = a2.getSkeletonAtomExcept(a1);

        if (before_a1 != null && after_a2 != null && before_a1 == after_a2) {
            // propane case, returns the center of the triangle
            PointF before_a1p = before_a1.getPoint();
            PointF center = new PointF((a1p.x + a2p.x + before_a1p.x) / 3, (a1p.y + a2p.y + before_a1p.y) / 3);

            return opposite ? Geometry.symmetricToLine(center, a1p, a2p) : center;
        } else {
            PointF[] centers = Geometry.regularTrianglePoint(a1p, a2p);
            int centerIndex;
            // next prefer the center side with more substituents
            int centerBiasTo0 = 0;

            centerBiasTo0 += centerBias(a1, centers[0], a1p, a2p);
            centerBiasTo0 += centerBias(a2, centers[0], a1p, a2p);
            centerIndex = centerBiasTo0 > 0 ? 0 : 1;

            if (opposite) {
                centerIndex = (centerIndex + 1) % 2;
            }
            return centers[centerIndex];
        }
    }
}
