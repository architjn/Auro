package com.architjn.acjmusicplayer.utils.adapters;

import android.content.Context;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.architjn.acjmusicplayer.R;
import com.architjn.acjmusicplayer.utils.PlaylistDBHelper;
import com.architjn.acjmusicplayer.utils.items.Playlist;

import java.util.ArrayList;

/**
 * Created by architjn on 28/11/15.
 */
public class AddToPlaylistDialogListAdapter extends RecyclerView.Adapter<AddToPlaylistDialogListAdapter.SimpleItemViewHolder> {

    private ArrayList<Playlist> items;
    private long songId;
    private Context context;
    private AlertDialog dialog;

    public AddToPlaylistDialogListAdapter(Context context, ArrayList<Playlist> items, long songId) {
        this.context = context;
        this.items = items;
        this.songId = songId;
    }

    @Override
    public AddToPlaylistDialogListAdapter.SimpleItemViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).
                inflate(R.layout.playlist_dialog_item, parent, false);
        return new SimpleItemViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final AddToPlaylistDialogListAdapter.SimpleItemViewHolder holder, final int position) {
        holder.name.setText(items.get(position).getPlaylistName());
        holder.count.setText(context.getResources().getString(R.string.songs)
                + " " + items.get(position).getSongCount());
        holder.menu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });
        holder.mainView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PlaylistDBHelper helper = new PlaylistDBHelper(context);
                helper.addSong((int) songId,
                        items.get(position).getPlaylistId());
                items = helper.getAllPlaylist();
                notifyDataSetChanged();
                dialog.dismiss();
                Toast.makeText(context, R.string.added_to_playlist, Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void setDialog(AlertDialog dialog) {
        this.dialog = dialog;
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public void updateList(ArrayList<Playlist> allPlaylist) {
        this.items = allPlaylist;
        notifyDataSetChanged();
    }

    public class SimpleItemViewHolder extends RecyclerView.ViewHolder {

        public View mainView, menu;
        public TextView name, count;

        public SimpleItemViewHolder(View itemView) {
            super(itemView);
            mainView = itemView;
            name = (TextView) itemView.findViewById(R.id.playlist_dialog_name);
            menu = itemView.findViewById(R.id.playlist_dialog_menu);
            count = (TextView) itemView.findViewById(R.id.playlist_dialog_song_count);
        }
    }

}
