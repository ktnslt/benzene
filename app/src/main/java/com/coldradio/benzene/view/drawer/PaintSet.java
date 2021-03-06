package com.coldradio.benzene.view.drawer;

import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Typeface;

import com.coldradio.benzene.project.Configuration;

public class PaintSet {
    public enum PaintType {
        GENERAL, THICK, GUIDE_LINE
    }
    private Paint[] mPaints = new Paint[PaintType.values().length];
    private Rect[] mTextBounds = new Rect[PaintType.values().length];
    private static PaintSet msInstance = new PaintSet();

    private PaintSet() {
        int ii = 0;

        for (ii = 0; ii < mTextBounds.length; ++ii) {
            mTextBounds[ii] = new Rect();
        }
        // GENERAL
        ii = 0;
        mPaints[ii] = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPaints[ii].setStrokeWidth(Configuration.BOND_THICKNESS);
        mPaints[ii].setTextSize(Configuration.FONT_SIZE);
        mPaints[ii].setStyle(Paint.Style.FILL);
        mPaints[ii].setTypeface(Typeface.create("Consolas", Typeface.NORMAL));
        mPaints[ii].getTextBounds("O", 0, 1, mTextBounds[ii]);

        // THICK
        ii = 1;
        mPaints[ii] = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPaints[ii].setStrokeWidth(Configuration.BOND_THICKNESS * 7);
        mPaints[ii].setStrokeCap(Paint.Cap.ROUND);
        mPaints[ii].setStyle(Paint.Style.STROKE);
        mPaints[ii].setARGB(180, 140, 180, 250);
        mPaints[ii].getTextBounds("O", 0, 1, mTextBounds[ii]);

        // GUIDE_LINE
        ii = 2;
        mPaints[ii] = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPaints[ii].setStrokeWidth(Configuration.BOND_THICKNESS * 3);
        mPaints[ii].setStrokeCap(Paint.Cap.ROUND);
        mPaints[ii].setStyle(Paint.Style.FILL);
        mPaints[ii].setARGB(180, 255, 200, 14);
        mPaints[ii].getTextBounds("O", 0, 1, mTextBounds[ii]);
    }

    public static PaintSet instance() {
        return msInstance;
    }

    public Paint paint(PaintType paintType) {
        return mPaints[paintType.ordinal()];
    }

    public int fontHeight(PaintType paintType) {
        return mTextBounds[paintType.ordinal()].height();
    }

    public int fontWidth(PaintType paintType) {
        return mTextBounds[paintType.ordinal()].width();
    }
}
