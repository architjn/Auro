package com.architjn.acjmusicplayer.elements.adapters;

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
                                Toast.makeText(context, "PLay next", Toast.LENGTH_SHORT).show();
                                return true;
                            case R.id.menu_add_playing:
                                Toast.makeText(context, "Add to playing", Toast.LENGTH_SHORT).show();
                                return true;
                            case R.id.menu_add_playlist:
                                Toast.makeText(context, "Add to playlist", Toast.LENGTH_SHORT).show();
                                return true;
                            case R.id.menu_share:
                                Toast.makeText(context, "Share", Toast.LENGTH_SHORT).show();
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
                                Toast.makeText(context, "Delete from Device", Toast.LENGTH_SHORT).show();
                                return true;
                        }
                        return false;
                    }
                });
                popupMenu.inflate(R.menu.song_popup_menu);
                popupMenu.show();
            }
        });
    }

    @Override
    public int getItemCount() {
        return this.name.size();
    }
}
