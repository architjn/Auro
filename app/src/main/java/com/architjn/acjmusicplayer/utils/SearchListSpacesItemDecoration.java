package com.architjn.acjmusicplayer.utils;

import android.graphics.Rect;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.architjn.acjmusicplayer.utils.adapters.SearchListAdapter;

public class SearchListSpacesItemDecoration extends RecyclerView.ItemDecoration {
    private int space;
    private SearchListAdapter adapter;

    public SearchListSpacesItemDecoration(int space, SearchListAdapter adapter) {
        this.space = space;
        this.adapter = adapter;
    }

    @Override
    public void getItemOffsets(Rect outRect, View view,
                               RecyclerView parent, RecyclerView.State state) {
        int pos = parent.getChildLayoutPosition(view);
        if (adapter.whatView(pos) == adapter.ITEM_VIEW_TYPE_LIST_ALBUM) {
            if (pos % 2 == 0 && pos != 0) {
                outRect.left = space;
            }
            outRect.bottom = space;
        }
    }
}