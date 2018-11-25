package com.coldradio.benzene.view.drawer;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PointF;
import android.graphics.Rect;

import com.coldradio.benzene.project.Configuration;

public class TextDrawer {
    private static Rect drawSubscript(String txt, int start, int end, Rect prevBounds, boolean toRight, boolean bgOn, int bgColor, Canvas canvas, Paint paint) {
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

    private static void drawText(String text, PointF centerOfFirstLetter, boolean toRight, boolean bgOn, int bgColor, Canvas canvas, Paint paint) {
        Rect charBounds = DrawingLib.atomEnclosingRect(centerOfFirstLetter);

        if (toRight) {
            // 2. offset to left by the character's width
            charBounds.offset(-charBounds.width(), 0);
        } else {
            charBounds.offset(charBounds.width(), 0);
        }

        for (int ii = (toRight ? 0 : text.length() - 1); ii < text.length() && ii >= 0; ii = ii + (toRight ? 1 : -1)) {
            if (Character.isDigit(text.charAt(ii))) {
                charBounds = drawSubscript(text, ii, ii + 1, charBounds, toRight, bgOn, bgColor, canvas, paint);
            } else {
                charBounds = drawChar(text, ii, ii + 1, charBounds, toRight, bgOn, bgColor, canvas, paint);
            }
            // give some padding after the first letter
            charBounds.offset(toRight ? 3 : -3, 0);
        }
    }

    public static Rect drawSuperscript(String txt, Rect prevBounds, boolean bgOn, int bgColor, Canvas canvas, Paint paint) {
        return drawSuperscript(txt, 0, txt.length(), prevBounds, true, bgOn, bgColor, canvas, paint);
    }

    public static Rect drawSuperscript(String txt, int start, int end, Rect prevBounds, boolean toRight, boolean bgOn, int bgColor, Canvas canvas, Paint paint) {
        float origTextSize = paint.getTextSize();

        paint.setTextSize(origTextSize * Configuration.SUBSCRIPT_SIZE_RATIO);
        // when subscripting, the char is overlapped with the previous char. so give some padding
        prevBounds.offset(toRight ? 2 : 0, 0);

        prevBounds.offset(0, -prevBounds.height() / 2);
        Rect myBounds = drawChar(txt, start, end, prevBounds, toRight, bgOn, bgColor, canvas, paint);
        myBounds.offset(0, prevBounds.height() / 2);

        paint.setTextSize(origTextSize);

        return myBounds;
    }

    public static void draw(String text, PointF centerOfFirstLetter, boolean bgOn, int bgColor, Canvas canvas, Paint paint) {
        if (text.length() > 0 && text.charAt(0) == 'H') {
            drawText(text, centerOfFirstLetter, false, bgOn, bgColor, canvas, paint);
        } else {
            drawText(text, centerOfFirstLetter, true, bgOn, bgColor, canvas, paint);
        }
    }
}
