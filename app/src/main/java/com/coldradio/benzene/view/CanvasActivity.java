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
import com.coldradio.benzene.lib.ScreenInfo;
import com.coldradio.benzene.project.Project;

public class CanvasActivity extends AppCompatActivity {
    CanvasView mCanvasView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.canvas_main);
        Toolbar toolbar = findViewById(R.id.canvas_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        // add CanvasView to the canvas_main layout
        ViewGroup canvas_layout = findViewById(R.id.canvas_main);
        if(canvas_layout != null) {
            mCanvasView = new CanvasView(this, toolbar);
            canvas_layout.addView(mCanvasView);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.canvas_toolbar_menu, menu);
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
            Project.instance().removeSelectedCompound();
//        } else if (id == R.id.action_add) {
//            startActivity(new Intent("com.coldradio.benzene.COMPOUND_SEARCH"));
        } else if (id == R.id.action_copy) {
            Project.instance().copySelectedCompound();
        } else if (id == R.id.action_paste) {
            Project.instance().pasteSelectedCompound(ScreenInfo.instance().centerPoint());
        } else if (id == R.id.action_redo) {

        } else if (id == R.id.action_undo) {

        } else if (id == R.id.action_trashcan) {
            Project.instance().removeSelectedCompound();
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
}