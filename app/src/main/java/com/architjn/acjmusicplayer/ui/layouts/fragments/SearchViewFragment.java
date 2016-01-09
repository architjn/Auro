package com.architjn.acjmusicplayer.ui.layouts.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.architjn.acjmusicplayer.R;
import com.architjn.acjmusicplayer.ui.layouts.activity.MainActivity;
import com.architjn.acjmusicplayer.utils.ListSongs;
import com.architjn.acjmusicplayer.utils.adapters.SearchListAdapter;
import com.architjn.acjmusicplayer.utils.decorations.SearchListSpacesItemDecoration;
import com.architjn.acjmusicplayer.utils.items.Album;
import com.architjn.acjmusicplayer.utils.items.Artist;
import com.architjn.acjmusicplayer.utils.items.Search;
import com.architjn.acjmusicplayer.utils.items.Song;
import com.lapism.searchview.SearchView;

import java.util.ArrayList;

/**
 * Created by architjn on 17/12/15.
 */
public class SearchViewFragment extends Fragment {

    private static final String TAG = "SearchViewFragment-TAG";
    private SearchView searchView;
    private RecyclerView rv;
    private Context context;
    private MainActivity activity;
    private View mainView, emptyView;
    private SearchListAdapter adapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_search,
                container, false);
        mainView = view;
        context = view.getContext();
        activity = (MainActivity) getActivity();
        setRecyclerView();
        return view;
    }

    private void setRecyclerView() {
        Search searchRes = new Search(new ArrayList<Song>(), new ArrayList<Album>(),
                new ArrayList<Artist>());
        rv = (RecyclerView) mainView.findViewById(R.id.search_view_results);
        emptyView = mainView.findViewById(R.id.search_empty_view);
        final GridLayoutManager manager = new GridLayoutManager(context, 2);
        manager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
            @Override
            public int getSpanSize(int position) {
                if (adapter.whatView(position) == SearchListAdapter.ITEM_VIEW_TYPE_LIST_ALBUM) {
                    return 1;
                } else {
                    return 2;
                }
            }
        });
        rv.setLayoutManager(manager);
        adapter = new SearchListAdapter(context, searchRes.getSongs(),
                searchRes.getAlbums(), searchRes.getArtists());
        rv.addItemDecoration(new SearchListSpacesItemDecoration(2, adapter));
        rv.setAdapter(adapter);
        emptyView.setVisibility(View.VISIBLE);
        rv.setVisibility(View.GONE);
    }

    public void setSearchView(SearchView searchView) {
        this.searchView = searchView;
        initSearchView();
    }

    private void initSearchView() {
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                searchView.closeSearch(false);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                if (newText.matches("")) {
                    if (emptyView != null) {
                        emptyView.setVisibility(View.VISIBLE);
                        rv.setVisibility(View.GONE);
                    }
                    return false;
                }
                emptyView.setVisibility(View.GONE);
                rv.setVisibility(View.VISIBLE);
                Search searchRes = ListSongs.getSearchResults(context, newText);
                adapter.updateList(searchRes);
                return true;
            }
        });
    }

    public void onBackPressed() {
        if (!searchView.isSearchOpen())
            activity.fragmentSwitcher(activity.getFragmentFromName(activity.lastExpanded),
                    activity.lastItem, activity.lastExpanded, android.R.anim.fade_in,
                    android.R.anim.fade_out);
    }

}
