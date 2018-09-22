package com.coldradio.benzene.view;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PointF;

import com.coldradio.benzene.compound.Compound;
import com.coldradio.benzene.compound.CompoundReactor;
import com.coldradio.benzene.project.Configuration;
import com.coldradio.benzene.project.SelectedCompound;

import java.util.ArrayList;
import java.util.List;

public class DrawerManager {
    private Paint mPaint;
    private List<ICompoundDrawer> mCompoundDrawer = new ArrayList<>();
    private GenericDrawer mGenericDrawer = new GenericDrawer();

    public DrawerManager() {
        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPaint.setStrokeWidth(Configuration.LINE_THICKNESS);
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

    public void draw(Compound compound, boolean selected, Canvas canvas) {
        mPaint.setStyle(Paint.Style.STROKE);
        if (selected) {
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
        if (! drawn) {
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

    public void drawSelectedCompoundAccessory(SelectedCompound selectedCompound, Canvas canvas) {
        PointF pivot = selectedCompound.getRotationPivotPoint();
        PointF center = selectedCompound.getCompound().centerOfRectangle();

        mPaint.setColor(Color.rgb(0, 0, 120));
        mPaint.setStyle(Paint.Style.FILL);
        canvas.drawCircle(pivot.x, pivot.y, Configuration.ROTATION_PIVOT_SIZE, mPaint);
        canvas.drawLine(pivot.x, pivot.y, center.x, center.y, mPaint);
    }
}
