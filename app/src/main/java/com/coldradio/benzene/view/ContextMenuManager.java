package com.coldradio.benzene.view;

import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import com.coldradio.benzene.R;
import com.coldradio.benzene.project.Project;

public class ContextMenuManager {
    private Toolbar mToolbar;

    private void showItem(int id, boolean show) {
        if (mToolbar != null) {
            MenuItem item = mToolbar.getMenu().findItem(id);

            if (item != null) {
                item.setVisible(show);
            }
        }
    }

    private void compoundMenu() {
        showItem(R.id.action_paste, false);
        showItem(R.id.action_cut, true);
        showItem(R.id.action_copy, true);
        showItem(R.id.action_trashcan, true);
    }

    private void idleMenu() {
        showItem(R.id.action_paste, false);
        showItem(R.id.action_cut, false);
        showItem(R.id.action_copy, false);
        showItem(R.id.action_trashcan, false);
    }

    private void pasteMenu() {
        showItem(R.id.action_paste, true);
        showItem(R.id.action_cut, false);
        showItem(R.id.action_copy, false);
        showItem(R.id.action_trashcan, false);
    }

    public ContextMenuManager(Toolbar toolbar) {
        mToolbar = toolbar;
    }

    public void update() {
        if (Project.instance().hasSelectedCompound()) {
            compoundMenu();
        } else if (Project.instance().hasCopiedCompound()) {
            pasteMenu();
        } else {
            idleMenu();
        }
    }
}