package com.architjn.acjmusicplayer.utils.adapters;

import android.animation.ArgbEvaluator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.architjn.acjmusicplayer.R;
import com.architjn.acjmusicplayer.ui.layouts.activity.MainActivity;
import com.architjn.acjmusicplayer.ui.layouts.fragments.ArtistListFragment;
import com.architjn.acjmusicplayer.utils.items.Artist;

import java.util.ArrayList;

/**
 * Created by architjn on 28/11/15.
 */
public class ArtistsListAdapter extends RecyclerView.Adapter<ArtistsListAdapter.SimpleItemViewHolder> {

    private static final String TAG = "ArtistsListAdapter-TAG";
    private ArrayList<Artist> items;
    private ArtistListFragment fragment;
    private Context context;
    private int expandedPosition = -1;
    private SimpleItemViewHolder expandedHolder;

    public ArtistsListAdapter(Context context, ArrayList<Artist> items, ArtistListFragment fragment) {
        this.context = context;
        this.items = items;
        this.fragment = fragment;
    }

    @Override
    public ArtistsListAdapter.SimpleItemViewHolder onCreateViewHolder(ViewGroup parent,
                                                                      int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).
                inflate(R.layout.artist_list_item, parent, false);
        return new SimpleItemViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final ArtistsListAdapter.SimpleItemViewHolder holder,
                                 final int position) {
        holder.img.setImageDrawable(new ColorDrawable(0xFFFFFFFF));
        holder.name.setText(items.get(position).getArtistName());
//        Bitmap bitmap = BitmapFactory.decodeResource(this.getResources(),R.drawable.circularimage);
//        Bitmap circularBitmap = ImageConverter.getRoundedCornerBitmap(bitmap, 100);
        holder.songCount.setText(context.getResources().getString(R.string.songs)
                + " " + items.get(position).getNumberOfSongs() + " . "
                + context.getResources().getString(R.string.albums) + " "
                + items.get(position).getNumberOfAlbums());
        if (position == expandedPosition) {
            expandWithoutAnimation(holder);
        } else {
            collapseWithoutAnimation(holder);
        }
        holder.mainView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Check for an expanded view, collapse if you find one
                if (expandedPosition != position) {
                    if (expandedPosition >= 0) {
                        collapse();
                    }
                    expand(holder, position);
                } else {

                }
            }
        });
    }

    private void expand(SimpleItemViewHolder holder, int position) {
        expandedPosition = position;
        expandedHolder = holder;
        animateElevation(0, 12, holder.mainView);
        holder.mainView.setBackgroundColor(0xffffffff);
        if (expandedPosition == -1)
            animateHeight(0, 400, holder.expandedArea, false);
        else
            animateHeight(0, 400, holder.expandedArea, true);
    }

    private void collapse() {
        animateElevation(12, 0, expandedHolder.mainView);
        expandedHolder.mainView.setBackgroundColor(0xfffafafa);
        animateHeight(400, 0, expandedHolder.expandedArea, false);
        expandedPosition = -1;
        expandedHolder = null;
    }

    private void collapseWithoutAnimation(SimpleItemViewHolder holder) {
        holder.mainView.setElevation(0);
        holder.expandedArea.setLayoutParams(new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, dpToPx(0)));
        holder.mainView.setBackgroundColor(0xfffafafa);
    }

    private void expandWithoutAnimation(SimpleItemViewHolder holder) {
        holder.mainView.setElevation(12);
        holder.expandedArea.setLayoutParams(new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, dpToPx(200)));
        holder.mainView.setBackgroundColor(0xffffffff);
    }

    @Override
    public int getItemCount() {
        return items.size();
    }


    public int dpToPx(int dp) {
        DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
        int px = Math.round(dp * (displayMetrics.xdpi / DisplayMetrics.DENSITY_DEFAULT));
        return px;
    }

    private void animateElevation(int from, int to, final View view) {
        Integer elevationFrom = from;
        Integer elevationTo = to;
        ValueAnimator colorAnimation =
                ValueAnimator.ofObject(
                        new ArgbEvaluator(), elevationFrom, elevationTo);
        colorAnimation.addUpdateListener(
                new ValueAnimator.AnimatorUpdateListener() {
                    @Override
                    public void onAnimationUpdate(ValueAnimator animator) {
                        view.setElevation(
                                (Integer) animator.getAnimatedValue());
                    }

                });
        colorAnimation.start();
    }

    private void animateHeight(int from, int to, final View view, boolean delay) {
        // Declare a ValueAnimator object
        int duration = 500;
        ValueAnimator valueAnimator;
        valueAnimator = ValueAnimator.ofInt(from, to); // These values in this method can be changed to expand however much you like
        valueAnimator.setDuration(duration);
        if (delay)
            valueAnimator.setStartDelay(duration);
        valueAnimator.setInterpolator(new AccelerateDecelerateInterpolator());
        valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            public void onAnimationUpdate(ValueAnimator animation) {
                Integer value = (Integer) animation.getAnimatedValue();
                view.getLayoutParams().height = value.intValue();
                view.requestLayout();
            }
        });


        valueAnimator.start();
    }

    public void recyclerScrolled() {
//        if (expandedPosition != -1 && expandedHolder != null)
//            collapse();
    }

    public class SimpleItemViewHolder extends RecyclerView.ViewHolder {

        public TextView name, songCount;
        public View mainView;
        public LinearLayout expandedArea;
        public ImageView img;

        public SimpleItemViewHolder(View itemView) {
            super(itemView);
            mainView = itemView;
            img = (ImageView) itemView.findViewById(R.id.artist_item_img);
            name = (TextView) itemView.findViewById(R.id.artist_item_name);
            expandedArea = (LinearLayout) itemView.findViewById(R.id.expanded_area);
            songCount = (TextView) itemView.findViewById(R.id.artist_item_song_count);
        }
    }


    public void onBackPressed() {
        if (expandedPosition != -1)
            collapse();
        else
            ((MainActivity) fragment.getActivity()).killActivity();
    }

}
