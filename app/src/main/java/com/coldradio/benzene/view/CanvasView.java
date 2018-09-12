package com.coldradio.benzene.view;

import android.content.Context;
import android.graphics.Canvas;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Toast;

import com.coldradio.benzene.compound.ChainCompound;
import com.coldradio.benzene.compound.CompoundFactory;
import com.coldradio.benzene.compound.ConjuatedCyclicCompound;
import com.coldradio.benzene.compound.CyclicCompound;
import com.coldradio.benzene.project.Project;

public class CanvasView extends View implements View.OnTouchListener {
    enum Mode {
        SELECT, SYNTHESIS, DECOMPOSITION, CYCLE_BOND_TYPE
    }

    Mode mMode = Mode.SYNTHESIS;

    public CanvasView(Context context) {
        super(context);
        setOnTouchListener(this);
        // TODO: delete this line later
        Project.instance().addCompound(CompoundFactory.propane(100, 100));

        Project.instance().addCompound(CompoundFactory.butane(500, 100));

        Project.instance().addCompound(CompoundFactory.conjugatedCyclicAlkane(3, 100, 300));

        Project.instance().addCompound(CompoundFactory.conjugatedCyclicAlkane(4, 400, 300));

        Project.instance().addCompound(CompoundFactory.conjugatedCyclicAlkane(5, 700, 300));

        Project.instance().addCompound(CompoundFactory.conjugatedCyclicAlkane(6, 100, 600));

        Project.instance().addCompound(CompoundFactory.conjugatedCyclicAlkane(7, 400, 600));

        Project.instance().addCompound(CompoundFactory.conjugatedCyclicAlkane(8, 700, 600));

        Project.instance().addCompound(CompoundFactory.conjugatedCyclicAlkane(9, 300, 900));

        Project.instance().addCompound(CompoundFactory.conjugatedCyclicAlkane(10, 700, 900));
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        Project.instance().drawTo(canvas);
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_UP) {
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
