package com.coldradio.benzene.view;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PointF;

import com.coldradio.benzene.compound.Compound;
import com.coldradio.benzene.compound.CompoundReactor;
import com.coldradio.benzene.project.Configuration;

import java.util.ArrayList;
import java.util.List;

public class CompoundDrawer {
    interface ICompoundDrawer {
        boolean draw(Compound compound, Canvas canvas, Paint paint);

        String getID();
    }

    private Paint mPaint;
    private static CompoundDrawer instance = new CompoundDrawer();
    private List<ICompoundDrawer> mCompoundDrawer = new ArrayList<>();
    private GenericDrawer mGenericDrawer = new GenericDrawer();

    private CompoundDrawer() {
        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPaint.setStrokeWidth(Configuration.LINE_THICKNESS);
    }

    public static CompoundDrawer instance() {
        return instance;
    }

    public void addCompoundDrawer(ICompoundDrawer newCompoundDrawer) {
        boolean unregistered = true;

        for (ICompoundDrawer componentDrawer : mCompoundDrawer) {
            if (componentDrawer.getID() == newCompoundDrawer.getID()) {
                unregistered = false;
                break;
            }
        }
        if (unregistered) {
            mCompoundDrawer.add(newCompoundDrawer);
        }
    }

    public void draw(Compound compound, Canvas canvas) {
        mPaint.setStyle(Paint.Style.STROKE);
        if (compound.isSelected()) {
            mPaint.setColor(Color.GREEN);
        } else {
            mPaint.setColor(Color.BLACK);
        }
        boolean drawn = false;
        for (ICompoundDrawer componentDrawer : mCompoundDrawer) {
            if (drawn = componentDrawer.draw(compound, canvas, mPaint)) {
                break;
            }
        }
        if (drawn == false) {
            mGenericDrawer.draw(compound, canvas, mPaint);
        }
    }

    public void drawSynthesis(CompoundReactor compoundReactor, Canvas canvas) {
        PointF synthesisSource = compoundReactor.getSynthesisSourcePoint();

        if(synthesisSource != null) {
            mPaint.setStyle(Paint.Style.FILL);
            mPaint.setColor(Color.GREEN);
            canvas.drawCircle(synthesisSource.x, synthesisSource.y, 10, mPaint);
        }
    }
}
