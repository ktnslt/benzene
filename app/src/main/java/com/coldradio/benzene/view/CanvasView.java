package com.coldradio.benzene.view;

import android.content.Context;
import android.graphics.Canvas;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Toast;

import com.coldradio.benzene.compound.ChainCompound;
import com.coldradio.benzene.compound.ConjuatedCyclicCompound;
import com.coldradio.benzene.compound.CyclicCompound;
import com.coldradio.benzene.project.Project;

public class CanvasView extends View implements View.OnTouchListener{
    enum Mode {
        SELECT, SYNTHESIS, DECOMPOSITION, CYCLE_BOND_TYPE
    }
    Mode mMode = Mode.SYNTHESIS;

    public CanvasView(Context context) {
        super(context);
        setOnTouchListener(this);
        // TODO: delete this line later
        ChainCompound chainCompound = new ChainCompound(4);
        chainCompound.offset(200, 100);
        Project.instance().addCompound(chainCompound);

        CyclicCompound hexane = new ConjuatedCyclicCompound(6);
        hexane.offset(100, 100);
        Project.instance().addCompound(hexane);

        CyclicCompound triangle = new ConjuatedCyclicCompound(3);
        triangle.offset(400, 100);
        Project.instance().addCompound(triangle);

        CyclicCompound rect = new ConjuatedCyclicCompound(4);
        rect.offset(700, 100);
        Project.instance().addCompound(rect);

        ConjuatedCyclicCompound bz = new ConjuatedCyclicCompound(6);
        bz.offset(100, 400);
        Project.instance().addCompound(bz);

        CyclicCompound heptane = new ConjuatedCyclicCompound(7);
        heptane.offset(400, 400);
        Project.instance().addCompound(heptane);

        CyclicCompound octane = new ConjuatedCyclicCompound(8);
        octane.offset(700, 400);
        Project.instance().addCompound(octane);

        CyclicCompound pentane = new ConjuatedCyclicCompound(5);
        pentane.offset(100, 700);
        Project.instance().addCompound(pentane);

        CyclicCompound nonane = new ConjuatedCyclicCompound(9);
        nonane.offset(400, 700);
        Project.instance().addCompound(nonane);

        CyclicCompound decane = new ConjuatedCyclicCompound(10);
        decane.offset(700, 700);
        Project.instance().addCompound(decane);
    }
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        Project.instance().drawTo(canvas);
    }
    @Override
    public boolean onTouch(View v, MotionEvent event) {
        if(event.getAction() == MotionEvent.ACTION_UP) {
            switch (mMode) {
                case SELECT:
                    Project.instance().selectComponent(event.getX(), event.getY());
                    break;
                case SYNTHESIS:
                    Project.instance().synthesis(event.getX(), event.getY());
                    break;
                case DECOMPOSITION:
                    Project.instance().decomposition(event.getX(), event.getY());
                    Toast.makeText(getContext(), "Total " + Project.instance().compoundNumber() + " Compounds", Toast.LENGTH_SHORT).show();
                    break;
                case CYCLE_BOND_TYPE:
                    Project.instance().cycleBondType(event.getX(), event.getY());
                    break;
            }
            invalidate();
        }
        return true;
    }
}
