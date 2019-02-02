package com.coldradio.benzene.view.drawer;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PointF;
import android.graphics.Rect;
import android.graphics.RectF;

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

    public static Rect atomEnclosingRect(String atomName, PointF atomXY, Rect result) {
        int oneCharWidth = PaintSet.instance().fontWidth(PaintSet.PaintType.GENERAL), oneCharHeight = PaintSet.instance().fontHeight(PaintSet.PaintType.GENERAL);

        result.set(0, 0, oneCharWidth * atomName.length(), oneCharHeight);
        result.offsetTo((int) atomXY.x - oneCharWidth / 2, (int) atomXY.y - oneCharHeight / 2);

        return result;
    }

    private static RectF msRectF = new RectF();
    public static void drawCwArc(PointF start, PointF end, PointF center, Canvas canvas, Paint paint) {
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

    public static Rect drawTextSuperscript(String txt, Rect prevBounds, boolean bgOn, int bgColor, Canvas canvas, Paint paint) {
        return drawTextSuperscript(txt, 0, txt.length(), prevBounds, true, bgOn, bgColor, canvas, paint);
    }

    public static Rect drawTextSuperscript(String txt, int start, int end, Rect prevBounds, boolean toRight, boolean bgOn, int bgColor, Canvas canvas, Paint paint) {
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

    public static void drawText(String text, PointF centerOfFirstLetter, boolean bgOn, int bgColor, Canvas canvas, Paint paint) {
        if (text.length() > 0 && text.charAt(0) == 'H') {
            drawText(text, centerOfFirstLetter, false, bgOn, bgColor, canvas, paint);
        } else {
            drawText(text, centerOfFirstLetter, true, bgOn, bgColor, canvas, paint);
        }
    }
}
