package org.nexussoft.spstlunches;

import android.Manifest;
import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Intent;
import android.content.pm.ComponentInfo;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.RemoteViews;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener
{

    public static int INTERNET_PERMISSION_REQUEST_CODE = 0xabc;

    private TextView mLunch1View;
    private TextView mLunch2View;

    private boolean mUpdateWidget;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        if(ContextCompat.checkSelfPermission(this, Manifest.permission.INTERNET) != PackageManager.PERMISSION_GRANTED) {
            if(ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.INTERNET)) {
                //TODO: Show explanation.
            }
        } else {
            ActivityCompat.requestPermissions(this, new String[] {Manifest.permission.INTERNET}, INTERNET_PERMISSION_REQUEST_CODE);
        }

        Button tryAgainButton = (Button)findViewById(R.id.try_again_button);
        tryAgainButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mUpdateWidget = true;
                new DownloadTask().execute();
            }
        });

        mLunch1View = (TextView) findViewById(R.id.lunch_1_title);
        mLunch2View = (TextView) findViewById(R.id.lunch_2_title);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        toggle.syncState();

        new DownloadTask().execute();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        if (navigationView != null)
        {
            navigationView.setNavigationItemSelectedListener(this);
        }
    }

    @Override
    public void onBackPressed()
    {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if(drawer == null)
            return;
        if (drawer.isDrawerOpen(GravityCompat.START))
        {
            drawer.closeDrawer(GravityCompat.START);
        }
        else
        {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item)
    {
        return true;
    }

    private class DownloadTask extends AsyncTask<Void, Void, Void> {

        private String[] result;
        private boolean mSuccess;

        @SuppressWarnings("ConstantConditions")
        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);

            if(mSuccess) {
                mLunch1View.setText(result[0]);
                mLunch2View.setText(result[1]);

                if(!mUpdateWidget) return;
                RemoteViews remoteViews = new RemoteViews(getApplicationContext().getPackageName(), R.layout.basic_widget);
                ComponentName widget = new ComponentName(MainActivity.this, BasicWidgetProvider.class);

                remoteViews.setTextViewText(R.id.lunch_1_title, result[0].substring(0, 1).toUpperCase() + result[0].substring(1));
                remoteViews.setTextViewText(R.id.lunch_2_title, result[1].substring(0, 1).toUpperCase() + result[1].substring(1));

                remoteViews.setTextViewText(R.id.lunch_1_description, result[2]);
                remoteViews.setTextViewText(R.id.lunch_2_description, result[2]);

                AppWidgetManager.getInstance(MainActivity.this).updateAppWidget(widget, remoteViews);
                mUpdateWidget = false;

                Snackbar.make(findViewById(R.id.drawer_layout), R.string.success_updating, Snackbar.LENGTH_SHORT).show();
            } else {
                Snackbar.make(findViewById(R.id.drawer_layout), R.string.failure_updating, Snackbar.LENGTH_SHORT).show();
            }

        }

        @Override
        protected Void doInBackground(Void... params) {
            try {
                result = new DataProvider().getLatest();
                mSuccess = true;
            } catch (Exception e) {
                e.printStackTrace();
                mSuccess = false;
            }

            return null;
        }
    }
}
