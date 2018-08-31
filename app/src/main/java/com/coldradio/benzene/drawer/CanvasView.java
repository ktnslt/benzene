package com.coldradio.benzene.drawer;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.view.View;

public class CanvasView extends View {
    public CanvasView(Context context) {
        super(context);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        ProjectDrawer.instance().drawTo(canvas);
    }
}
