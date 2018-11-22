package com.coldradio.benzene.view;

import android.app.Activity;
import android.content.Context;
import android.content.res.Configuration;
import android.graphics.Canvas;
import android.graphics.Point;
import android.graphics.PointF;
import android.support.v4.view.GestureDetectorCompat;
import android.support.v7.widget.Toolbar;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;

import com.coldradio.benzene.project.Project;
import com.coldradio.benzene.util.ScreenInfo;
import com.coldradio.benzene.view.drawer.AtomDecorationDrawer;
import com.coldradio.benzene.view.drawer.DrawerManager;
import com.coldradio.benzene.view.drawer.SelectedElementAccessoryDrawer;
import com.coldradio.benzene.view.drawer.SelectedElementBackgroundDrawer;
import com.coldradio.benzene.view.drawer.SelectedRegionDrawer;

public class CanvasView extends View implements View.OnTouchListener, GestureDetector.OnGestureListener, GestureDetector.OnDoubleTapListener {
    private PointF mClickedPoint = new PointF();
    private GestureDetectorCompat mGestureDetector;
    private ContextMenuManager mContextMenuManager;
    private DrawerManager mDrawerManager = new DrawerManager();
    private boolean mMovedAfterActionDown;

    private PointF actualClickedPosition(MotionEvent e) {
        return new PointF(e.getX() + getScrollX(), e.getY() + getScrollY());
    }

    public CanvasView(Context context, Toolbar topToolbar, Toolbar bottomToolbar) {
        super(context);
        setOnTouchListener(this);
        mGestureDetector = new GestureDetectorCompat(getContext(), this);
        mContextMenuManager = new ContextMenuManager(topToolbar, bottomToolbar);

        // register drawer
        mDrawerManager.addPreCompoundDrawer(new SelectedRegionDrawer());
        mDrawerManager.addPreCompoundDrawer(new SelectedElementBackgroundDrawer());
        mDrawerManager.addPostCompoundDrawer(new SelectedElementAccessoryDrawer());
        mDrawerManager.addPostCompoundDrawer(new AtomDecorationDrawer());
    }

    public void updateContextMenu() {
        mContextMenuManager.update();
    }

    public void toCenter() {
        PointF centerOfAllCompounds = Project.instance().centerOfAllCompounds();

        setScrollX((int) centerOfAllCompounds.x - ScreenInfo.instance().screenWidth() / 2);
        // TODO: 150 shall be calculated by adding the height of top title bar + bottom navigation bar + soft navigation bar
        setScrollY((int) centerOfAllCompounds.y - (ScreenInfo.instance().screenHeight() / 2 - 150));
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        mDrawerManager.draw(canvas);
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
            mClickedPoint.set(event.getX(), event.getY());
            mMovedAfterActionDown = false;
            return true;
        } else if (event.getAction() == MotionEvent.ACTION_UP && !mMovedAfterActionDown) {
            // this event happens only if the ACTION_DOWN returns true
            Project.instance().select(actualPoint);
            updateContextMenu();
            invalidate();
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
        mMovedAfterActionDown = true;

        if (Project.instance().isSelectingRegion()) {
            return false;
        } else if (Project.instance().hasSelectedElement()) {
            Project.instance().moveSelectedElement(new PointF(-distanceX, -distanceY));
            invalidate();
        } else {
            scrollBy((int) distanceX, (int) distanceY);
            ScreenInfo.instance().offset(distanceX, distanceY);
        }

        return true;
    }

    @Override
    public void onLongPress(MotionEvent e) {

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
        if (!Project.instance().tryToSelect(actualClickedPosition(e))) {
            toCenter();
        }

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