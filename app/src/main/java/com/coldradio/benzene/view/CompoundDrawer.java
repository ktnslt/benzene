package com.coldradio.benzene.view;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;

import com.coldradio.benzene.compound.Compound;
import com.coldradio.benzene.project.Configuration;

import java.util.ArrayList;
import java.util.List;

public class CompoundDrawer {
    interface IComponentDrawer {
        boolean draw(Compound compound, Canvas canvas, Paint paint);
        String getID();
    }
    private Paint mPaint;
    private static CompoundDrawer instance = new CompoundDrawer();
    private List<IComponentDrawer> mComponentDrawer = new ArrayList<>();
    private GenericDrawer mGenericDrawer = new GenericDrawer();

    private CompoundDrawer() {
        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPaint.setStrokeWidth(Configuration.LINE_THICKNESS);
    }
    public static CompoundDrawer instance() {
        return instance;
    }
    public void addComponentDrawer(IComponentDrawer newComponentDrawer) {
        boolean unregistered = true;

        for(IComponentDrawer componentDrawer : mComponentDrawer) {
            if(componentDrawer.getID() == newComponentDrawer.getID()) {
                unregistered = false;
                break;
            }
        }
        if(unregistered) {
            mComponentDrawer.add(newComponentDrawer);
        }
    }
    public void draw(Compound compound, Canvas canvas) {
        mPaint.setStyle(Paint.Style.STROKE);
        if(compound.isSelected()) {
            mPaint.setColor(Color.GREEN);
        }
        else {
            mPaint.setColor(Color.BLACK);
        }
        boolean drawn = false;
        for(IComponentDrawer componentDrawer : mComponentDrawer) {
            if(drawn = componentDrawer.draw(compound, canvas, mPaint)) {
                break;
            }
        }
        if(drawn == false) {
            mGenericDrawer.draw(compound, canvas, mPaint);
        }
    }
}
