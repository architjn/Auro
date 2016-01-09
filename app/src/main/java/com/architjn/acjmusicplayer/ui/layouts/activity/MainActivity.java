package com.architjn.acjmusicplayer.ui.layouts.activity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.speech.RecognizerIntent;
import android.support.annotation.AnimRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
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
import com.architjn.acjmusicplayer.ui.layouts.fragments.UpNextFragment;
import com.architjn.acjmusicplayer.ui.widget.slidinguppanel.SlidingUpPanelLayout;
import com.architjn.acjmusicplayer.utils.adapters.AlbumListAdapter;
import com.crashlytics.android.Crashlytics;
import com.lapism.searchview.SearchView;

import java.util.List;

import io.fabric.sdk.android.Fabric;

/**
 * Created by architjn on 27/11/15.
 */
public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity-TAG";
    private FragmentName currentFragment;
    private DrawerLayout drawerLayout;
    private Toolbar toolbar;
    private int currentItem = -1;
    private SongsListFragment songFragment;
    private AlbumsListFragment albumFragment;
    private ArtistListFragment artistFragment;
    private PlaylistListFragment playlistFragment;
    private SearchViewFragment searchViewFragment;
    private SlidingUpPanelLayout slidingUpPanelLayout;
    private PlayerFragment playerFragment;
    private UpNextFragment upNextFragment;
    private SearchView searchView;

    public static boolean activityRuning = false;

    public FragmentName lastExpanded;
    public int lastItem;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        setTheme(R.style.AppTheme_Main);
        super.onCreate(savedInstanceState);

        //Start music player service
        startService(new Intent(this, PlayerService.class));

        Fabric.with(this, new Crashlytics());
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
        initSearch();
    }

    private void initSearch() {
        searchView = (SearchView) findViewById(R.id.search_view);
        searchView.setVoiceSearch(true);
        searchViewFragment = new SearchViewFragment();
        searchViewFragment.setSearchView(searchView);
        searchView.setOnSearchViewListener(new SearchView.SearchViewListener() {

            @Override
            public void onSearchViewShown() {
                if (currentFragment != FragmentName.Search) {
                    lastExpanded = currentFragment;
                    lastItem = currentItem;
                    currentFragment = FragmentName.Search;
                }
                fragmentSwitcher(searchViewFragment, -1, FragmentName.Search,
                        android.R.anim.fade_in, android.R.anim.fade_out);
            }

            @Override
            public void onSearchViewClosed() {
            }
        });

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
        AlbumListAdapter.onceAnimated = false;
    }

    @Override
    protected void onPause() {
        super.onPause();
        activityRuning = false;
    }

    public void fragmentSwitcher(Fragment fragment, int itemId,
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
//        setSearchView(menu);
        return true;
    }

//    private void setSearchView(Menu menu) {
//        final SearchView searchView = (SearchView) MenuItemCompat
//                .getActionView(menu.findItem(R.id.action_search));
//        final SearchViewFragment searchViewFragment = new SearchViewFragment();
//        searchViewFragment.setSearchView(searchView);
//        MenuItemCompat.setOnActionExpandListener(menu.findItem(R.id.action_search),
//                new MenuItemCompat.OnActionExpandListener() {
//                    @Override
//                    public boolean onMenuItemActionExpand(MenuItem item) {
//                        lastExpanded = currentFragment;
//                        lastItem = currentItem;
//                        fragmentSwitcher(searchViewFragment, -1, FragmentName.Search,
//                                android.R.anim.fade_in, android.R.anim.fade_out);
//                        return true;
//                    }
//
//                    @Override
//                    public boolean onMenuItemActionCollapse(MenuItem item) {
//                        fragmentSwitcher(getFragmentFromName(lastExpanded), lastItem,
//                                lastExpanded, android.R.anim.fade_in, android.R.anim.fade_out);
//                        return true;
//                    }
//                });
//        SearchManager searchManager = (SearchManager) getSystemService(SEARCH_SERVICE);
//        searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
//    }


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

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_search: {
                searchView.showSearch(findViewById(R.id.action_search), true);
                return true;
            }
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void initSmallPlayer() {
        playerFragment = new PlayerFragment();
        upNextFragment = new UpNextFragment();
        playerFragment.setUpNextFragment(upNextFragment);
        playerFragment.setSlidingUpPanelLayout(slidingUpPanelLayout);
        FragmentManager fragmentManager1 = getSupportFragmentManager();
        fragmentManager1.beginTransaction()
                .replace(R.id.panel_holder, playerFragment).commitAllowingStateLoss();
    }

    public void killActivity() {
        super.onBackPressed();
    }

    public Fragment getFragmentFromName(FragmentName name) {
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
        if (searchView.isSearchOpen()) {
            searchView.closeSearch(true);
            return;
        }
        if (slidingUpPanelLayout.isPanelExpanded()) {
            playerFragment.onBackPressed();
            upNextFragment.onBackPressed();
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
            case Search:
                searchViewFragment.onBackPressed();
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == SearchView.SPEECH_REQUEST_CODE && resultCode == RESULT_OK) {
            List<String> matches = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
            if (matches != null && matches.size() > 0) {
                String searchWrd = matches.get(0);
                if (!TextUtils.isEmpty(searchWrd)) {
                    searchView.setQuery(searchWrd, false);
                }
            }
            return;
        }
        super.onActivityResult(requestCode, resultCode, data);
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
