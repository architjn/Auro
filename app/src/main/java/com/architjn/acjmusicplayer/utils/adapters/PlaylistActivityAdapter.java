package com.architjn.acjmusicplayer.utils.adapters;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.CountDownTimer;
import android.provider.MediaStore;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;

import com.architjn.acjmusicplayer.R;
import com.architjn.acjmusicplayer.service.MusicService;
import com.architjn.acjmusicplayer.utils.MySQLiteHelper;
import com.architjn.acjmusicplayer.utils.items.SongListItem;

import java.io.File;
import java.util.List;


public class PlaylistActivityAdapter extends RecyclerView.Adapter<PlaylistActivityAdapter.MainViewHolder> {

    public static final String TAG = PlaylistActivityAdapter.class.getSimpleName();
    private Context context;
    private final List<SongListItem> data;
    private int playlistId;

    public PlaylistActivityAdapter(Context context, List<SongListItem> data, int playlistId) {
        this.data = data;
        this.context = context;
        this.playlistId = playlistId;
    }

    @Override
    public MainViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.song_list_item, parent, false);
        MainViewHolder holder = new MainViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(final MainViewHolder holder, final int position) {
        holder.songName.setText(data.get(position).getName());
        holder.songDesc.setText(data.get(position).getDesc());
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
                                i.putExtra("songId", data.get(position).getId());
                                i.putExtra("songPath", data.get(position).getPath());
                                i.putExtra("songName", data.get(position).getName());
                                i.putExtra("songDesc", data.get(position).getDesc());
                                i.putExtra("songArt", data.get(position).getArt());
                                i.putExtra("songAlbumId", data.get(position).getAlbumId());
                                i.putExtra("songAlbumName", data.get(position).getAlbumName());
                                context.sendBroadcast(i);
                                return true;
                            case R.id.menu_add_playing:
                                Intent a = new Intent();
                                a.setAction(MusicService.ACTION_ADD_SONG);
                                a.putExtra("songId", data.get(position).getId());
                                a.putExtra("songPath", data.get(position).getPath());
                                a.putExtra("songName", data.get(position).getName());
                                a.putExtra("songDesc", data.get(position).getDesc());
                                a.putExtra("songArt", data.get(position).getArt());
                                a.putExtra("songAlbumId", data.get(position).getAlbumId());
                                a.putExtra("songAlbumName", data.get(position).getAlbumName());
                                context.sendBroadcast(a);
                                return true;
                            case R.id.menu_remove_playlist:
                                MySQLiteHelper helper = new MySQLiteHelper(context);
                                helper.removeSong(data.get(position).getId(), playlistId);
                                data.remove(position);
                                notifyItemRemoved(position);
                                updateListWithInterval();
                                return true;
                            case R.id.menu_mood:
                                setMood(position);
                                return true;
                            case R.id.menu_share:
                                Intent share = new Intent(Intent.ACTION_SEND);
                                share.setType("audio/*");
                                share.putExtra(Intent.EXTRA_STREAM, Uri.parse("file:///" + data.get(position).getPath()));
                                context.startActivity(Intent.createChooser(share, "Share Song"));
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
                                File file = new File(data.get(position).getPath());
                                boolean deleted = file.delete();
                                if (deleted) {
                                    Toast.makeText(context, "Song Deleted", Toast.LENGTH_SHORT).show();
                                    context.getContentResolver().delete(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                                            MediaStore.MediaColumns._ID + "='" + data.get(position).getId() + "'", null);
                                    data.remove(position);
                                    notifyItemRemoved(position);
                                    updateListWithInterval();
                                } else
                                    Toast.makeText(context, "Song Not Deleted", Toast.LENGTH_SHORT).show();
                                return true;
                        }
                        return false;
                    }
                });
                popupMenu.inflate(R.menu.play_list_popup_menu);
                popupMenu.show();
            }
        });
        holder.view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent a = new Intent();
                a.setAction(MusicService.ACTION_PLAY_SINGLE);
                a.putExtra("songId", data.get(position).getId());
                a.putExtra("songPath", data.get(position).getPath());
                a.putExtra("songName", data.get(position).getName());
                a.putExtra("songDesc", data.get(position).getDesc());
                a.putExtra("songArt", data.get(position).getArt());
                a.putExtra("songAlbumId", data.get(position).getAlbumId());
                a.putExtra("songAlbumName", data.get(position).getAlbumName());
                context.sendBroadcast(a);
            }
        });
    }


    private void setMood(int position) {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);
        alertDialogBuilder.setTitle("Choose mood");
        View view = ((Activity) context).getLayoutInflater().inflate(R.layout.dialog_listview, null);
        com.architjn.acjmusicplayer.utils.Mood mood = new com.architjn.acjmusicplayer.utils.Mood();
        List<String> moods = mood.getAllMoods();
        RecyclerView gv = (RecyclerView) view.findViewById(R.id.dialog_playlist_rv);
        LinearLayoutManager gridLayoutManager = new LinearLayoutManager(context);
        gridLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        gridLayoutManager.scrollToPosition(0);
        gv.setLayoutManager(gridLayoutManager);
        gv.setHasFixedSize(true);
        alertDialogBuilder.setView(view);
        alertDialogBuilder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        alertDialogBuilder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        AlertDialog dialog = alertDialogBuilder.create();
        DialogMoodAdapter adapter = new DialogMoodAdapter(context, moods, data.get(position), dialog);
        gv.setAdapter(adapter);
        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(dialog.getWindow().getAttributes());
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.height = WindowManager.LayoutParams.MATCH_PARENT;
        dialog.show();
    }

    private void updateListWithInterval() {
        new CountDownTimer(400, 1000) {
            public void onTick(long millisUntilFinished) {
            }

            public void onFinish() {
                notifyDataSetChanged();
            }
        }.start();
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    static class MainViewHolder extends RecyclerView.ViewHolder {

        View view;
        TextView songName, songDesc;
        public View menu;

        public MainViewHolder(View itemView) {
            super(itemView);
            view = itemView;
            songName = (TextView) itemView.findViewById(R.id.song_item_name);
            songDesc = (TextView) itemView.findViewById(R.id.song_item_desc);
            menu = itemView.findViewById(R.id.song_item_menu);
        }
    }
}