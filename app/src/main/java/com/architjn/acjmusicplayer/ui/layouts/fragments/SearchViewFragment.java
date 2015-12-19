package com.architjn.acjmusicplayer.ui.layouts.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.architjn.acjmusicplayer.R;
import com.architjn.acjmusicplayer.utils.ListSongs;
import com.architjn.acjmusicplayer.utils.SearchListSpacesItemDecoration;
import com.architjn.acjmusicplayer.utils.adapters.SearchListAdapter;
import com.architjn.acjmusicplayer.utils.items.Search;

/**
 * Created by architjn on 17/12/15.
 */
public class SearchViewFragment extends Fragment {

    private static final String TAG = "SearchViewFragment-TAG";
    private SearchView searchView;
    private RecyclerView rv;
    private Context context;
    private View mainView;
    private SearchListAdapter adapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_search,
                container, false);
        mainView = view;
        context = view.getContext();
        setRecyclerView();
        return view;
    }

    private void setRecyclerView() {
        Search searchRes = ListSongs.getSearchResults(context, "Pharrell");
        rv = (RecyclerView) mainView.findViewById(R.id.search_view_results);
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
    }

    public void setSearchView(SearchView searchView) {
        this.searchView = searchView;
        initSearchView();
    }

    private void initSearchView() {
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                if (newText.matches(""))
                    return false;
                Search searchRes = ListSongs.getSearchResults(context, newText);
                adapter.updateList(searchRes);
                return true;
            }
        });
    }

}
