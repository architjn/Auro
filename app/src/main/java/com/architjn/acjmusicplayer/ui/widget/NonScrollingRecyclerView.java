package com.architjn.acjmusicplayer.ui.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.ViewGroup;
import android.widget.ListView;

/**
 * Created by architjn on 04/07/15.
 */
public class NonScrollingRecyclerView extends ListView {

    public NonScrollingRecyclerView(Context context) {
        super(context);
    }

    public NonScrollingRecyclerView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public NonScrollingRecyclerView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int heightMeasureSpec_custom = MeasureSpec.makeMeasureSpec(
                Integer.MAX_VALUE >> 2, MeasureSpec.AT_MOST);
        super.onMeasure(widthMeasureSpec, heightMeasureSpec_custom);
        ViewGroup.LayoutParams params = getLayoutParams();
        params.height = getMeasuredHeight();
    }

}
