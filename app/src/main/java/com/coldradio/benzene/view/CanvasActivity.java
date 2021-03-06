package com.coldradio.benzene.view;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewGroup;

import com.coldradio.benzene.R;
import com.coldradio.benzene.compound.CompoundInspector;
import com.coldradio.benzene.project.ElementSelector;
import com.coldradio.benzene.project.FingerSelector;
import com.coldradio.benzene.project.Project;
import com.coldradio.benzene.project.ProjectFileManager;
import com.coldradio.benzene.project.RectSelector;
import com.coldradio.benzene.util.AppEnv;
import com.coldradio.benzene.util.FileUtil;
import com.coldradio.benzene.util.ImageUtil;
import com.coldradio.benzene.util.Notifier;
import com.coldradio.benzene.util.PermissionManager;
import com.coldradio.benzene.util.ScreenInfo;

public class CanvasActivity extends AppCompatActivity {
    private CanvasView mCanvasView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.canvas_main);

        Toolbar topToolbar = findViewById(R.id.canvas_top_toolbar);
        setSupportActionBar(topToolbar);
        try {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        } catch(NullPointerException npe) {
            // intentionally empty
        }
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setTitle(Project.instance().getProjectFile().getName());

        // add CanvasView to the canvas_main layout
        ViewGroup canvas_layout = findViewById(R.id.canvas_main);
        if (canvas_layout != null) {
            mCanvasView = new CanvasView(this);
            canvas_layout.addView(mCanvasView);
        }

        PermissionManager.instance().checkAndRequestPermission(this, PermissionManager.PermissionCode.WRITE_EXTERNAL_STORAGE);
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
        ElementSelector elementSelector = Project.instance().getElementSelector();

        // FliBondGuideLine will be turned on if the menu is selected
        mCanvasView.showFlipBondGuideLine(false);

        if (id == R.id.action_cut) {
            if (elementSelector.hasSelected()) {
                ProjectFileManager.instance().pushForDeletion();
                Project.instance().copySelectedCompound();
                Project.instance().deleteSelectedElement();
            } else {
                Notifier.instance().notification("Nothing Selected");
            }
        } else if (id == R.id.action_copy) {
            if (Project.instance().copySelectedCompound()) {
                Notifier.instance().notification(Project.instance().getElementCopier().numberOfCopiedCompounds() + " Compounds Copied");
            } else {
                Notifier.instance().notification("Nothing Selected");
            }
        } else if (id == R.id.action_paste) {
            if (Project.instance().getElementCopier().hasCopied()) {
                // can paste multiple compounds. And push() shall be called before paste()
                ProjectFileManager.instance().pushAllChangedHistory(Project.instance().getCompounds());
                Project.instance().pasteSelectedCompound(ScreenInfo.instance().centerPoint());
            } else {
                Notifier.instance().notification("Nothing Copied");
            }
        } else if (id == R.id.action_redo) {
            if (!ProjectFileManager.instance().redo()) {
                Notifier.instance().notification("Nothing to Redo");
            }
        } else if (id == R.id.action_undo) {
            if (!ProjectFileManager.instance().undo()) {
                Notifier.instance().notification("Nothing to Undo");
            }
        } else if (id == R.id.action_save) {
            ProjectFileManager.instance().saveWithoutPreview(Project.instance());
        } else if (id == R.id.action_shot_to_gallery) {
            ImageUtil.shotToGallery(mCanvasView, ScreenInfo.instance().region(), getApplicationContext());
        } else if (id == R.id.action_share_screenshot) {
            String imagePath = ImageUtil.shotToTemporary(mCanvasView, ScreenInfo.instance().region(), getApplicationContext());

            if (imagePath != null) {
                FileUtil.share(imagePath, this);
                // cannot delete the temporary file here. it needs to exist to be shared for some time
            }
        } else if (id == R.id.action_delete_selected) {
            ProjectFileManager.instance().pushForDeletion();
            Project.instance().deleteSelectedElement();
            Notifier.instance().notification("Total Compounds: " + String.valueOf(Project.instance().getCompounds().size()));
        } else if (id == R.id.action_add) {
            startActivity(new Intent("com.coldradio.benzene.COMPOUND_SEARCH"));
        } else if (id == R.id.action_func_group) {
            startActivity(new Intent("com.coldradio.benzene.ADD_TO_ATOM"));
        } else if (id == R.id.action_add_cyclic_to_bond) {
            startActivity(new Intent("com.coldradio.benzene.ADD_TO_BOND"));
        } else if (id == R.id.action_bond) {
            ProjectFileManager.instance().pushForChange();
            Project.instance().cycleBondType();
        } else if (id == R.id.action_synthesize) {
            // no action for push
            if (Project.instance().getElementSelector().selection() == ElementSelector.Selection.ATOM) {
                Notifier.instance().notification("Select Atom to make a bond");
                mCanvasView.synthesizing(true);
            } else {
                Notifier.instance().notification("Select Atom first");
            }
        } else if (id == R.id.action_atom_deco) {
            startActivity(new Intent("com.coldradio.benzene.ATOM_DECO"));
        } else if (id == R.id.action_saturate_h) {
            startActivity(new Intent("com.coldradio.benzene.SATURATE_H"));
        } else if (id == R.id.action_flip_h) {
            Project.instance().flipHydrogenForSelected();
        } else if (id == R.id.action_wedge_up) {
            ProjectFileManager.instance().pushForChange();
            Project.instance().bondAnnotation(true);
        } else if (id == R.id.action_wedge_down) {
            ProjectFileManager.instance().pushForChange();
            Project.instance().bondAnnotation(false);
        } else if (id == R.id.action_flip_bond) {
            // no action for push
            mCanvasView.showFlipBondGuideLine(true);
        } else if (id == R.id.action_show_h) {
            ProjectFileManager.instance().pushForChange();
            Project.instance().showHydrogenForSelectedElement(! CompoundInspector.showAnyHydrogen(elementSelector.getSelectedAsList()));
        } else if (id == android.R.id.home) {
            // Toolbar back Button
            ProjectFileManager.instance().savePreviewOnly(Project.instance(), mCanvasView);
            ret = false; // to call super.onOptionsItemSelected(item);
        } else if (id == R.id.action_select_by_rect) {
            elementSelector.setRegionSelector(new RectSelector());
        } else if (id == R.id.action_select_by_finger) {
            elementSelector.setRegionSelector(new FingerSelector());
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
        AppEnv.instance().setCanvasView(mCanvasView);
        AppEnv.instance().setCurrentActivity(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        ProjectFileManager.instance().saveWithoutPreview(Project.instance());
        AppEnv.instance().setCanvasView(null);
        AppEnv.instance().setCurrentActivity(null);
    }

    @Override
    public void onBackPressed() {
        ElementSelector elementSelector = Project.instance().getElementSelector();

        if (elementSelector.getRegionSelector() != null) {
            elementSelector.setRegionSelector(null);
            mCanvasView.updateContextMenu();
            mCanvasView.invalidate();
        } else if (elementSelector.hasSelected()) {
            elementSelector.reset();
            mCanvasView.updateContextMenu();
            mCanvasView.invalidate();
        } else {
            // here Project file is not saved yet. Only preview will be saved. The project file will be saved in onPause() that will be called later after this method
            ProjectFileManager.instance().savePreviewOnly(Project.instance(), mCanvasView);
            super.onBackPressed();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        PermissionManager.instance().onRequestPermissionsResult(requestCode, permissions, grantResults);
    }
}
