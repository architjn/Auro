package com.architjn.acjmusicplayer.ui.layouts.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.TabLayout;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.architjn.acjmusicplayer.R;
import com.architjn.acjmusicplayer.utils.adapters.ViewPagerAdapter;
import com.architjn.acjmusicplayer.service.MusicService;
import com.architjn.acjmusicplayer.ui.layouts.activity.settings.Settings;
import com.architjn.acjmusicplayer.ui.layouts.fragments.AlbumsFragment;
import com.architjn.acjmusicplayer.ui.layouts.fragments.ArtistsFragment;
import com.architjn.acjmusicplayer.ui.layouts.fragments.GenresFragment;
import com.architjn.acjmusicplayer.ui.layouts.fragments.SongsFragment;


public class MainActivity extends AppCompatActivity {

    private SharedPreferences settingsPref;
    private FloatingActionButton fab;
    private DrawerLayout drawerLayout;
    private Toolbar toolbar;
    private ViewPager viewPager;
    private NavigationView navigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        overridePendingTransition(0, 0);
        Toolbar toolbar = (Toolbar) findViewById(R.id.main_toolbar);
        setSupportActionBar(toolbar);

        init();
        setDrawer();

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent requestSongDetials = new Intent();
                requestSongDetials.setAction(MusicService.ACTION_REQUEST_SONG_DETAILS);
                sendBroadcast(requestSongDetials);
            }
        });

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
//            getWindow().setExitTransition(new Explode());
        }

        setupViewPager(viewPager);
        TabLayout tabLayout = (TabLayout) findViewById(R.id.main_tablayout);
        if (settingsPref.getBoolean("pref_extend_tabs", false))
            tabLayout.setTabMode(TabLayout.MODE_FIXED);
        else
            tabLayout.setTabMode(TabLayout.MODE_SCROLLABLE);
        tabLayout.setupWithViewPager(viewPager);
    }

    private void init() {
        fab = (FloatingActionButton) findViewById(R.id.fab_main);
        viewPager = (ViewPager) findViewById(R.id.main_viewPager);
        toolbar = (Toolbar) findViewById(R.id.main_toolbar);
        settingsPref = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
        drawerLayout = (DrawerLayout) findViewById(R.id.main_drawerlayout);
        navigationView = (NavigationView) findViewById(R.id.main_navigationview);
    }

    private void setupViewPager(ViewPager viewPager) {
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
        adapter.addFrag(new SongsFragment(), getResources().getString(R.string.songs));
        adapter.addFrag(new AlbumsFragment(), getResources().getString(R.string.album));
        adapter.addFrag(new ArtistsFragment(), getResources().getString(R.string.artist));
        adapter.addFrag(new GenresFragment(), getResources().getString(R.string.genres));
        viewPager.setAdapter(adapter);
        viewPager.setCurrentItem(getIntent().getIntExtra("pos", 2) - 2);
    }

    public void setDrawer() {
        drawerLayout.setStatusBarBackgroundColor(Color.TRANSPARENT);
        if (toolbar != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            toolbar.setNavigationIcon(R.drawable.ic_drawer);
            toolbar.setNavigationOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    drawerLayout.openDrawer(GravityCompat.START);
                }
            });
        }
        navigationView.getMenu().getItem(getIntent().getIntExtra("pos", 2)).setChecked(true);
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(MenuItem menuItem) {
                switch (menuItem.getItemId()) {
                    case R.id.navigation_home:
                        startActivity(new Intent(MainActivity.this, HomeActivity.class));
                        finish();
                        drawerLayout.closeDrawer(GravityCompat.START);
                        break;
                    case R.id.navigation_playlist:
                        startActivity(new Intent(MainActivity.this, HomeActivity.class));
                        finish();
                        drawerLayout.closeDrawer(GravityCompat.START);
                        break;
                    case R.id.navigation_songs:
                        viewPager.setCurrentItem(0);
                        drawerLayout.closeDrawer(GravityCompat.START);
                        break;
                    case R.id.navigation_albums:
                        viewPager.setCurrentItem(1);
                        drawerLayout.closeDrawer(GravityCompat.START);
                        break;
                    case R.id.navigation_artists:
                        viewPager.setCurrentItem(2);
                        drawerLayout.closeDrawer(GravityCompat.START);
                        break;
                    case R.id.navigation_genres:
                        viewPager.setCurrentItem(3);
                        drawerLayout.closeDrawer(GravityCompat.START);
                        break;
                    case R.id.navigation_sub_item_2:
                        startActivity(new Intent(MainActivity.this, Settings.class));
                        drawerLayout.closeDrawer(GravityCompat.START);
                        break;
                }
                return false;
            }
        });
        viewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                navigationView.getMenu().getItem(position + 2).setChecked(true);
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public void onResume() {
        super.onResume();
        if (fab.getVisibility() != View.VISIBLE)
            fab.setVisibility(View.VISIBLE);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_settings) {
            startActivity(new Intent(MainActivity.this, Settings.class));
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

}
