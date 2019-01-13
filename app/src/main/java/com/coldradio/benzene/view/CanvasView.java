package com.coldradio.benzene.view;

import android.app.Activity;
import android.graphics.Canvas;
import android.graphics.PointF;
import android.support.v4.view.GestureDetectorCompat;
import android.support.v7.widget.Toolbar;
import android.view.GestureDetector;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;

import com.coldradio.benzene.R;
import com.coldradio.benzene.compound.CompoundArranger;
import com.coldradio.benzene.project.ElementSelector;
import com.coldradio.benzene.project.Project;
import com.coldradio.benzene.project.ProjectFileManager;
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
    private boolean mElementMoved;
    private SelectedElementAccessoryDrawer mSelectedElementAccessoryDrawer;
    private boolean mSynthesizing;
    private boolean mFirstDraw = true;
    private Toolbar mTopToolbar;

    private void calcActualClickedPosition(MotionEvent e) {
        mActualClickedPosition.set(e.getX() + getScrollX(), e.getY() + getScrollY());
    }

    private void toCenter() {
        ScreenInfo screen = ScreenInfo.instance();

        if (screen.screenWidth() != 0 && screen.screenHeight() != 0) {
            PointF centerOfAllCompounds = CompoundArranger.center(Project.instance().getCompounds());

            setScrollX((int) centerOfAllCompounds.x - screen.screenWidth() / 2);
            setScrollY((int) centerOfAllCompounds.y - screen.screenHeight() / 2);
        }
    }

    public CanvasView(Activity activity) {
        super(activity);

        // cautious - here getWidth() and getHeight() returns 0
        // set SelectedElementAccessoryDrawer
        mSelectedElementAccessoryDrawer = new SelectedElementAccessoryDrawer(getResources().getDrawable(R.drawable.ic_menu_flip_bond));

        mGestureDetector = new GestureDetectorCompat(getContext(), this);
        mTopToolbar = activity.findViewById(R.id.canvas_top_toolbar);
        mContextMenuManager = new ContextMenuManager(mTopToolbar, (Toolbar) activity.findViewById(R.id.canvas_bottom_toolbar));

        // register drawer
        mDrawerManager.addPreCompoundDrawer(new SelectedRegionDrawer());
        mDrawerManager.addPreCompoundDrawer(new SelectedElementBackgroundDrawer());
        mDrawerManager.addPostCompoundDrawer(new AtomDecorationDrawer());
        mDrawerManager.addPostCompoundDrawer(mSelectedElementAccessoryDrawer);

        ProjectFileManager.instance().clearHistory();

        // register listener
        ProjectFileManager.instance().addListener(new ProjectFileManager.OnChangeListener() {
            @Override
            public void saved() {
                MenuItem item = mTopToolbar.getMenu().findItem(R.id.action_save);
                if (item != null)
                    item.setVisible(false);
            }

            @Override
            public void changed() {
                MenuItem item = mTopToolbar.getMenu().findItem(R.id.action_save);
                if (item != null)
                    item.setVisible(true);
            }
        });
    }

    public void updateContextMenu() {
        mContextMenuManager.update();
    }

    public void showFlipBondGuideLine(boolean show) {
        mSelectedElementAccessoryDrawer.showFlipBondGuideLine(show);
    }

    public void synthesizing(boolean syn) {
        mSynthesizing = syn;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        ScreenInfo.instance().setScreen(getScrollX(), getScrollY(), getWidth(), getHeight());

        if (mFirstDraw) {
            toCenter();
            mFirstDraw = false;
        }
        //canvas.drawCircle(ScreenInfo.instance().centerPoint().x, ScreenInfo.instance().centerPoint().y, 10, PaintSet.instance().paint(PaintSet.PaintType.GUIDE_LINE));
        mDrawerManager.draw(canvas);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        ElementSelector elementSelector = Project.instance().getElementSelector();

        calcActualClickedPosition(event);

        if (Project.instance().rotateSelectedCompound(mActualClickedPosition, event.getAction())) {
            // this handler shall be the first not to feed the event to GestureDetector
            invalidate();
        } else if (elementSelector.onTouchEvent(mActualClickedPosition, event.getAction())) {
            invalidate();
        } else if (!mGestureDetector.onTouchEvent(event)) {
            int maskedAction = event.getActionMasked();

            if (elementSelector.selectionCancelled()) {
                updateContextMenu();
            }

            switch (maskedAction) {
                // the overall sequence is ACTION_DOWN (first finger) -> ACTION_POINTER_DOWN (second finger)
                // if makes ACTION_DOWN cannot replace below CASE, causing de-select whatever selected when try to move the selected element.
                case MotionEvent.ACTION_UP:
                    if (!mScrolledAfterSelected) {
                        if (mSynthesizing) {
                            // the pushAllChangedHistory for synthesize is handled in synthesize()
                            if (Project.instance().synthesize(mActualClickedPosition)) {
                                invalidate();
                            }
                        } else {
                            if (!mSelectedElementAccessoryDrawer.flipBond(mActualClickedPosition)) {
                                Project.instance().select(mActualClickedPosition);
                                updateContextMenu();
                            }
                            invalidate(); // invalidate also for the de-select case
                        }
                        mSynthesizing = false;
                    }
                    mElementMoved = false;
                    mScrolledAfterSelected = false;
                    break;
                case MotionEvent.ACTION_POINTER_DOWN:
                    mMoveSelectedElement = true;
                    break;
                case MotionEvent.ACTION_POINTER_UP:
                    mMoveSelectedElement = false;
                    break;
            }
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

        if (mMoveSelectedElement && Project.instance().getElementSelector().hasSelected()) {
            if (! mElementMoved) {
                // this is first movement
                ProjectFileManager.instance().pushForMove();
            }
            Project.instance().getElementSelector().moveSelectedElement(pmScrollDistance);
            mElementMoved = true;
            invalidate();
        } else {
            scrollBy((int) distanceX, (int) distanceY);
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
        if (!Project.instance().getElementSelector().canSelectAny(mActualClickedPosition)) {
            toCenter();
        }

        return true;
    }

    @Override
    public boolean onDoubleTapEvent(MotionEvent e) {
        return false;
    }
}