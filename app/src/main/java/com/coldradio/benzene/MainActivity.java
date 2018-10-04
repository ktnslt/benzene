package com.coldradio.benzene;

import android.content.Intent;
import android.graphics.Point;
import android.os.Bundle;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewGroup;

import com.coldradio.benzene.lib.ScreenInfo;
import com.coldradio.benzene.project.Project;
import com.coldradio.benzene.view.CanvasView;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private CanvasView canvasView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        // set screen size
        Point screenSize = new Point();

        getWindowManager().getDefaultDisplay().getSize(screenSize);
        ScreenInfo.instance().setScreenSize(screenSize.x, screenSize.y);

        // add CanvasView to the canvas_main layout
        ViewGroup canvas_layout = findViewById(R.id.canvas_main);
        if(canvas_layout != null) {
            canvasView = new CanvasView(this, toolbar);
            canvas_layout.addView(canvasView);
            // attach the navibar listener
            BottomNavigationView navigation = findViewById(R.id.bottom_navibar);

            if (navigation != null) {
                navigation.setOnNavigationItemSelectedListener(canvasView);
            }
        }

        // initialize AutoCompleteTextView
//        final String[] predefined_compounds = {
//                "Propane", "ConjuatedCyclicCompound", "Toluene"
//        };
//        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, predefined_compounds);
//        AutoCompleteTextView textView = findViewById(R.id.compound_name);
//        textView.setThreshold(0);
//        textView.setAdapter(adapter);
//        textView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//            @Override
//            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//                List predefined_compound_list = Arrays.asList(predefined_compounds);
//                if(id == predefined_compound_list.indexOf("ConjuatedCyclicCompound")) {
//                    Project.instance().addCompound(new ConjuatedCyclicCompound());
//                }
//            }
//        });
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        boolean ret = true;

        if (id == R.id.action_cut) {
            Project.instance().copySelectedCompound();
            Project.instance().removeSelectedCompound();
        } else if (id == R.id.action_add) {
            startActivity(new Intent("com.coldradio.benzene.COMPOUND_SEARCH"));
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

        if (canvasView != null && ret) {
            canvasView.invalidate();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.my_compounds) {
            // Handle the camera action
        } else if (id == R.id.start_new) {

        } else if (id == R.id.library) {

        }

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
