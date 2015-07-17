package com.architjn.acjmusicplayer.ui.layouts.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.architjn.acjmusicplayer.R;
import com.architjn.acjmusicplayer.elements.adapters.ViewPagerAdapter;
import com.architjn.acjmusicplayer.service.MusicService;
import com.architjn.acjmusicplayer.ui.layouts.activity.settings.Settings;
import com.architjn.acjmusicplayer.ui.layouts.fragments.AlbumsFragment;
import com.architjn.acjmusicplayer.ui.layouts.fragments.ArtistsFragment;
import com.architjn.acjmusicplayer.ui.layouts.fragments.GenresFragment;
import com.architjn.acjmusicplayer.ui.layouts.fragments.SongsFragment;


public class MainActivity extends AppCompatActivity {

    private SharedPreferences settingsPref;
    private FloatingActionButton fab;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        overridePendingTransition(0, 0);
        Toolbar toolbar = (Toolbar) findViewById(R.id.main_toolbar);
        setSupportActionBar(toolbar);

        Intent i = new Intent(MainActivity.this, MusicService.class);
        startService(i);

        fab = (FloatingActionButton) findViewById(R.id.fab_main);
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

        ViewPager viewPager = (ViewPager) findViewById(R.id.main_viewPager);
        setupViewPager(viewPager);
        TabLayout tabLayout = (TabLayout) findViewById(R.id.main_tablayout);
        settingsPref = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
        if (settingsPref.getBoolean("pref_extend_tabs", false))
            tabLayout.setTabMode(TabLayout.MODE_FIXED);
        else
            tabLayout.setTabMode(TabLayout.MODE_SCROLLABLE);
        tabLayout.setupWithViewPager(viewPager);
    }

    private void setupViewPager(ViewPager viewPager) {
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
        adapter.addFrag(new SongsFragment(), getResources().getString(R.string.songs));
        adapter.addFrag(new AlbumsFragment(fab), getResources().getString(R.string.album));
        adapter.addFrag(new ArtistsFragment(fab), getResources().getString(R.string.artist));
        adapter.addFrag(new GenresFragment(), getResources().getString(R.string.genres));
        viewPager.setAdapter(adapter);
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
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            startActivity(new Intent(MainActivity.this, Settings.class));
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

}
