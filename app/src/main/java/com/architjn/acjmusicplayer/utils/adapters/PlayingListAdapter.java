package com.architjn.acjmusicplayer.utils.adapters;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.architjn.acjmusicplayer.R;
import com.architjn.acjmusicplayer.service.PlayerService;
import com.architjn.acjmusicplayer.ui.widget.PointShiftingArrayList;
import com.architjn.acjmusicplayer.utils.ListSongs;
import com.architjn.acjmusicplayer.utils.Utils;
import com.architjn.acjmusicplayer.utils.items.Song;
import com.squareup.picasso.Picasso;

import java.io.File;

/**
 * Created by architjn on 28/11/15.
 */
public class PlayingListAdapter extends RecyclerView.Adapter<PlayingListAdapter.SimpleItemViewHolder> {

    private static final int ITEM_VIEW_TYPE_HEADER = 0;
    private static final int ITEM_VIEW_TYPE_ITEM = 1;
    private static final int ITEM_VIEW_TYPE_UP_NEXT_HEADER = 2;

    private PointShiftingArrayList<Song> items;
    private Context context;
    private View header;
    private int lightColor;
    private int darkColor;

    public PlayingListAdapter(Context context, View header, PointShiftingArrayList<Song> items) {
        this.context = context;
        this.header = header;
        this.items = items;
    }

    public boolean isHeader(int position) {
        return position == 0;
    }

    public boolean isUpNextHeader(int position) {
        return position == 2;
    }

    @Override
    public PlayingListAdapter.SimpleItemViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == ITEM_VIEW_TYPE_HEADER) {
            return new SimpleItemViewHolder(header);
        } else if (viewType == ITEM_VIEW_TYPE_UP_NEXT_HEADER) {
            return new SimpleItemViewHolder(LayoutInflater.from(parent.getContext()).
                    inflate(R.layout.up_next_header, parent, false));
        }
        View itemView = LayoutInflater.from(parent.getContext()).
                inflate(R.layout.songs_list_item, parent, false);
        return new SimpleItemViewHolder(itemView);
    }

    public void setCurrentColor(int lightColor, int darkColor) {
        this.lightColor = lightColor;
        this.darkColor = darkColor;
    }

    @Override
    public void onBindViewHolder(final PlayingListAdapter.SimpleItemViewHolder holder, final int position) {
        if (isHeader(position)) {
            holder.seekHolder.setBackgroundColor(darkColor);
            holder.controlHolder.setBackgroundColor(lightColor);
            return;
        } else if (isUpNextHeader(position)) {
            return;
        }
        holder.name.setText(items.get(getPosition(position)).getName());
        holder.artistName.setText(items.get(getPosition(position)).getArtist());
        holder.img.setPadding(0, 0, 0, 0);
        //Load Image in Background
        Utils utils = new Utils(context);
        if (getPosition(position) == 0) {
            int padding = utils.dpToPx(10);
            holder.img.setPadding(padding, padding, padding, padding);
            Picasso.with(context).load(R.drawable.ic_speaker_48dp).into(holder.img);
        } else {
            String path = ListSongs.getAlbumArt(context,
                    items.get(getPosition(position)).getAlbumId());
            int size = utils.dpToPx(50);
            if (path != null)
                Picasso.with(context).load(new File(path)).resize(size,
                        size).centerCrop().into(holder.img);
            else {
                holder.img.setImageBitmap(utils.getBitmapOfVector(R.drawable.default_art,
                        size, size));
            }
        }
        setOnClick(holder, getPosition(position));
    }

    private void setOnClick(SimpleItemViewHolder holder, final int position) {
        holder.mainView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (position == 0) {
                    new Thread(new Runnable() {
                        public void run() {
                            Intent i = new Intent(PlayerService.ACTION_SEEK_SONG);
                            i.putExtra("seek", 0);
                            context.sendBroadcast(i);
//                            ((PlayerActivity) context).seekBar.setProgress(0);
                        }
                    }).start();
                    return;
                }
                new Thread(new Runnable() {
                    public void run() {
                        Intent i = new Intent();
                        i.setAction(PlayerService.ACTION_CHANGE_SONG);
                        i.putExtra("pos", items.getNormalIndex(position));
                        context.sendBroadcast(i);
                    }
                }).start();
            }
        });
        holder.menu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PopupMenu pm = new PopupMenu(context, view);
                pm.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        switch (item.getItemId()) {
                            case R.id.popup_song_play:
                                new Thread(new Runnable() {
                                    public void run() {
                                        Intent i = new Intent(PlayerService.ACTION_PLAY_SINGLE);
                                        i.putExtra("songId", items.get(position).getSongId());
                                        context.sendBroadcast(i);
                                    }
                                }).start();
                                break;
                            case R.id.popup_song_addtoplaylist:
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

    private int getPosition(int position) {
        if (position > 2)
            return position - 2;
        return position - 1;
    }

    @Override
    public int getItemCount() {
        return items.size() + 2;
    }

    public void setPointOnShifted(int pointOnShifted) {
        items.setPointOnShifted(pointOnShifted);
        notifyDataSetChanged();
    }

    @Override
    public int getItemViewType(int position) {
        if (isHeader(position))
            return ITEM_VIEW_TYPE_HEADER;
        else if (isUpNextHeader(position))
            return ITEM_VIEW_TYPE_UP_NEXT_HEADER;
        else
            return ITEM_VIEW_TYPE_ITEM;
    }

    public class SimpleItemViewHolder extends RecyclerView.ViewHolder {

        public TextView name, artistName;
        public View mainView, menu;
        public ImageView img;

        public View seekHolder, controlHolder;

        public SimpleItemViewHolder(View itemView) {
            super(itemView);
            mainView = itemView;
            img = (ImageView) itemView.findViewById(R.id.song_item_img);
            name = (TextView) itemView.findViewById(R.id.song_item_name);
            menu = itemView.findViewById(R.id.song_item_menu);
            artistName = (TextView) itemView.findViewById(R.id.song_item_artist);

            seekHolder = itemView.findViewById(R.id.control_seek_bar_holder);
            controlHolder = itemView.findViewById(R.id.controller_holder);
        }
    }
}
