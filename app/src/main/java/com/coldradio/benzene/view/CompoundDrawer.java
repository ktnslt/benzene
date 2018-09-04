package com.coldradio.benzene.view;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;

import com.coldradio.benzene.compound.Atom;
import com.coldradio.benzene.compound.Compound;
import com.coldradio.benzene.project.Configuration;

import java.util.List;

public class CompoundDrawer {
    private Paint mPaint;
    private static CompoundDrawer instance = new CompoundDrawer();

    private CompoundDrawer() {
        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPaint.setStrokeWidth(Configuration.LINE_THICKNESS);
    }
    public static CompoundDrawer instance() {
        return instance;
    }
    public void draw(Compound compound, Canvas canvas) {
        if(compound.isSelected()) {
            mPaint.setColor(Color.GREEN);
        }
        else {
            mPaint.setColor(Color.BLACK);
        }

        List<Atom> atoms = compound.getAtoms();

        for(int ii = 0; ii < atoms.size() - 1; ++ii) {
            canvas.drawLine(atoms.get(ii).getPoint().x, atoms.get(ii).getPoint().y, atoms.get(ii + 1).getPoint().x, atoms.get(ii+1).getPoint().y, mPaint);
        }
        if(compound.isCyclo()) {
            canvas.drawLine(atoms.get(atoms.size()-1).getPoint().x, atoms.get(atoms.size()-1).getPoint().y, atoms.get(0).getPoint().x, atoms.get(0).getPoint().y, mPaint);
        }
    }
}
