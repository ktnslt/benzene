package com.coldradio.benzene.view;

import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import com.coldradio.benzene.R;
import com.coldradio.benzene.project.Project;

public class ContextMenuManager {
    private Toolbar mTopToolbar;
    private Toolbar mBottomToolbar;

    private void showItem(Toolbar toolbar, int id, boolean show) {
        if (toolbar != null) {
            MenuItem item = toolbar.getMenu().findItem(id);

            if (item != null) {
                item.setVisible(show);
            }
        }
    }

    private void setTopToolbar(boolean paste, boolean cut, boolean copy) {
        showItem(mTopToolbar, R.id.action_paste, paste);
        showItem(mTopToolbar, R.id.action_cut, cut);
        showItem(mTopToolbar, R.id.action_copy, copy);
    }

    private void setBottomToolbar(boolean change_atom, boolean rs, boolean star_mark, boolean bond, boolean synthesize, boolean auto_adjust, boolean trashcan) {
        showItem(mBottomToolbar, R.id.action_change_atom, change_atom);
        showItem(mBottomToolbar, R.id.action_3d, rs);
        showItem(mBottomToolbar, R.id.action_star, star_mark);
        showItem(mBottomToolbar, R.id.action_bond, bond);
        showItem(mBottomToolbar, R.id.action_synthesize, synthesize);
        showItem(mBottomToolbar, R.id.action_adjust, auto_adjust);
        showItem(mBottomToolbar, R.id.action_trashcan, trashcan);
    }

    public ContextMenuManager(Toolbar topToolbar, Toolbar bottomToolbar) {
        mTopToolbar = topToolbar;
        mBottomToolbar = bottomToolbar;
    }

    public void update() {
        // tom toolbar update
        if (Project.instance().hasSelectedCompound()) {
            setTopToolbar(false, true, true);
        } else if (Project.instance().hasCopiedCompound()) {
            setTopToolbar(true, false, false);
        } else {
            setTopToolbar(false, false, false);
        }
        // bottom toolbar update
        switch(Project.instance().getElementSelector().selection()) {
            case ATOM:
                setBottomToolbar(true, true, true, false, true, false, true);
                break;
            case EDGE:
                setBottomToolbar(false, false, false, true, false, false, true);
                break;
            case COMPOUND:
                setBottomToolbar(false, false, false, false, false, true, true);
                break;
            case NONE:
                setBottomToolbar(false, false, false, false, false, false, false);
                break;
        }
    }
}