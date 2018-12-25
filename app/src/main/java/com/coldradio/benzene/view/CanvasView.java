package com.coldradio.benzene.view;

import android.app.Activity;
import android.graphics.Canvas;
import android.graphics.PointF;
import android.support.v4.view.GestureDetectorCompat;
import android.support.v7.widget.Toolbar;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;

import com.coldradio.benzene.R;
import com.coldradio.benzene.project.Project;
import com.coldradio.benzene.util.ScreenInfo;
import com.coldradio.benzene.view.drawer.AtomDecorationDrawer;
import com.coldradio.benzene.view.drawer.DrawerManager;
import com.coldradio.benzene.view.drawer.SelectedElementAccessoryDrawer;
import com.coldradio.benzene.view.drawer.SelectedElementBackgroundDrawer;
import com.coldradio.benzene.view.drawer.SelectedRegionDrawer;

public class CanvasView extends View implements GestureDetector.OnGestureListener, GestureDetector.OnDoubleTapListener {
    private GestureDetectorCompat mGestureDetector;
    private ContextMenuManager mContextMenuManager;
    private DrawerManager mDrawerManager = new DrawerManager();
    private boolean mMoveSelectedElement;
    private PointF mActualClickedPosition = new PointF();
    private boolean mScrolledAfterSelected;
    private SelectedElementAccessoryDrawer mSelectedElementAccessoryDrawer;

    private void calcActualClickedPosition(MotionEvent e) {
        mActualClickedPosition.set(e.getX() + getScrollX(), e.getY() + getScrollY());
    }

    public CanvasView(Activity activity) {
        super(activity);

        // cautious - here getWidth() and getHeight() returns 0
        // set SelectedElementAccessoryDrawer
        mSelectedElementAccessoryDrawer = new SelectedElementAccessoryDrawer(getResources().getDrawable(R.drawable.ic_menu_flip_bond));

        mGestureDetector = new GestureDetectorCompat(getContext(), this);
        mContextMenuManager = new ContextMenuManager((Toolbar)activity.findViewById(R.id.canvas_top_toolbar),
                (Toolbar)activity.findViewById(R.id.canvas_bottom_toolbar));

        // register drawer
        mDrawerManager.addPreCompoundDrawer(new SelectedRegionDrawer());
        mDrawerManager.addPreCompoundDrawer(new SelectedElementBackgroundDrawer());
        mDrawerManager.addPostCompoundDrawer(new AtomDecorationDrawer());
        mDrawerManager.addPostCompoundDrawer(mSelectedElementAccessoryDrawer);

        // When View is created, the default x, y are 0, hence reset Screen's x and y
        ScreenInfo.instance().setScreenXY((int) getX(), (int) getY());
    }

    public void updateContextMenu() {
        mContextMenuManager.update();
    }

    public void showFlipBondGuideLine(boolean show) {
        mSelectedElementAccessoryDrawer.showFlipBondGuideLine(show);
    }

    public void toCenter() {
        // The project is offset to zero. this might be helpful to keep Atoms among zero points.
        // In case that Atom's position is too large, the precision might be problems.
        if (ScreenInfo.instance().screenWidth() * ScreenInfo.instance().screenHeight() != 0) {
            Project.instance().offsetTo(0, 0);

            PointF centerOfAllCompounds = Project.instance().centerOfAllCompounds();

            setScrollX((int) centerOfAllCompounds.x - ScreenInfo.instance().screenWidth() / 2);
            setScrollY((int) centerOfAllCompounds.y - ScreenInfo.instance().screenHeight() / 2);
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        ScreenInfo.instance().setScreenSize(getWidth(), getHeight());
        mDrawerManager.draw(canvas);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        calcActualClickedPosition(event);

        if (Project.instance().rotateSelectedCompound(mActualClickedPosition, event.getAction())) {
            // this handler shall be the first not to feed the event to GestureDetector
            invalidate();
        } else {
            int maskedAction = event.getActionMasked();

            switch (maskedAction) {
                case MotionEvent.ACTION_UP:
                    if (!mScrolledAfterSelected) {
                        if (! mSelectedElementAccessoryDrawer.flipBond(mActualClickedPosition)) {
                            Project.instance().select(mActualClickedPosition);
                            updateContextMenu();
                        }
                        invalidate();
                    }
                    mScrolledAfterSelected = false;
                    break;
                case MotionEvent.ACTION_POINTER_DOWN:
                    mMoveSelectedElement = true;
                    break;
                case MotionEvent.ACTION_POINTER_UP:
                    mMoveSelectedElement = false;
                    break;
            }
            mGestureDetector.onTouchEvent(event);
        }

        return true;
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

    private PointF pmScrollDistance = new PointF();

    @Override
    public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
        pmScrollDistance.set(-distanceX, -distanceY);
        mScrolledAfterSelected = true;

        if (mMoveSelectedElement && Project.instance().hasSelectedElement()) {
            Project.instance().moveSelectedElement(pmScrollDistance);
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
        if (!Project.instance().tryToSelect(mActualClickedPosition)) {
            toCenter();
        }

        return true;
    }

    @Override
    public boolean onDoubleTapEvent(MotionEvent e) {
        return false;
    }
}