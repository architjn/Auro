package com.architjn.acjmusicplayer.utils;

import android.graphics.Rect;
import android.support.v7.widget.RecyclerView;
import android.view.View;

public class AlbumListSpacesItemDecoration extends RecyclerView.ItemDecoration {
    private int space;

    public AlbumListSpacesItemDecoration(int space) {
        this.space = space;
    }

    @Override
    public void getItemOffsets(Rect outRect, View view,
                               RecyclerView parent, RecyclerView.State state) {
        int pos = parent.getChildLayoutPosition(view);
        if (pos % 2 == 0 && pos != 0) {
            outRect.left = space;
        }
        outRect.bottom = space;
    }


}