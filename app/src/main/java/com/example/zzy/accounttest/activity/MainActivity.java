package com.example.zzy.accounttest.activity;

import android.app.Notification;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.ActionBar;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.example.zzy.accounttest.R;
import com.example.zzy.accounttest.fragment.HomeFragment;

import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends AppCompatActivity {

    public View content;
    private boolean isInit;
    private DrawerLayout mDrawerLayout;
    private NavigationView mNavigationView;
    private static Boolean isExit = false;
    private Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

    }

    @Override
    protected void onResume() {
        super.onResume();
        if (isInit) return;
        initView();
        isInit = true;
    }


    private void initView() {
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        content = findViewById(R.id.frame_content);
        setSupportActionBar(toolbar);
        ActionBar ab = getSupportActionBar();
        ab.setDisplayHomeAsUpEnabled(true);

        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, mDrawerLayout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        mDrawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        mNavigationView = (NavigationView) findViewById(R.id.nav_view);
        setupDrawerContent(mNavigationView);//设置菜单可选择，并且选择后关闭
//        navigationView.setNavigationItemSelectedListener(this);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivityForResult(new Intent(MainActivity.this,
                        AddAccountActivity.class), 1);
//                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
//                        .setAction("Action", null).show();
            }
        });
        initPreView();
    }

    private void initPreView() {
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.frame_content, new HomeFragment()).commit();
        toolbar.setTitle(getString(R.string.nav_header_account));
        MenuItem mItem = mNavigationView.getMenu().getItem(0);
        mItem.setChecked(true);
    }


    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            Toast.makeText(this, "action_settings", Toast.LENGTH_SHORT).show();
//            startActivityForResult(new Intent(MainActivity.this,
//                    ActivitySetting.class), 1);
            return true;
        }
        if (id == R.id.tool_add) {
            Toast.makeText(this, "tool_add", Toast.LENGTH_SHORT).show();
            startActivityForResult(new Intent(MainActivity.this,
                    AddAccountActivity.class), 1);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")

    private void setupDrawerContent(NavigationView navigationView) {
        navigationView
                .setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(MenuItem menuItem) {
                        mDrawerLayout.closeDrawers();
                        switch (menuItem.getItemId()) {
                            case R.id.nav_home:
                                initPreView();
                                break;
                            case R.id.nav_history:

//                                getSupportFragmentManager()
//                                        .beginTransaction()
//                                        .replace(R.id.frame_content,
//                                                new Fragment_History()).commit();
                                toolbar.setTitle(getString(R.string.nav_header_accounts));
                                break;
                            case R.id.nav_yusuan:
//                                getSupportFragmentManager()
//                                        .beginTransaction()
//                                        .replace(R.id.frame_content,
//                                                new Fragment_Yusuan()).commit();
                                toolbar.setTitle(getString(R.string.nav_header_ys));
                                break;

                            case R.id.nav_fenpei:
//                                getSupportFragmentManager()
//                                        .beginTransaction()
//                                        .replace(R.id.frame_content,
//                                                new Fragment_Fenpei()).commit();
                                toolbar.setTitle(getString(R.string.nav_header_accout_all));
                                break;

                            case R.id.nav_about:
//                                startActivity(new Intent(MainActivity.this,
//                                        ActivityAbout.class));
                                toolbar.setTitle(getString(R.string.nav_header_about));
                                break;
                            case R.id.nav_exit:
                                MainActivity.this.finish();
                                System.exit(0);
                                break;
                        }
                        return true;
                    }
                });
    }

    @Override
    protected void onActivityResult(int arg0, int arg1, Intent arg2) {
        super.onActivityResult(arg0, arg1, arg2);
        initPreView();
    }


    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            exitBy2Click();
        }
        return false;
    }
    public void open() {
        mDrawerLayout.openDrawer(Gravity.LEFT);
    }

    private void exitBy2Click() {
        Timer tExit;
        if (!isExit) {
            isExit = true;
            Toast.makeText(this, "再按一次退出程序", Toast.LENGTH_SHORT).show();
            open();
            tExit = new Timer();
            tExit.schedule(new TimerTask() {
                @Override
                public void run() {
                    isExit = false;
                }
            }, 2000);
        } else {
            this.finish();
            System.exit(0);
        }
    }

}
