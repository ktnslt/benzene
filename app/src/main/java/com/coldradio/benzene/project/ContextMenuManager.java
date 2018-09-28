package com.coldradio.benzene.project;

import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import com.coldradio.benzene.R;

public class ContextMenuManager {
    private Toolbar mToolbar;

    private void showItem(int id, boolean show) {
        MenuItem item = mToolbar.getMenu().findItem(id);

        if (item != null) {
            item.setVisible(show);
        }
    }

    private void compoundMenu() {
        showItem(R.id.action_paste, true);
        showItem(R.id.action_cut, true);
        showItem(R.id.action_copy, true);
    }

    private void idleMenu() {
        showItem(R.id.action_paste, false);
        showItem(R.id.action_cut, false);
        showItem(R.id.action_copy, false);
    }

    public ContextMenuManager(Toolbar toolbar) {
        mToolbar = toolbar;
    }

    public void update() {
        if (Project.instance().hasSelectedCompound()) {
            compoundMenu();
        } else {
            idleMenu();
        }
    }
}