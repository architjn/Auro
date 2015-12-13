package com.architjn.acjmusicplayer.ui.layouts.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.architjn.acjmusicplayer.R;
import com.architjn.acjmusicplayer.ui.layouts.fragments.AlbumsListFragment;
import com.architjn.acjmusicplayer.ui.layouts.fragments.ArtistListFragment;
import com.architjn.acjmusicplayer.ui.layouts.fragments.PlaylistListFragment;
import com.architjn.acjmusicplayer.ui.layouts.fragments.SongsListFragment;
import com.architjn.acjmusicplayer.ui.widget.OnSwipeListener;
import com.architjn.acjmusicplayer.ui.widget.SwipeInterface;

/**
 * Created by architjn on 27/11/15.
 */
public class MainActivity extends AppCompatActivity {


    private static final String TAG = "MainActivity-TAG";

    public enum FragmentName {
        Albums, Songs, Artists, Playlists
    }

    private DrawerLayout drawerLayout;
    private ListView drawerList;
    private ArrayAdapter<String> navigationDrawerAdapter;
    private ActionBarDrawerToggle drawerToggle;
    private Toolbar toolbar;
    private int currentItem = -1;
    private SongsListFragment songFragment;
    private AlbumsListFragment albumFragment;
    private ArtistListFragment artistFragment;
    private PlaylistListFragment playlistFragment;

    private FragmentName currentFragment;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        initDrawer();
        setPlayer();
        songFragment = new SongsListFragment();
        fragmentSwitcher(songFragment, 0, FragmentName.Songs);
    }

    private void setPlayer() {
        findViewById(R.id.small_panel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                launchPlayer();
            }
        });
        findViewById(R.id.small_panel).setOnTouchListener(
                new OnSwipeListener(
                        new SwipeInterface() {
                            @Override
                            public void bottom2top(View v) {
                                launchPlayer();
                            }

                            @Override
                            public void left2right(View v) {
                                launchPlayer();
                            }

                            @Override
                            public void right2left(View v) {
                                launchPlayer();
                            }

                            @Override
                            public void top2bottom(View v) {
                                launchPlayer();
                            }
                        }) {
                });
    }

    private void launchPlayer() {
        startActivity(new Intent(MainActivity.this, PlayerActivity.class));
    }

    private void initDrawer() {
        drawerList = (ListView) findViewById(R.id.left_drawer);
        drawerLayout = (DrawerLayout) findViewById(R.id.drawerLayout);
        String[] leftSliderData = {getResources().getString(R.string.songs),
                getResources().getString(R.string.albums),
                getResources().getString(R.string.artists),
                getResources().getString(R.string.playlist)};
        navigationDrawerAdapter = new ArrayAdapter<String>(
                MainActivity.this, R.layout.drawer_list_item, leftSliderData);
        drawerList.setAdapter(navigationDrawerAdapter);
        drawerList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(
                    AdapterView<?> adapterView, View view, int i, long l) {
                switch (i) {
                    case 0:
                        if (songFragment == null)
                            songFragment = new SongsListFragment();
                        fragmentSwitcher(songFragment, i,
                                FragmentName.Songs);
                        break;
                    case 1:
                        if (albumFragment == null)
                            albumFragment = new AlbumsListFragment();
                        fragmentSwitcher(albumFragment, i,
                                FragmentName.Albums);
                        break;
                    case 2:
                        if (artistFragment == null)
                            artistFragment = new ArtistListFragment();
                        fragmentSwitcher(artistFragment, i,
                                FragmentName.Artists);
                        break;
                    case 3:
                        if (playlistFragment == null)
                            playlistFragment = new PlaylistListFragment();
                        fragmentSwitcher(playlistFragment, i,
                                FragmentName.Playlists);
                        break;
                }
            }
        });
        drawerToggle = new ActionBarDrawerToggle(this, drawerLayout,
                toolbar, 0, 0) {

            @Override
            public void onDrawerClosed(View drawerView) {
                super.onDrawerClosed(drawerView);
                invalidateOptionsMenu();
                syncState();
            }

            @Override
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
                invalidateOptionsMenu();
                syncState();
            }
        };
        drawerLayout.setDrawerListener(drawerToggle);
        drawerToggle.syncState();
    }

    private void fragmentSwitcher(Fragment fragment, int itemId, FragmentName fname) {
        currentFragment = fname;
        if (currentItem == itemId) {
            // Don't allow re-selection of the currently active item
            return;
        }
        currentItem = itemId;
        if (getSupportActionBar() != null)
            getSupportActionBar().setTitle(String.valueOf(fname));

        getSupportFragmentManager().beginTransaction()
                .setCustomAnimations(android.R.anim.slide_in_left, 0)
                .replace(R.id.main_fragment_holder, fragment)
                .commit();

        if (drawerLayout.isDrawerOpen(Gravity.LEFT)) {
            drawerLayout.closeDrawer(Gravity.LEFT);
        }
    }

    public void killActivity() {
        super.onBackPressed();
    }

    @Override
    public void onBackPressed() {
        switch (currentFragment) {
            case Songs:
                songFragment.onBackPress();
                break;
            case Albums:
                albumFragment.onBackPress();
                break;
            case Artists:
                artistFragment.onBackPress();
                break;
            case Playlists:
                playlistFragment.onBackPress();
                break;
        }
    }
}
