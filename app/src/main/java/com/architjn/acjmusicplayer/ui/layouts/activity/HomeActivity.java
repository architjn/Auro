package com.architjn.acjmusicplayer.ui.layouts.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
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
import android.view.MenuItem;
import android.view.View;

import com.architjn.acjmusicplayer.R;
import com.architjn.acjmusicplayer.elements.adapters.ViewPagerAdapter;
import com.architjn.acjmusicplayer.service.MusicService;
import com.architjn.acjmusicplayer.ui.layouts.activity.settings.Settings;
import com.architjn.acjmusicplayer.ui.layouts.fragments.HomeFragment;
import com.architjn.acjmusicplayer.ui.layouts.fragments.PlaylistFragment;

/**
 * Created by architjn on 31/08/15.
 */
public class HomeActivity extends AppCompatActivity {


    private FloatingActionButton fab;
    private ViewPager viewPager;
    private Toolbar toolbar;
    private SharedPreferences settingsPref;
    private NavigationView navigationView;
    private DrawerLayout drawerLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        init();
        setSupportActionBar(toolbar);
        setDrawer();
        setupViewPager();
        TabLayout tabLayout = (TabLayout) findViewById(R.id.home_tablayout);
        if (settingsPref.getBoolean("pref_extend_tabs", false))
            tabLayout.setTabMode(TabLayout.MODE_FIXED);
        else
            tabLayout.setTabMode(TabLayout.MODE_SCROLLABLE);
        tabLayout.setupWithViewPager(viewPager);

        Intent i = new Intent(HomeActivity.this, MusicService.class);
        startService(i);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent requestSongDetials = new Intent();
                requestSongDetials.setAction(MusicService.ACTION_REQUEST_SONG_DETAILS);
                sendBroadcast(requestSongDetials);
            }
        });
    }

    private void init() {
        fab = (FloatingActionButton) findViewById(R.id.fab_home);
        viewPager = (ViewPager) findViewById(R.id.home_viewPager);
        toolbar = (Toolbar) findViewById(R.id.home_toolbar);
        settingsPref = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
        drawerLayout = (DrawerLayout) findViewById(R.id.home_drawerlayout);
        navigationView = (NavigationView) findViewById(R.id.home_navigationview);
    }

    private void setupViewPager() {
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
        adapter.addFrag(new HomeFragment(), getResources().getString(R.string.home));
        adapter.addFrag(new PlaylistFragment(), getResources().getString(R.string.playlist));
        viewPager.setAdapter(adapter);
        viewPager.setCurrentItem(0);
    }

    public void setDrawer() {
        drawerLayout.setStatusBarBackgroundColor(Color.TRANSPARENT);
        navigationView.getMenu().getItem(0).setChecked(true);
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
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(MenuItem menuItem) {
                switch (menuItem.getItemId()) {
                    case R.id.navigation_home:
                        viewPager.setCurrentItem(0);
                        drawerLayout.closeDrawer(GravityCompat.START);
                        break;
                    case R.id.navigation_playlist:
                        viewPager.setCurrentItem(1);
                        drawerLayout.closeDrawer(GravityCompat.START);
                        break;
                    case R.id.navigation_songs:
                        Intent i = new Intent(HomeActivity.this, MainActivity.class);
                        i.putExtra("pos", 2);
                        startActivity(i);
                        finish();
                        drawerLayout.closeDrawer(GravityCompat.START);
                        break;
                    case R.id.navigation_albums:
                        Intent a = new Intent(HomeActivity.this, MainActivity.class);
                        a.putExtra("pos", 3);
                        startActivity(a);
                        finish();
                        drawerLayout.closeDrawer(GravityCompat.START);
                        break;
                    case R.id.navigation_artists:
                        Intent b = new Intent(HomeActivity.this, MainActivity.class);
                        b.putExtra("pos", 4);
                        startActivity(b);
                        finish();
                        drawerLayout.closeDrawer(GravityCompat.START);
                        break;
                    case R.id.navigation_genres:
                        Intent c = new Intent(HomeActivity.this, MainActivity.class);
                        c.putExtra("pos", 5);
                        startActivity(c);
                        finish();
                        drawerLayout.closeDrawer(GravityCompat.START);
                        break;
                    case R.id.navigation_sub_item_2:
                        startActivity(new Intent(HomeActivity.this, Settings.class));
                        finish();
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
                navigationView.getMenu().getItem(position).setChecked(true);
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }

}
