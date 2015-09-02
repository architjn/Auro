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
import android.widget.Toast;

import com.architjn.acjmusicplayer.R;
import com.architjn.acjmusicplayer.service.MusicService;

import java.util.List;

/**
 * Created by architjn on 23/06/15.
 */
public class PlayingSongAdapter extends RecyclerView.Adapter<PlayingSongAdapter.SimpleItemViewHolder> {

    private final List<String> name, desc, songId;
    private Context context;

    public final static class SimpleItemViewHolder extends RecyclerView.ViewHolder {
        public TextView title, desc;
        public View view;
        public ImageView menu;

        public SimpleItemViewHolder(View itemView) {
            super(itemView);
            title = (TextView) itemView.findViewById(R.id.song_item_name);
            desc = (TextView) itemView.findViewById(R.id.song_item_desc);
            view = itemView;
            menu = (ImageView) itemView.findViewById(R.id.song_item_menu);
        }
    }

    public PlayingSongAdapter(Context context, List<String> name, List<String> desc, List<String> songId) {
        this.context = context;
        this.name = name;
        this.desc = desc;
        this.songId = songId;
    }

    @Override
    public PlayingSongAdapter.SimpleItemViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).
                inflate(R.layout.song_list_item, parent, false);
        return new SimpleItemViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(SimpleItemViewHolder holder, final int position) {
        holder.title.setText(name.get(position));
        holder.desc.setText(desc.get(position));
        holder.view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent a = new Intent();
                a.setAction(MusicService.ACTION_PLAY_FROM_PLAYLIST);
                a.putExtra("playListId", songId.get(position) + "");
                context.sendBroadcast(a);
            }
        });

        holder.menu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PopupMenu popupMenu = new PopupMenu(context, v);
                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        switch (item.getItemId()) {
                            case R.id.menu_play_next:
                                Intent a = new Intent();
                                a.setAction(MusicService.ACTION_MENU_FROM_PLAYLIST);
                                a.putExtra("count", position);
                                a.putExtra("action", MusicService.ACTION_MENU_PLAY_NEXT);
                                context.sendBroadcast(a);
                                return true;
                            case R.id.menu_remove_playing:
                                Intent b = new Intent();
                                b.setAction(MusicService.ACTION_MENU_FROM_PLAYLIST);
                                b.putExtra("count", position);
                                b.putExtra("action", MusicService.ACTION_MENU_REMOVE_FROM_QUEUE);
                                context.sendBroadcast(b);
                                notifyItemRemoved(position);
                                return true;
                            case R.id.menu_add_playlist:
                                Toast.makeText(context, "Add to playlist", Toast.LENGTH_SHORT).show();
                                return true;
                            case R.id.menu_share:
                                Intent c = new Intent();
                                c.setAction(MusicService.ACTION_MENU_FROM_PLAYLIST);
                                c.putExtra("count", position);
                                c.putExtra("action", MusicService.ACTION_MENU_SHARE);
                                context.sendBroadcast(c);
                                return true;
                            case R.id.menu_song_editor:
                                Toast.makeText(context, "Song tag editor", Toast.LENGTH_SHORT).show();
                                return true;
                            case R.id.menu_details:
                                Toast.makeText(context, "Details", Toast.LENGTH_SHORT).show();
                                return true;
                            case R.id.menu_set_ringtone:
                                Toast.makeText(context, "Set as Ringtone", Toast.LENGTH_SHORT).show();
                                return true;
                            case R.id.menu_delete:
                                Intent d = new Intent();
                                d.setAction(MusicService.ACTION_MENU_FROM_PLAYLIST);
                                d.putExtra("count", position);
                                d.putExtra("action", MusicService.ACTION_MENU_DELETE);
                                context.sendBroadcast(d);
                                notifyItemRemoved(position);
                                return true;
                        }
                        return false;
                    }
                });
                popupMenu.inflate(R.menu.playing_popup_menu);
                popupMenu.show();
            }
        });
    }

    @Override
    public int getItemCount() {
        return this.name.size();
    }
}
