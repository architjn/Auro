package com.architjn.acjmusicplayer.elements;

import android.graphics.Rect;
import android.support.v7.widget.RecyclerView;
import android.view.View;

public class SpacesItemDecoration extends RecyclerView.ItemDecoration {
    private int space, columns;

    public SpacesItemDecoration(int space, int columns) {
        this.space = space;
        this.columns = columns;
    }

    @Override
    public void getItemOffsets(Rect outRect, View view,
                               RecyclerView parent, RecyclerView.State state) {
        outRect.left = space / 2;
        outRect.right = space / 2;
        outRect.bottom = space;

        // Add top margin only for the first item to avoid double space between items
        if (parent.getChildPosition(view) < columns)
            outRect.top = space;
    }
}