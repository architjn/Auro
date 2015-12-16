package com.architjn.acjmusicplayer.utils.adapters;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;
import com.architjn.acjmusicplayer.R;
import com.architjn.acjmusicplayer.service.PlayerService;
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
    private MaterialDialog dialog;

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
        holder.mainView.setBackgroundColor(0xffffffff);
        holder.name.setText(items.get(position).getPlaylistName());
        holder.count.setText(context.getResources().getString(R.string.songs)
                + " " + items.get(position).getSongCount());
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
                                break;
                        }
                        return false;
                    }
                });
                pm.inflate(R.menu.popup_playlist);
                pm.show();
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

    public void updateNewList(ArrayList<Playlist> newList) {
        items = newList;
        notifyDataSetChanged();
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

    public void setDialog(MaterialDialog dialog) {
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
