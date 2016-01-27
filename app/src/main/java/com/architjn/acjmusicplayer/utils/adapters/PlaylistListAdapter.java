package com.architjn.acjmusicplayer.utils.adapters;

import android.animation.ArgbEvaluator;
import android.animation.ValueAnimator;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.architjn.acjmusicplayer.R;
import com.architjn.acjmusicplayer.service.PlayerService;
import com.architjn.acjmusicplayer.ui.layouts.activity.MainActivity;
import com.architjn.acjmusicplayer.ui.layouts.activity.PlaylistActivity;
import com.architjn.acjmusicplayer.ui.layouts.fragments.PlaylistListFragment;
import com.architjn.acjmusicplayer.utils.handlers.PlaylistDBHelper;
import com.architjn.acjmusicplayer.utils.items.Playlist;

import java.util.ArrayList;

/**
 * Created by architjn on 28/11/15.
 */
public class PlaylistListAdapter extends RecyclerView.Adapter<PlaylistListAdapter.SimpleItemViewHolder> {

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
                    Intent playlistSongPage = new Intent(context, PlaylistActivity.class);
                    playlistSongPage.putExtra("id", items.get(position).getPlaylistId());
                    context.startActivity(playlistSongPage);
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
                                playPlaylist(position);
                                break;
                            case R.id.popup_playlist_shuffle:
                                break;
                            case R.id.popup_playlist_rename:
                                renameDialog(helper, position);
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
                playPlaylist(position);
            }
        });
        holder.shuffleButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });
    }

    public void playPlaylist(final int position) {
        if (items.get(position).getSongCount() != 0)
            new Thread(new Runnable() {
                public void run() {
                    Intent i = new Intent();
                    i.setAction(PlayerService.ACTION_PLAY_PLAYLIST);
                    i.putExtra("id", items.get(position).getPlaylistId());
                    context.sendBroadcast(i);
                }
            }).start();
    }

    private void renameDialog(final PlaylistDBHelper helper, final int position) {
        final EditText edittext = new EditText(context);
        AlertDialog.Builder alert = new AlertDialog.Builder(context);
        alert.setTitle("Rename");

        alert.setView(edittext);

        alert.setPositiveButton("Done", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                if (edittext.getText().toString().matches("")) {
                    renameDialog(helper, position);
                } else {
                    helper.renamePlaylist(edittext.getText().toString(),
                            items.get(position).getPlaylistId());
                    updateNewList(helper.getAllPlaylist());
                    notifyDataSetChanged();
                }
            }
        });

        alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
            }
        });

        alert.show();
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

    public void onBackPressed() {
        if (expandedPosition != -1)
            collapse();
        else
            ((MainActivity) fragment.getActivity()).killActivity();
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

}
