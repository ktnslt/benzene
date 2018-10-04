package com.coldradio.benzene.view;

import android.app.Activity;
import android.content.Context;
import android.content.res.Configuration;
import android.graphics.Canvas;
import android.graphics.Point;
import android.graphics.PointF;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.view.GestureDetectorCompat;
import android.support.v7.widget.Toolbar;
import android.view.GestureDetector;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;

import com.coldradio.benzene.R;
import com.coldradio.benzene.compound.Compound;
import com.coldradio.benzene.compound.CompoundArranger;
import com.coldradio.benzene.lib.CompoundLibrary;
import com.coldradio.benzene.lib.Helper;
import com.coldradio.benzene.lib.ScreenInfo;
import com.coldradio.benzene.project.ContextMenuManager;
import com.coldradio.benzene.project.Project;

public class CanvasView extends View implements View.OnTouchListener, BottomNavigationView.OnNavigationItemSelectedListener, GestureDetector.OnGestureListener, GestureDetector.OnDoubleTapListener {
    enum Mode {
        BROWSE, ATTACH_FUNC_GROUP, CHANGE_ATOM, CYCLE_BOND_TYPE, SYNTHESIS, DECOMPOSITION
    }

    private Mode mMode = Mode.BROWSE;
    private PointF mClickedPoint = new PointF();
    private GestureDetectorCompat mGestureDetector;
    private ContextMenuManager mContextMenuManager;

    private PointF actualClickedPosition(MotionEvent e) {
        return new PointF(e.getX() + getScrollX(), e.getY() + getScrollY());
    }

    public CanvasView(Context context, Toolbar toolbar) {
        super(context);
        setOnTouchListener(this);
        mGestureDetector = new GestureDetectorCompat(getContext(), this);
        CompoundLibrary.instance().parseLibrary(this.getResources());
        Helper.instance().setContext(this.getContext());
        mContextMenuManager = new ContextMenuManager(toolbar);
        // TODO: delete this line later
        Compound c = CompoundLibrary.instance().getCompoundIndexByCID(18937).compound.copy();
        CompoundArranger.zoomToStandard(c, 1);
        CompoundArranger.alignCenter(c, ScreenInfo.instance().centerPoint());
        Project.instance().addCompound(c);
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
            case R.id.navibar_function:
                mMode = Mode.ATTACH_FUNC_GROUP;
                return true;
            case R.id.navibar_change_atom:
                mMode = Mode.CHANGE_ATOM;
                return true;
            case R.id.navibar_cycle_bond:
                mMode = Mode.CYCLE_BOND_TYPE;
                return true;
            case R.id.navibar_synthesis_decomposition:
                if (mMode == Mode.SYNTHESIS) {
                    mMode = Mode.DECOMPOSITION;
                    item.setIcon(R.drawable.ic_menu_decomposition);
                    item.setTitle("Decomposition");
                } else {
                    mMode = Mode.SYNTHESIS;
                    item.setIcon(R.drawable.ic_menu_synthesis);
                    item.setTitle("Synthesis");
                }
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
        ScreenInfo.instance().setLeftTopPoint(getX(), getY());

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

        setScrollX((int) centerOfAllCompounds.x - ScreenInfo.instance().screenWidth() / 2);
        // TODO: 150 shall be calculated by adding the height of top title bar + bottom navigation bar + soft navigation bar
        setScrollY((int) centerOfAllCompounds.y - (ScreenInfo.instance().screenHeight() / 2 - 150));
        return true;
    }

    @Override
    public boolean onDoubleTapEvent(MotionEvent e) {
        return false;
    }

    @Override
    protected void onConfigurationChanged(Configuration newConfig) {
        Point screenSize = new Point();

        ((Activity) getContext()).getWindowManager().getDefaultDisplay().getSize(screenSize);
        ScreenInfo.instance().setScreenSize(screenSize.x, screenSize.y);
        super.onConfigurationChanged(newConfig);
    }
}