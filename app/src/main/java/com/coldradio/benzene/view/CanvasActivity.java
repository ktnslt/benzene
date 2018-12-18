package com.coldradio.benzene.view;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewGroup;

import com.coldradio.benzene.R;
import com.coldradio.benzene.project.ProjectFileManager;
import com.coldradio.benzene.util.Notifier;
import com.coldradio.benzene.util.ScreenInfo;
import com.coldradio.benzene.project.Project;

public class CanvasActivity extends AppCompatActivity {
    private CanvasView mCanvasView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.canvas_main);

        Toolbar topToolbar = findViewById(R.id.canvas_top_toolbar);
        setSupportActionBar(topToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        // add CanvasView to the canvas_main layout
        ViewGroup canvas_layout = findViewById(R.id.canvas_main);
        if (canvas_layout != null) {
            mCanvasView = new CanvasView(this, topToolbar, (Toolbar)findViewById(R.id.canvas_bottom_toolbar));
            canvas_layout.addView(mCanvasView);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.canvas_toolbar_menu, menu);

        Toolbar bottomBar = findViewById(R.id.canvas_bottom_toolbar);
        Menu bottomMenu = bottomBar.getMenu();
        getMenuInflater().inflate(R.menu.canvas_bottom_toolbar_menu, bottomMenu);

        for (int ii = 0; ii < bottomMenu.size(); ++ii) {
            bottomMenu.getItem(ii).setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
                @Override
                public boolean onMenuItemClick(MenuItem item) {
                    return onOptionsItemSelected(item);
                }
            });
        }
        return true;
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        boolean ret = true;

        if (id == R.id.action_cut) {
            Project.instance().copySelectedCompound();
            Project.instance().deleteSelectedElement();
        } else if (id == R.id.action_copy) {
            Project.instance().copySelectedCompound();
            Notifier.instance().notification("Compound Copied");
        } else if (id == R.id.action_paste) {
            Project.instance().pasteSelectedCompound(ScreenInfo.instance().centerPoint());
        } else if (id == R.id.action_redo) {

        } else if (id == R.id.action_undo) {

        } else if (id == R.id.action_trashcan) {
            Project.instance().deleteSelectedElement();
        } else if (id == R.id.action_add) {
            startActivity(new Intent("com.coldradio.benzene.COMPOUND_SEARCH"));
        } else if (id == R.id.action_func_group) {
            startActivityForResult(new Intent("com.coldradio.benzene.ADD_TO_ATOM"), ActivityRequestCode.ADD_TO_ATOM_REQ.ordinal());
        } else if (id == R.id.action_add_cyclic_to_bond) {
            startActivityForResult(new Intent("com.coldradio.benzene.ADD_TO_BOND"), ActivityRequestCode.ADD_TO_BOND_REQ.ordinal());
        } else if (id == R.id.action_change_atom) {
            startActivityForResult(new Intent("com.coldradio.benzene.CHANGE_ATOM"), ActivityRequestCode.CHANGE_ATOM_REQ.ordinal());
        } else if (id == R.id.action_bond) {
            Project.instance().cycleBondType();
        } else if (id == R.id.action_synthesize) {

        } else if (id == R.id.action_atom_deco) {
            startActivityForResult(new Intent("com.coldradio.benzene.ATOM_DECO"), ActivityRequestCode.ATOM_DECO_REQ.ordinal());
        } else if (id == R.id.action_show_h) {
            Project.instance().toggleShowHydrogen();
        } else if (id == R.id.action_lettering) {
            Project.instance().toggleLettering();
        } else {
            ret = false;
        }

        if (mCanvasView != null && ret) {
            mCanvasView.updateContextMenu();
            mCanvasView.invalidate();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onResume() {
        super.onResume();
        // after adding compound, the context menu needs to be updated
        mCanvasView.updateContextMenu();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == ActivityRequestCode.CHANGE_ATOM_REQ.ordinal()) {
            if (resultCode == RESULT_OK) {
                String atomName = data.getStringExtra("AtomName");

                if (atomName != null && atomName.length() >= 1) {
                    Project.instance().changeSelectedAtom(atomName);
                    mCanvasView.invalidate();
                }
            }
        } else if (requestCode == ActivityRequestCode.ADD_TO_BOND_REQ.ordinal()) {
            if (resultCode == RESULT_OK) {
                Project.instance().addCyclicToSelectedBond(data.getIntExtra("EdgeNumber", 6), data.getBooleanExtra("OppositeSite", false));
                mCanvasView.invalidate();
            }
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        ProjectFileManager.instance().save(Project.instance(), mCanvasView);
    }
}
