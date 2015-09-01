package com.architjn.acjmusicplayer.elements.adapters;

import android.content.Context;
import android.content.DialogInterface;
import android.os.CountDownTimer;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.architjn.acjmusicplayer.R;
import com.architjn.acjmusicplayer.elements.MySQLiteHelper;
import com.architjn.acjmusicplayer.elements.items.Playlist;

import java.util.List;

public class PlaylistAdapter extends RecyclerView.Adapter<PlaylistAdapter.SimpleItemViewHolder> {

    private final List<Playlist> items;
    private Context context;

    public final static class SimpleItemViewHolder extends RecyclerView.ViewHolder {
        public TextView gridName;
        public ImageView gridArt, overflow;
        public View realBackground, mainView;

        public SimpleItemViewHolder(View view) {
            super(view);

            gridName = (TextView) view.findViewById(R.id.grid_name);
            gridArt = (ImageView) view.findViewById(R.id.grid_art);
            overflow = (ImageView) view.findViewById(R.id.grid_menu);
            mainView = view;
            realBackground = view.findViewById(R.id.real_background);
        }
    }

    public PlaylistAdapter(Context context, List<Playlist> items) {
        this.context = context;
        this.items = items;
    }

    @Override
    public PlaylistAdapter.SimpleItemViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).
                inflate(R.layout.playlist_grid, parent, false);


        return new SimpleItemViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final SimpleItemViewHolder holder, final int position) {
        holder.gridName.setText(items.get(position).getName());
        if (items.get(position).getId() == -1) {
            holder.overflow.setVisibility(View.GONE);
            holder.mainView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    showNewPlaylistPrompt();
                }
            });
        } else {
            holder.overflow.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    PopupMenu popupMenu = new PopupMenu(context, v);
                    popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                        @Override
                        public boolean onMenuItemClick(MenuItem item) {
                            switch (item.getItemId()) {
                                case R.id.menu_playlist_delete:
                                    MySQLiteHelper helper = new MySQLiteHelper(context);
                                    helper.removePlayList(items.get(position).getId());
                                    items.remove(position);
                                    notifyItemRemoved(position);
                                    new CountDownTimer(400, 1000) {

                                        public void onTick(long millisUntilFinished) {
                                        }

                                        public void onFinish() {
                                            notifyDataSetChanged();
                                        }

                                    }.start();
                                    return true;
                                case R.id.menu_playlist_play:
                                    Toast.makeText(context, "play PlayList", Toast.LENGTH_SHORT).show();
                                    return true;
                                case R.id.menu_playlist_rename:
                                    showRenamePlaylistPrompt(position);
                                    return true;
                            }
                            return false;
                        }
                    });
                    popupMenu.inflate(R.menu.playlist_popup_menu);
                    popupMenu.show();
                }
            });
            holder.mainView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                }
            });
        }
    }


    protected void showNewPlaylistPrompt() {
        LayoutInflater layoutInflater = LayoutInflater.from(context);
        View promptView = layoutInflater.inflate(R.layout.input_dialog, null);
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);
        alertDialogBuilder.setView(promptView);
        alertDialogBuilder.setTitle("Enter name");
        final EditText editText = (EditText) promptView.findViewById(R.id.edittext);
        alertDialogBuilder.setCancelable(false)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        if (!editText.getText().equals("")) {
                            MySQLiteHelper helper = new MySQLiteHelper(context);
                            items.add(items.size() - 1, new Playlist(helper.createNewPlayList(
                                    editText.getText().toString()), editText.getText().toString()));
                            notifyItemInserted(items.size() - 2);
                        } else
                            Toast.makeText(context, "Enter some name first", Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton("Cancel",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                            }
                        });

        AlertDialog alert = alertDialogBuilder.create();
        alert.show();
    }

    protected void showRenamePlaylistPrompt(final int pos) {
        LayoutInflater layoutInflater = LayoutInflater.from(context);
        View promptView = layoutInflater.inflate(R.layout.input_dialog, null);
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);
        alertDialogBuilder.setView(promptView);
        alertDialogBuilder.setTitle("Enter new name");
        final EditText editText = (EditText) promptView.findViewById(R.id.edittext);
        alertDialogBuilder.setCancelable(false)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        if (!editText.getText().equals("")) {
                            MySQLiteHelper helper = new MySQLiteHelper(context);
                            helper.renamePlaylist(editText.getText().toString(), items.get(pos).getId());
                            items.get(pos).setName(editText.getText().toString());
                            notifyItemChanged(pos);
                        } else
                            Toast.makeText(context, "Enter some name first", Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton("Cancel",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                            }
                        });

        AlertDialog alert = alertDialogBuilder.create();
        alert.show();
    }


    @Override
    public int getItemCount() {
        return this.items.size();
    }
}

