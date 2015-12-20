package com.architjn.acjmusicplayer.utils;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.architjn.acjmusicplayer.R;

public class PlayingListDividerItemDecoration extends RecyclerView.ItemDecoration {
    private Drawable mDivider;
    private int size;

    public PlayingListDividerItemDecoration(Context context, int paddingLeft) {
        this.size = paddingLeft;
        mDivider = ContextCompat.getDrawable(context,R.drawable.line_divider);
    }

    @Override
    public void onDrawOver(Canvas c, RecyclerView parent, RecyclerView.State state) {
        int left;
        int right = parent.getWidth() - parent.getPaddingRight();

        int childCount = parent.getChildCount();
        for (int i = 0; i < childCount; i++) {
            if (i == 1) {
                left = parent.getPaddingLeft();
                draw(left, right, parent, c, i);
            } else if (i != 2) {
                left = parent.getPaddingLeft() + (size * 2);
                draw(left, right, parent, c, i);
            }
        }
    }

    private void draw(int left, int right, RecyclerView parent, Canvas c, int i) {
        View child = parent.getChildAt(i);

        RecyclerView.LayoutParams params = (RecyclerView.LayoutParams) child.getLayoutParams();

        int top = child.getBottom() + params.bottomMargin;
        int bottom = top + mDivider.getIntrinsicHeight();

        mDivider.setBounds(left, top, right, bottom);
        mDivider.draw(c);
    }
}