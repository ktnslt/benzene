package com.coldradio.benzene.view;

import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import com.coldradio.benzene.R;
import com.coldradio.benzene.project.ElementSelector;
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

    private ContextMenuManager show(int id) {
        showItem(mBottomToolbar, id, true);
        return this;
    }

    private ContextMenuManager hideAll() {
        showItem(mBottomToolbar, R.id.action_atom_deco, false);
        showItem(mBottomToolbar, R.id.action_bond, false);
        showItem(mBottomToolbar, R.id.action_synthesize, false);
        showItem(mBottomToolbar, R.id.action_delete_selected, false);
        showItem(mBottomToolbar, R.id.action_select_by_rect, false);
        showItem(mBottomToolbar, R.id.action_select_by_finger, false);
        showItem(mBottomToolbar, R.id.action_coloring, false);
        showItem(mBottomToolbar, R.id.action_add, false);
        showItem(mBottomToolbar, R.id.action_func_group, false);
        showItem(mBottomToolbar, R.id.action_add_cyclic_to_bond, false);
        showItem(mBottomToolbar, R.id.action_wedge_up, false);
        showItem(mBottomToolbar, R.id.action_wedge_down, false);
        showItem(mBottomToolbar, R.id.action_flip_bond, false);
        showItem(mBottomToolbar, R.id.action_saturate_h, false);
        showItem(mBottomToolbar, R.id.action_show_h, false);
        return this;
    }

    private void setTopToolbar(boolean paste, boolean cut, boolean copy) {
        showItem(mTopToolbar, R.id.action_paste, paste);
        showItem(mTopToolbar, R.id.action_cut, cut);
        showItem(mTopToolbar, R.id.action_copy, copy);
    }

    public ContextMenuManager(Toolbar topToolbar, Toolbar bottomToolbar) {
        mTopToolbar = topToolbar;
        mBottomToolbar = bottomToolbar;
    }

    public void update() {
        // tom toolbar update
        if (Project.instance().getElementSelector().selection() != ElementSelector.Selection.NONE) {
            setTopToolbar(false, true, true);
        } else if (Project.instance().hasCopiedCompound()) {
            setTopToolbar(true, false, false);
        } else {
            setTopToolbar(false, false, false);
        }
        // bottom toolbar update
        switch(Project.instance().getElementSelector().selection()) {
            case ATOM:
                hideAll().
                        show(R.id.action_atom_deco).
                        show(R.id.action_synthesize).
                        show(R.id.action_func_group).
                        show(R.id.action_saturate_h).
                        show(R.id.action_delete_selected);
                break;
            case EDGE:
                hideAll().
                        show(R.id.action_bond).
                        show(R.id.action_add_cyclic_to_bond).
                        show(R.id.action_wedge_up).
                        show(R.id.action_wedge_down).
                        show(R.id.action_flip_bond).
                        show(R.id.action_delete_selected);
                break;
            case COMPOUND:
                hideAll().
                        show(R.id.action_coloring).
                        show(R.id.action_delete_selected).
                        show(R.id.action_saturate_h).
                        show(R.id.action_show_h);
                break;
            case NONE:
                hideAll().
                        show(R.id.action_select_by_rect).
                        show(R.id.action_select_by_finger).
                        show(R.id.action_coloring).
                        show(R.id.action_add);
                break;
        }
    }
}