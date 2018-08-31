package com.coldradio.benzene.drawer;

import android.graphics.Canvas;
import android.graphics.Paint;

import com.coldradio.benzene.project.Confiiguration;

public class ProjectDrawer {
    private static final ProjectDrawer instance = new ProjectDrawer();
    private Paint mPaint;

    private ProjectDrawer() {
        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPaint.setStrokeWidth(Confiiguration.LINE_THICKNESS);
    }

    public static ProjectDrawer instance() {
        return instance;
    }

    public void drawTo(Canvas canvas) {

    }
}
