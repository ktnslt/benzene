package com.coldradio.benzene;

import android.content.Intent;
import android.graphics.Point;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.coldradio.benzene.util.ScreenInfo;
import com.coldradio.benzene.project.Project;
import com.coldradio.benzene.project.ProjectFileManager;
import com.coldradio.benzene.view.ProjectView;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.project_toolbar);
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

        // add ProjectView
        ViewGroup project_layout = findViewById(R.id.project_main);
        if (project_layout != null) {
            ProjectView projectView = new ProjectView(this, (RecyclerView)findViewById(R.id.project_recycler_view));

            project_layout.addView(projectView);
        }

        // add FloatingActionBar
        FloatingActionButton fab = findViewById(R.id.fab_add);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Project.instance().createNew(ProjectFileManager.instance().createNew());
                startActivity(new Intent("com.coldradio.benzene.CANVAS"));
            }
        });
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
        getMenuInflater().inflate(R.menu.project_toolbar_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_search) {

        } else if (id == R.id.action_sort) {

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
