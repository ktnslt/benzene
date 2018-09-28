package com.coldradio.benzene.view;

import android.app.Activity;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Point;
import android.graphics.PointF;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.view.GestureDetectorCompat;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;

import com.coldradio.benzene.R;
import com.coldradio.benzene.compound.CompoundFactory;
import com.coldradio.benzene.project.ContextMenuManager;
import com.coldradio.benzene.project.Project;

public class CanvasView extends View implements View.OnTouchListener, BottomNavigationView.OnNavigationItemSelectedListener, GestureDetector.OnGestureListener, GestureDetector.OnDoubleTapListener {
    enum Mode {
        BROWSE, SYNTHESIS, DECOMPOSITION, CYCLE_BOND_TYPE
    }

    private Mode mMode = Mode.BROWSE;
    private PointF mClickedPoint = new PointF();
    private GestureDetectorCompat mGestureDetector;
    private ContextMenuManager mContextMenuManager;

    private PointF actualClickedPosition(MotionEvent e) {
        return new PointF(e.getX() + getScrollX(), e.getY() + getScrollY());
    }

    public CanvasView(Context context) {
        super(context);
        setOnTouchListener(this);
        mGestureDetector = new GestureDetectorCompat(getContext(), this);
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
        PointF actualPoint = actualClickedPosition(event);

        if (Project.instance().regionSelect(actualPoint, event.getAction())) {
            invalidate();
            return true;
        } else if (Project.instance().rotateSelectedCompound(actualPoint, event.getAction())) {
            // this handler shall be the first not to feed the event to GestureDetector
            invalidate();
            return true;
        } else if (mGestureDetector.onTouchEvent(event)) {
            return true;
        } else if (event.getAction() == MotionEvent.ACTION_DOWN) {
            // for long click, the clicked position is saved
            mClickedPoint.set(event.getX(), event.getY());

            switch (mMode) {
                case BROWSE:
                    Project.instance().selectComponent(actualPoint);
                    invalidate();
                    mContextMenuManager.update();
                    return true;
                case SYNTHESIS:
                    Project.instance().synthesis(actualPoint);
                    invalidate();
                    return true;
                case DECOMPOSITION:
                    Project.instance().decomposition(actualPoint);
                    invalidate();
                    return true;
                case CYCLE_BOND_TYPE:
                    Project.instance().cycleBondType(actualPoint);
                    invalidate();
                    return true;
            }
        }
        return false;
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.navibar_browse:
                mMode = Mode.BROWSE;
                return true;
            case R.id.navibar_atom:
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
        }
        return false;
    }

    @Override
    public boolean onDown(MotionEvent e) {
        return false;
    }

    @Override
    public void onShowPress(MotionEvent e) {

    }

    @Override
    public boolean onSingleTapUp(MotionEvent e) {
        return false;
    }

    @Override
    public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
        if (Project.instance().isSelectingRegion()) {
            return false;
        } else if (Project.instance().hasSelectedCompound()) {
            Project.instance().moveSelectedCompoundBy(new PointF(-distanceX, -distanceY));
            invalidate();
        } else {
            scrollBy((int) distanceX, (int) distanceY);
        }

        return true;
    }

    @Override
    public void onLongPress(MotionEvent e) {
        if (mMode == Mode.BROWSE) {
            Project.instance().regionSelect(mClickedPoint, -1);
            invalidate();
        }
    }

    @Override
    public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
        return false;
    }

    @Override
    public boolean onSingleTapConfirmed(MotionEvent e) {
        return false;
    }

    @Override
    public boolean onDoubleTap(MotionEvent e) {
        PointF centerOfAllCompounds = Project.instance().centerOfAllCompounds();
        Point screenSize = new Point();

        ((Activity) getContext()).getWindowManager().getDefaultDisplay().getSize(screenSize);

        setScrollX((int) centerOfAllCompounds.x - screenSize.x / 2);
        // TODO: 150 shall be calculated by adding the height of top title bar + bottom navigation bar + soft navigation bar
        setScrollY((int) centerOfAllCompounds.y - (screenSize.y / 2 - 150));
        return true;
    }

    @Override
    public boolean onDoubleTapEvent(MotionEvent e) {
        return false;
    }
}