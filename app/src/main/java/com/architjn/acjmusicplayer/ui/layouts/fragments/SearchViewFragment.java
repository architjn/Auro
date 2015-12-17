package com.architjn.acjmusicplayer.ui.layouts.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.architjn.acjmusicplayer.R;
import com.architjn.acjmusicplayer.utils.adapters.SearchListAdapter;

import java.util.ArrayList;

/**
 * Created by architjn on 17/12/15.
 */
public class SearchViewFragment extends Fragment {

    private static final String TAG = "SearchViewFragment-TAG";
    private SearchView searchView;
    private RecyclerView rv;
    private Context context;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_search,
                container, false);
        context = view.getContext();
        ArrayList<String> strings = new ArrayList<>();
        rv = (RecyclerView) view.findViewById(R.id.search_view_results);
        rv.setLayoutManager(new LinearLayoutManager(context));
        rv.setAdapter(new SearchListAdapter(context, strings));
        return view;
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
                Log.v(TAG, newText+" <<");
                return true;
            }
        });
    }

}
