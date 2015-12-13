package com.architjn.acjmusicplayer.utils.adapters;

import android.animation.ArgbEvaluator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.architjn.acjmusicplayer.R;
import com.architjn.acjmusicplayer.ui.layouts.activity.MainActivity;
import com.architjn.acjmusicplayer.ui.layouts.fragments.PlaylistListFragment;
import com.architjn.acjmusicplayer.utils.PlaylistDBHelper;
import com.architjn.acjmusicplayer.utils.items.Playlist;
import com.architjn.acjmusicplayer.utils.items.Song;

import java.util.ArrayList;

/**
 * Created by architjn on 28/11/15.
 */
public class PlaylistListAdapter extends RecyclerView.Adapter<PlaylistListAdapter.SimpleItemViewHolder> {

    private static final String TAG = "PlaylistListAdapter-TAG";
    private ArrayList<Playlist> items;
    private PlaylistListFragment fragment;
    private Context context;
    private int expandedPosition = -1;
    private SimpleItemViewHolder expandedHolder;
    private int expandSize = 45;

    public PlaylistListAdapter(Context context, ArrayList<Playlist> items, PlaylistListFragment fragment) {
        this.context = context;
        this.items = items;
        this.fragment = fragment;
    }

    @Override
    public PlaylistListAdapter.SimpleItemViewHolder onCreateViewHolder(ViewGroup parent,
                                                                       int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).
                inflate(R.layout.playlist_list_item, parent, false);
        return new SimpleItemViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final PlaylistListAdapter.SimpleItemViewHolder holder,
                                 final int position) {
        holder.name.setText(items.get(position).getPlaylistName());
        holder.songCount.setText(items.get(position).getSongCount() + " "
                + context.getResources().getString(R.string.songs));
        if (position == expandedPosition) {
            expandWithoutAnimation(holder);
        } else {
            collapseWithoutAnimation(holder);
        }
        setOnClicks(holder, position);
    }

    private void setOnClicks(final SimpleItemViewHolder holder, final int position) {
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
                    ArrayList<Song> songList = new PlaylistDBHelper(context).getAllPlaylistSongs(
                            items.get(position).getPlaylistId());
                    Log.v(TAG, songList.size() + " << ");
                    for (int i = 0; i < songList.size(); i++) {
                        Log.v(TAG, songList.get(i).getName());
                    }
                }
            }
        });
        holder.menu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final PlaylistDBHelper helper = new PlaylistDBHelper(context);
                PopupMenu pm = new PopupMenu(context, view);
                pm.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        switch (item.getItemId()) {
                            case R.id.popup_playlist_play:
                                break;
                            case R.id.popup_playlist_shuffle:
                                break;
                            case R.id.popup_playlist_delete:
                                helper.deletePlaylist(items.get(position).getPlaylistId());
                                updateNewList(helper.getAllPlaylist());
                                if (items.size() == 0)
                                    fragment.listIsEmpty();
                                break;
                        }
                        return false;
                    }
                });
                pm.inflate(R.menu.popup_playlist);
                pm.show();
            }
        });
        holder.playButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });
        holder.shuffleButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });
    }

    private void expand(SimpleItemViewHolder holder, int position) {
        expandedPosition = position;
        expandedHolder = holder;
        animateElevation(0, 12, holder.mainView);
        holder.mainView.setBackgroundColor(0xffffffff);
        if (expandedPosition == -1)
            animateHeight(0, dpToPx(expandSize), holder.expandedArea, false);
        else
            animateHeight(0, dpToPx(expandSize), holder.expandedArea, true);
    }

    private void collapse() {
        animateElevation(12, 0, expandedHolder.mainView);
        expandedHolder.mainView.setBackgroundColor(0xfffafafa);
        animateHeight(dpToPx(expandSize), 0, expandedHolder.expandedArea, false);
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
                ViewGroup.LayoutParams.MATCH_PARENT, dpToPx(expandSize)));
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
        int duration = 300;
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

    public void updateNewList(ArrayList<Playlist> newList) {
        items = newList;
        notifyDataSetChanged();
    }

    public class SimpleItemViewHolder extends RecyclerView.ViewHolder {

        public TextView name, songCount, playButton, shuffleButton;
        public View mainView, menu;
        public LinearLayout expandedArea;

        public SimpleItemViewHolder(View itemView) {
            super(itemView);
            mainView = itemView;
            name = (TextView) itemView.findViewById(R.id.playlist_item_name);
            expandedArea = (LinearLayout) itemView.findViewById(R.id.playlist_expanded_area);
            songCount = (TextView) itemView.findViewById(R.id.playlist_item_song_count);
            menu = itemView.findViewById(R.id.playlist_item_menu);
            playButton = (TextView) itemView.findViewById(R.id.playlist_play_button);
            shuffleButton = (TextView) itemView.findViewById(R.id.playlist_shuffle_button);
        }
    }


    public void onBackPressed() {
        if (expandedPosition != -1)
            collapse();
        else
            ((MainActivity) fragment.getActivity()).killActivity();
    }

}
