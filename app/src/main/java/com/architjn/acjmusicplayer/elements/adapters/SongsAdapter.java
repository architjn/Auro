package com.architjn.acjmusicplayer.elements.adapters;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.provider.MediaStore;
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
import com.architjn.acjmusicplayer.elements.items.SongListItem;
import com.architjn.acjmusicplayer.service.MusicService;

import java.io.File;
import java.util.List;

public class SongsAdapter extends RecyclerView.Adapter<SongsAdapter.SimpleItemViewHolder> {

    private final List<SongListItem> items;
    private Context context;

    public final static class SimpleItemViewHolder extends RecyclerView.ViewHolder {
        public TextView title, desc;
        public View view;
        public ImageView menu;

        public SimpleItemViewHolder(View itemView) {
            super(itemView);
            view = itemView;
            title = (TextView) itemView.findViewById(R.id.song_item_name);
            desc = (TextView) itemView.findViewById(R.id.song_item_desc);
            menu = (ImageView) itemView.findViewById(R.id.song_item_menu);
        }
    }

    public SongsAdapter(Context context, List<SongListItem> items) {
        this.context = context;
        this.items = items;
    }

    @Override
    public SongsAdapter.SimpleItemViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).
                inflate(R.layout.song_list_item, parent, false);
        return new SimpleItemViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(SimpleItemViewHolder holder, final int position) {
        holder.title.setText(items.get(position).getName());
        holder.desc.setText(items.get(position).getDesc());
        holder.menu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PopupMenu popupMenu = new PopupMenu(context, v);
                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        switch (item.getItemId()) {
                            case R.id.menu_play_next:
                                Intent i = new Intent();
                                i.setAction(MusicService.ACTION_PLAY_NEXT);
                                i.putExtra("songId", items.get(position).getId());
                                i.putExtra("songPath", items.get(position).getPath());
                                i.putExtra("songName", items.get(position).getName());
                                i.putExtra("songDesc", items.get(position).getDesc());
                                i.putExtra("songArt", items.get(position).getArt());
                                i.putExtra("songAlbumId", items.get(position).getAlbumId());
                                i.putExtra("songAlbumName", items.get(position).getAlbumName());
                                context.sendBroadcast(i);
                                return true;
                            case R.id.menu_add_playing:
                                Intent a = new Intent();
                                a.setAction(MusicService.ACTION_ADD_SONG);
                                a.putExtra("songId", items.get(position).getId());
                                a.putExtra("songPath", items.get(position).getPath());
                                a.putExtra("songName", items.get(position).getName());
                                a.putExtra("songDesc", items.get(position).getDesc());
                                a.putExtra("songArt", items.get(position).getArt());
                                a.putExtra("songAlbumId", items.get(position).getAlbumId());
                                a.putExtra("songAlbumName", items.get(position).getAlbumName());
                                context.sendBroadcast(a);
                                return true;
                            case R.id.menu_add_playlist:
                                Toast.makeText(context, "Add to playlist", Toast.LENGTH_SHORT).show();
                                return true;
                            case R.id.menu_share:
                                Intent share = new Intent(Intent.ACTION_SEND);
                                share.setType("audio/*");
                                share.putExtra(Intent.EXTRA_STREAM, Uri.parse("file:///" + items.get(position).getPath()));
                                context.startActivity(Intent.createChooser(share, "Share Song"));
                                return true;
                            case R.id.menu_song_editor:
                                Toast.makeText(context, "Song tag editor", Toast.LENGTH_SHORT).show();
                                return true;
                            case R.id.menu_details:
                                Toast.makeText(context, "Details", Toast.LENGTH_SHORT).show();
                                return true;
                            case R.id.menu_set_ringtone:
//                                Uri uri = MediaStore.Audio.Media.getContentUriForPath(items.get(position).getPath());
//                                RingtoneManager.setActualDefaultRingtoneUri(context,
//                                        RingtoneManager.TYPE_RINGTONE, uri);
                                Toast.makeText(context, "Set as Ringtone", Toast.LENGTH_SHORT).show();
                                return true;
                            case R.id.menu_delete:
                                File file = new File(items.get(position).getPath());
                                boolean deleted = file.delete();
                                if (deleted) {
                                    Toast.makeText(context, "Song Deleted", Toast.LENGTH_SHORT).show();
                                    context.getContentResolver().delete(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                                            MediaStore.MediaColumns._ID + "='" + items.get(position).getId() + "'", null);
                                    notifyItemRemoved(position);
                                } else
                                    Toast.makeText(context, "Song Not Deleted", Toast.LENGTH_SHORT).show();
                                return true;
                        }
                        return false;
                    }
                });
                popupMenu.inflate(R.menu.song_popup_menu);
                popupMenu.show();
            }
        });
        holder.view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent a = new Intent();
                a.setAction(MusicService.ACTION_PLAY_SINGLE);
                a.putExtra("songId", items.get(position).getId());
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
