package com.coldradio.benzene.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.PointF;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Toast;

import com.coldradio.benzene.R;
import com.coldradio.benzene.compound.CompoundFactory;
import com.coldradio.benzene.project.Project;

public class CanvasView extends View implements View.OnTouchListener, BottomNavigationView.OnNavigationItemSelectedListener, View.OnLongClickListener {
    enum Mode {
        BROWSE, SYNTHESIS, DECOMPOSITION, CYCLE_BOND_TYPE
    }

    private Mode mMode = Mode.BROWSE;
    private PointF mClickedPoint = new PointF();

    public CanvasView(Context context) {
        super(context);
        setOnTouchListener(this);
        setLongClickable(true);
        setOnLongClickListener(this);
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
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            mClickedPoint.set(event.getX(), event.getY());
        } else if (event.getAction() == MotionEvent.ACTION_UP) {
            switch (mMode) {
                case BROWSE:
                    Project.instance().selectComponent(event.getX(), event.getY());
                    invalidate();
                    return true;
                case SYNTHESIS:
                    Project.instance().synthesis(event.getX(), event.getY());
                    invalidate();
                    return true;
                case DECOMPOSITION:
                    Project.instance().decomposition(event.getX(), event.getY());
                    Toast.makeText(getContext(), "Total " + Project.instance().compoundNumber() + " Compounds", Toast.LENGTH_SHORT).show();
                    invalidate();
                    return true;
                case CYCLE_BOND_TYPE:
                    Project.instance().cycleBondType(event.getX(), event.getY());
                    invalidate();
                    return true;
            }
        } else if (event.getAction() == MotionEvent.ACTION_MOVE) {

        }
        return false;
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.navibar_browse:
                mMode = Mode.BROWSE;
                return true;
            case R.id.navibar_cycle_bond:
                mMode = Mode.CYCLE_BOND_TYPE;
                return true;
            case R.id.navibar_synthesis:
                mMode = Mode.SYNTHESIS;
                return true;
            case R.id.navibar_decompose:
                mMode = Mode.DECOMPOSITION;
                return true;
            case R.id.navibar_add:
                // TODO: show library activity here
                return true;
        }
        return false;
    }

    @Override
    public boolean onLongClick(View v) {
        if (mMode == Mode.BROWSE) {
            Project.instance().initiateRegionSelect(mClickedPoint);
            invalidate();

            return true;
        } else {
            return false;
        }
    }
}
