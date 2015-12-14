package com.architjn.acjmusicplayer.utils.adapters;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.architjn.acjmusicplayer.R;
import com.architjn.acjmusicplayer.service.PlayerService;
import com.architjn.acjmusicplayer.ui.layouts.activity.AlbumActivity;
import com.architjn.acjmusicplayer.utils.Utils;
import com.architjn.acjmusicplayer.utils.items.Song;

import java.util.ArrayList;

/**
 * Created by architjn on 28/11/15.
 */
public class AlbumSongListAdapter extends RecyclerView.Adapter<AlbumSongListAdapter.SimpleItemViewHolder> {

    private ArrayList<Song> items;
    private AlbumActivity albumActivity;
    private Context context;

    public AlbumSongListAdapter(Context context, ArrayList<Song> items, AlbumActivity albumActivity) {
        this.context = context;
        this.items = items;
        this.albumActivity = albumActivity;
    }

    @Override
    public AlbumSongListAdapter.SimpleItemViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).
                inflate(R.layout.album_list_item, parent, false);
        return new SimpleItemViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final AlbumSongListAdapter.SimpleItemViewHolder holder, final int position) {
        holder.name.setText(items.get(position).getName());
        holder.count.setText(String.valueOf(position + 1));
        holder.duration.setText(items.get(position).getDuration());
        setOnClickListeners(holder, position);
    }

    private void setOnClickListeners(SimpleItemViewHolder holder, final int position) {
        holder.mainView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new Thread(new Runnable() {
                    public void run() {
                        Intent i = new Intent();
                        i.setAction(PlayerService.ACTION_PLAY_ALBUM);
                        i.putExtra("songPos", position);
                        i.putExtra("albumId", items.get(position).getAlbumId());
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
                        Intent intent = new Intent();
                        switch (item.getItemId()) {
                            case R.id.popup_song_play:
                                intent.setAction(PlayerService.ACTION_PLAY_SINGLE);
                                intent.putExtra("songId", items.get(position).getSongId());
                                context.sendBroadcast(intent);
                                break;
                            case R.id.popup_song_addtoplaylist:
                                new Utils(context).addToPlaylist(albumActivity,
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

    @Override
    public int getItemCount() {
        return items.size();
    }

    public class SimpleItemViewHolder extends RecyclerView.ViewHolder {

        public TextView name, count, duration;
        public View mainView, menu;

        public SimpleItemViewHolder(View itemView) {
            super(itemView);
            mainView = itemView;
            name = (TextView) itemView.findViewById(R.id.album_item_name);
            duration = (TextView) itemView.findViewById(R.id.album_item_duration);
            count = (TextView) itemView.findViewById(R.id.album_item_count);
            menu = itemView.findViewById(R.id.album_item_menu);
        }
    }

}
