package com.architjn.acjmusicplayer.elements.adapters;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.architjn.acjmusicplayer.R;
import com.architjn.acjmusicplayer.elements.items.SongListItem;
import com.architjn.acjmusicplayer.service.MusicService;

import java.util.List;

/**
 * Created by architjn on 23/06/15.
 */
public class AlbumSongAdapter extends RecyclerView.Adapter<AlbumSongAdapter.SimpleItemViewHolder> {

    private final List<SongListItem> items;
    private Context context;

    public final static class SimpleItemViewHolder extends RecyclerView.ViewHolder {
        public TextView title, desc, counter;
        public View view;

        public SimpleItemViewHolder(View itemView) {
            super(itemView);
            title = (TextView) itemView.findViewById(R.id.album_song_item_name);
            desc = (TextView) itemView.findViewById(R.id.album_song_item_desc);
            counter = (TextView) itemView.findViewById(R.id.album_song_itemcount);
            view = itemView;
        }
    }

    public AlbumSongAdapter(Context context, List<SongListItem> items) {
        this.context = context;
        this.items = items;
    }

    @Override
    public AlbumSongAdapter.SimpleItemViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).
                inflate(R.layout.album_song_list_item, parent, false);
        return new SimpleItemViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(SimpleItemViewHolder holder,final int position) {
        holder.title.setText(items.get(position).getName());
        holder.desc.setText(items.get(position).getDesc());
        holder.counter.setText(items.get(position).getCount() + "");
        holder.view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent a = new Intent();
                a.setAction(MusicService.ACTION_PLAY_SINGLE);
                a.putExtra("songPath", items.get(position).getPath());
                a.putExtra("songName", items.get(position).getName());
                a.putExtra("songDesc", items.get(position).getDesc());
                a.putExtra("songArt", items.get(position).getArt());
                a.putExtra("songAlbumId", items.get(position).getAlbumId());
                a.putExtra("songAlbumName", items.get(position).getAlbumName());
                context.sendBroadcast(a);
            }
        });
    }

    @Override
    public int getItemCount() {
        return this.items.size();
    }
}
