package com.architjn.acjmusicplayer.utils.adapters;

import android.animation.ArgbEvaluator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.architjn.acjmusicplayer.R;
import com.architjn.acjmusicplayer.service.PlayerService;
import com.architjn.acjmusicplayer.ui.layouts.activity.MainActivity;
import com.architjn.acjmusicplayer.ui.layouts.fragments.SongsListFragment;
import com.architjn.acjmusicplayer.utils.Utils;
import com.architjn.acjmusicplayer.utils.items.Song;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

/**
 * Created by architjn on 28/11/15.
 */
public class SongListAdapter extends RecyclerView.Adapter<SongListAdapter.SimpleItemViewHolder> {

    private ArrayList<Song> items;
    private int selectedSongId = -1;
    private View selectedView;
    private Context context;
    private SongsListFragment fragment;

    public SongListAdapter(Context context, SongsListFragment fragment, ArrayList<Song> items) {
        this.context = context;
        this.fragment = fragment;
        this.items = items;
    }

    @Override
    public SongListAdapter.SimpleItemViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).
                inflate(R.layout.songs_list_item, parent, false);
        return new SimpleItemViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final SongListAdapter.SimpleItemViewHolder holder, final int position) {
        holder.name.setText(items.get(position).getName());
        holder.artistName.setText(items.get(position).getArtist());
        holder.mainView.setElevation(0);
//        holder.img.setImageDrawable(null);
        //Load Image in Background
//        new SongItemLoader(context, holder, items.get(position).getAlbumId(), dpToPx(50)).execute();
        setAlbumArt(position, holder);
        if (selectedView != null)
            selectedView.setBackgroundColor(context.getResources()
                    .getColor(R.color.appBackground));
        selectedSongId = -1;
        selectedView = null;
        setOnClicks(holder, position);
    }

    private void setAlbumArt(int position, SimpleItemViewHolder holder) {
        final Uri sArtworkUri = Uri
                .parse("content://media/external/audio/albumart/"
                        + items.get(position).getAlbumId());
        Picasso.with(context).load(sArtworkUri).resize(dpToPx(50),
                dpToPx(50)).centerCrop().into(holder.img);
    }

    private void setOnClicks(final SimpleItemViewHolder holder, final int position) {
        holder.mainView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (selectedSongId != position) {
                    //other than focused is clicked
                    if (selectedSongId == -1) {
                        //nothing was focused
                        holder.mainView.setBackgroundColor(Color.parseColor("#ffffff"));
                        animateElevation(0, 12, holder.mainView);
                        selectedView = holder.mainView;
                        selectedSongId = position;
                    } else {
                        //something was focused
                        holder.mainView.setBackgroundColor(Color.parseColor("#ffffff"));
                        animateElevation(0, 12, holder.mainView);
                        selectedView.setBackgroundColor(context.getResources()
                                .getColor(R.color.appBackground));
                        animateElevation(12, 0, selectedView);
                        selectedSongId = position;
                        selectedView = holder.mainView;
                    }
                } else {
                    //focus is clicked again
                    //Play all the songs starting from this
                    new Thread(new Runnable() {
                        public void run() {
                            Intent i = new Intent();
                            i.setAction(PlayerService.ACTION_PLAY_ALL_SONGS);
                            i.putExtra("songId", items.get(position).getSongId());
                            context.sendBroadcast(i);
                        }
                    }).start();
                }
            }
        });
        holder.menu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PopupMenu pm = new PopupMenu(context, view);
                final Intent intent = new Intent();
                pm.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        switch (item.getItemId()) {
                            case R.id.popup_song_play:
                                intent.setAction(PlayerService.ACTION_PLAY_SINGLE);
                                intent.putExtra("songId", items.get(position).getSongId());
                                context.sendBroadcast(intent);
                                break;
                            case R.id.popup_song_addtoplaylist:
                                new Utils(context).addToPlaylist(fragment.getActivity(),
                                        items.get(position).getSongId());
                                break;
                        }
                        return false;
                    }
                });
                pm.inflate(R.menu.popup_song);
                pm.show();
            }
        });
    }

    public int dpToPx(int dp) {
        DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
        int px = Math.round(dp * (displayMetrics.xdpi / DisplayMetrics.DENSITY_DEFAULT));
        return px;
    }

    @Override
    public void onViewRecycled(SimpleItemViewHolder holder) {
        super.onViewRecycled(holder);
        holder.img.setImageDrawable(null);
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public class SimpleItemViewHolder extends RecyclerView.ViewHolder {

        public TextView name, artistName;
        public View mainView, menu;
        public ImageView img;

        public SimpleItemViewHolder(View itemView) {
            super(itemView);
            mainView = itemView;
            img = (ImageView) itemView.findViewById(R.id.song_item_img);
            name = (TextView) itemView.findViewById(R.id.song_item_name);
            menu = itemView.findViewById(R.id.song_item_menu);
            artistName = (TextView) itemView.findViewById(R.id.song_item_artist);
        }
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

    public void recyclerScrolled() {
        if (selectedView != null && selectedSongId != -1) {
            animateElevation(12, 0, selectedView);
            selectedSongId = -1;
            selectedView.setBackgroundColor(context.getResources()
                    .getColor(R.color.appBackground));
        }
    }

    public void onBackPressed() {
        if (selectedSongId != -1) {
            animateElevation(12, 0, selectedView);
            selectedView.setBackgroundColor(context.getResources()
                    .getColor(R.color.appBackground));
            selectedSongId = -1;
            selectedView = null;
        } else {
            if ((fragment.getActivity()) != null)
                ((MainActivity) fragment.getActivity()).killActivity();
        }
    }


}
