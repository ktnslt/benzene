package com.coldradio.benzene.view.drawer;

import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.Paint;
import android.graphics.Typeface;

import com.coldradio.benzene.project.Configuration;

public class PaintSet {
    private Paint mPaint;
    private Paint mThickPaint;
    private Paint mDashedLinePaint;
    private static PaintSet msInstance = new PaintSet();

    private PaintSet() {
        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPaint.setStrokeWidth(Configuration.LINE_THICKNESS);
        mPaint.setTextSize(Configuration.FONT_SIZE);
        mPaint.setStyle(Paint.Style.FILL);
        mPaint.setTypeface(Typeface.create("Arial", Typeface.NORMAL));

        mThickPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mThickPaint.setStrokeWidth(Configuration.LINE_THICKNESS * 7);
        mThickPaint.setStrokeCap(Paint.Cap.ROUND);
        mThickPaint.setStyle(Paint.Style.STROKE);
        mThickPaint.setColor(Color.rgb(140, 180, 250));

        mDashedLinePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mDashedLinePaint.setStrokeWidth(4);
        mDashedLinePaint.setColor(Color.rgb(60, 60, 60));
        mDashedLinePaint.setStyle(Paint.Style.STROKE);
        mDashedLinePaint.setPathEffect(new DashPathEffect(new float[]{5, 5},0));
    }

    public static PaintSet instance() {
        return msInstance;
    }

    public Paint general() {
        return mPaint;
    }

    public Paint thick() {
        return mThickPaint;
    }

    public Paint dashedLine() {
        return mDashedLinePaint;
    }
}
