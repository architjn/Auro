package com.architjn.acjmusicplayer.utils;

import android.content.Context;
import android.graphics.Rect;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.view.View;

public class ArtistSubListSpacesItemDecoration extends RecyclerView.ItemDecoration {
    private Context context;
    private int space;

    public ArtistSubListSpacesItemDecoration(Context context, int space) {
        this.context = context;
        this.space = space;
    }

    @Override
    public void getItemOffsets(Rect outRect, View view,
                               RecyclerView parent, RecyclerView.State state) {
        int pos = parent.getChildLayoutPosition(view);
        if (pos == 0) {
            outRect.left = dpToPx(75);
        }
        outRect.right = space;
    }

    public int dpToPx(int dp) {
        DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
        int px = Math.round(dp * (displayMetrics.xdpi / DisplayMetrics.DENSITY_DEFAULT));
        return px;
    }
}