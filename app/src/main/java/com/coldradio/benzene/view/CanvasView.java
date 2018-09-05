package com.coldradio.benzene.view;

import android.content.Context;
import android.graphics.Canvas;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Toast;

import com.coldradio.benzene.compound.Alkane;
import com.coldradio.benzene.compound.Benzene;
import com.coldradio.benzene.project.Project;

public class CanvasView extends View implements View.OnTouchListener{
    enum Mode {
        SELECT, SYNTHESIS, DECOMPOSITION
    }
    Mode mMode = Mode.DECOMPOSITION;

    public CanvasView(Context context) {
        super(context);
        setOnTouchListener(this);
        // TODO: delete this line later
        Benzene bz = new Benzene();
        bz.offset(500, 500);
        Project.instance().addCompound(bz);
        Alkane alkane = new Alkane(4);
        alkane.offset(200, 200);
        Project.instance().addCompound(alkane);
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
                    break;
                case DECOMPOSITION:
                    Project.instance().decomposition(event.getX(), event.getY());
                    Toast.makeText(getContext(), "Total " + Project.instance().compoundNumber() + " Compounds", Toast.LENGTH_SHORT).show();
                    break;
            }
            invalidate();
        }
        return true;
    }
}
