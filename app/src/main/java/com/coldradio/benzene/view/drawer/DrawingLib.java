package com.coldradio.benzene.view.drawer;

import android.graphics.PointF;
import android.graphics.Rect;

public class DrawingLib {
    public static Rect atomEnclosingRect(PointF atomXY) {
        Rect charBounds = new Rect(0, 0, PaintSet.instance().fontWidth(PaintSet.PaintType.GENERAL), PaintSet.instance().fontHeight(PaintSet.PaintType.GENERAL));

        charBounds.offsetTo((int) atomXY.x - charBounds.width() / 2, (int) atomXY.y - charBounds.height() / 2);

        return charBounds;
    }
}
