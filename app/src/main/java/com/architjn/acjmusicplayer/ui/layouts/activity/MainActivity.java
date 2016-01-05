package com.architjn.acjmusicplayer.ui.layouts.activity;

import android.app.SearchManager;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.AnimRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.architjn.acjmusicplayer.R;
import com.architjn.acjmusicplayer.service.PlayerService;
import com.architjn.acjmusicplayer.ui.layouts.fragments.AlbumsListFragment;
import com.architjn.acjmusicplayer.ui.layouts.fragments.ArtistListFragment;
import com.architjn.acjmusicplayer.ui.layouts.fragments.PlayerFragment;
import com.architjn.acjmusicplayer.ui.layouts.fragments.PlaylistListFragment;
import com.architjn.acjmusicplayer.ui.layouts.fragments.SearchViewFragment;
import com.architjn.acjmusicplayer.ui.layouts.fragments.SongsListFragment;
import com.architjn.acjmusicplayer.ui.widget.slidinguppanel.SlidingUpPanelLayout;

/**
 * Created by architjn on 27/11/15.
 */
public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity-TAG";
    private FragmentName currentFragment;
    private FragmentName lastExpanded;
    private int lastItem;
    private DrawerLayout drawerLayout;
    private Toolbar toolbar;
    private int currentItem = -1;
    private SongsListFragment songFragment;
    private AlbumsListFragment albumFragment;
    private ArtistListFragment artistFragment;
    private PlaylistListFragment playlistFragment;
    private SlidingUpPanelLayout slidingUpPanelLayout;
    private PlayerFragment playerFragment;

    public static boolean activityRuning = false;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        setTheme(R.style.AppTheme_Main);
        super.onCreate(savedInstanceState);

        //Start music player service
        startService(new Intent(this, PlayerService.class));

        setTheView();

        setAlbumFragment();
        activityRuning = true;
    }

    private void setAlbumFragment() {
        albumFragment = new AlbumsListFragment();
        fragmentSwitcher(albumFragment, 1, FragmentName.Albums,
                android.R.anim.fade_in, android.R.anim.fade_out);
    }

    private void setTheView() {
        //set Content view
        setContentView(R.layout.activity_main);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null)
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        init();
        initDrawer();
        sendBroadcast(new Intent(PlayerService.ACTION_GET_SONG));
    }

    private void init() {
        slidingUpPanelLayout = (SlidingUpPanelLayout) findViewById(R.id.sliding_layout);
        initSmallPlayer();
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if (getIntent().getBooleanExtra("openPanel", false)) {
                    slidingUpPanelLayout.expandPanel();
                }
            }
        }, 2000);
    }

    private void initDrawer() {
        ListView drawerList = (ListView) findViewById(R.id.left_drawer);
        drawerLayout = (DrawerLayout) findViewById(R.id.drawerLayout);
        String[] leftSliderData = {getResources().getString(R.string.songs),
                getResources().getString(R.string.albums),
                getResources().getString(R.string.artists),
                getResources().getString(R.string.playlist)};
        ArrayAdapter<String> navigationDrawerAdapter = new ArrayAdapter<>(
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
                                FragmentName.Songs,
                                android.R.anim.slide_in_left, android.R.anim.slide_out_right);
                        break;
                    case 1:
                        if (albumFragment == null)
                            albumFragment = new AlbumsListFragment();
                        fragmentSwitcher(albumFragment, i,
                                FragmentName.Albums,
                                android.R.anim.slide_in_left, android.R.anim.slide_out_right);
                        break;
                    case 2:
                        if (artistFragment == null)
                            artistFragment = new ArtistListFragment();
                        fragmentSwitcher(artistFragment, i,
                                FragmentName.Artists,
                                android.R.anim.slide_in_left, android.R.anim.slide_out_right);
                        break;
                    case 3:
                        if (playlistFragment == null)
                            playlistFragment = new PlaylistListFragment();
                        fragmentSwitcher(playlistFragment, i,
                                FragmentName.Playlists,
                                android.R.anim.slide_in_left, android.R.anim.slide_out_right);
                        break;
                }
            }
        });
        ActionBarDrawerToggle drawerToggle = new ActionBarDrawerToggle(this, drawerLayout,
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

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        try {
            albumFragment.onRequestPermissionsResult(requestCode, permissions, grantResults);
        } catch (NullPointerException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (albumFragment != null) {
            playerFragment.onResume();
        }
        activityRuning = true;
    }

    @Override
    protected void onPause() {
        super.onPause();
        activityRuning = false;
    }

    private void fragmentSwitcher(Fragment fragment, int itemId,
                                  FragmentName fname, @AnimRes int animationEnter,
                                  @AnimRes int animationExit) {
        currentFragment = fname;
        if (currentItem == itemId) {
            // Don't allow re-selection of the currently active item
            return;
        }
        currentItem = itemId;
        if (getSupportActionBar() != null)
            getSupportActionBar().setTitle(String.valueOf(fname));

        getSupportFragmentManager().beginTransaction()
                .setCustomAnimations(animationEnter, animationExit)
                .replace(R.id.main_fragment_holder, fragment)
                .commitAllowingStateLoss();

        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            final Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    drawerLayout.closeDrawer(GravityCompat.START);
                }
            }, 200);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        activityRuning = false;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        // Retrieve the SearchView and plug it into SearchManager
        setSearchView(menu);
        return true;
    }

    private void setSearchView(Menu menu) {
        final SearchView searchView = (SearchView) MenuItemCompat
                .getActionView(menu.findItem(R.id.action_search));
        final SearchViewFragment searchViewFragment = new SearchViewFragment();
        searchViewFragment.setSearchView(searchView);
        MenuItemCompat.setOnActionExpandListener(menu.findItem(R.id.action_search),
                new MenuItemCompat.OnActionExpandListener() {
                    @Override
                    public boolean onMenuItemActionExpand(MenuItem item) {
                        lastExpanded = currentFragment;
                        lastItem = currentItem;
                        fragmentSwitcher(searchViewFragment, -1, FragmentName.Search,
                                android.R.anim.fade_in, android.R.anim.fade_out);
                        return true;
                    }

                    @Override
                    public boolean onMenuItemActionCollapse(MenuItem item) {
                        fragmentSwitcher(getFragmentFromName(lastExpanded), lastItem,
                                lastExpanded, android.R.anim.fade_in, android.R.anim.fade_out);
                        return true;
                    }
                });
        SearchManager searchManager = (SearchManager) getSystemService(SEARCH_SERVICE);
        searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
    }

    private void changeToolbarColorLight(View view) {
        if (view != null) {
            if (view instanceof TextView) {
                ((TextView) view).setTextColor(Color.BLACK);
            } else if (view instanceof ViewGroup) {
                ViewGroup viewGroup = (ViewGroup) view;
                for (int i = 0; i < viewGroup.getChildCount(); i++) {
                    changeToolbarColorLight(viewGroup.getChildAt(i));
                }
            }
        }
    }

    private void initSmallPlayer() {
        playerFragment = new PlayerFragment();
        playerFragment.setSlidingUpPanelLayout(slidingUpPanelLayout);
        FragmentManager fragmentManager1 = getSupportFragmentManager();
        fragmentManager1.beginTransaction()
                .replace(R.id.panel_holder, playerFragment).commitAllowingStateLoss();
    }

    public void killActivity() {
        super.onBackPressed();
    }

    private Fragment getFragmentFromName(FragmentName name) {
        switch (name) {
            case Songs:
                return songFragment;
            case Albums:
                return albumFragment;
            case Artists:
                return artistFragment;
            case Playlists:
                return playlistFragment;
        }
        return null;
    }

    @Override
    public void onBackPressed() {
        if (slidingUpPanelLayout.isPanelExpanded()) {
            slidingUpPanelLayout.collapsePanel();
            return;
        }
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

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (event.getKeyCode() == KeyEvent.KEYCODE_BACK) {
            onBackPressed();
        }
        if (playerFragment != null)
            return playerFragment.onKeyEvent(event);
        return super.onKeyDown(keyCode, event);
    }

    public enum FragmentName {
        Albums, Songs, Artists, Playlists, Search
    }

}
