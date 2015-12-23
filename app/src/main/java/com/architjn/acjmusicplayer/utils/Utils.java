package com.architjn.acjmusicplayer.utils;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.media.MediaExtractor;
import android.media.MediaFormat;
import android.media.RingtoneManager;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.StringRes;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.text.Spanned;
import android.util.DisplayMetrics;
import android.view.MenuItem;
import android.widget.Toast;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.architjn.acjmusicplayer.R;
import com.architjn.acjmusicplayer.service.PlayerService;
import com.architjn.acjmusicplayer.ui.layouts.activity.AlbumActivity;
import com.architjn.acjmusicplayer.ui.layouts.activity.ArtistActivity;
import com.architjn.acjmusicplayer.utils.adapters.AddToPlaylistDialogListAdapter;
import com.architjn.acjmusicplayer.utils.items.Song;
import com.cocosw.bottomsheet.BottomSheet;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

/**
 * Created by architjn on 09/12/15.
 */
public class Utils {

    private final PlaylistDBHelper playlistDbHelper;
    private Context context;

    public Utils(Context context) {
        this.context = context;
        playlistDbHelper = new PlaylistDBHelper(context);
    }

    public int dpToPx(int dp) {
        DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
        int px = Math.round(dp * (displayMetrics.xdpi / DisplayMetrics.DENSITY_DEFAULT));
        return px;
    }

    public void addToPlaylist(final Activity activity, final long songId) {
        final AddToPlaylistDialogListAdapter adapter = new AddToPlaylistDialogListAdapter(context,
                playlistDbHelper.getAllPlaylist(), songId);
        RecyclerView rv = (RecyclerView) activity.getLayoutInflater()
                .inflate(R.layout.addtoplaylist, null);
        rv.setLayoutManager(new LinearLayoutManager(context));
        rv.addItemDecoration(new SimpleDividerItemDecoration(context, 0));
        rv.setAdapter(adapter);
        MaterialDialog dialog = new MaterialDialog.Builder(context)
                .title(R.string.add_to_playlist)
                .customView(rv, false)
                .positiveText(R.string.new_playlist)
                .negativeText(R.string.close)
                .onPositive(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog materialDialog, @NonNull DialogAction dialogAction) {
                        newPlaylistDialog(activity, songId);
                    }
                })
                .onNegative(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog materialDialog, @NonNull DialogAction dialogAction) {
                        materialDialog.dismiss();
                    }
                })
                .show();
        adapter.setDialog(dialog);
    }

    private void newPlaylistDialog(final Activity activity, final long songId) {
        new MaterialDialog.Builder(context)
                .title(R.string.new_playlist)
                .input(null, null, new MaterialDialog.InputCallback() {
                    @Override
                    public void onInput(MaterialDialog dialog, CharSequence input) {
                        if (!input.toString().matches("")) {
                            new PlaylistDBHelper(context).createPlaylist(input.toString());
                            addToPlaylist(activity, songId);
                        }
                    }
                }).show();
    }

    public void handleSongMenuClick(final MenuItem item, final ArrayList<Song> items,
                                    final Intent intent, final int position, Activity activity,
                                    PermissionChecker permissionChecker) {
        switch (item.getItemId()) {
            case R.id.popup_song_play:
                intent.setAction(PlayerService.ACTION_PLAY_SINGLE);
                intent.putExtra("songId", items.get(position).getSongId());
                context.sendBroadcast(intent);
                break;
            case R.id.popup_song_addtoplaylist:
                addToPlaylist(activity,
                        items.get(position).getSongId());
                break;
            case R.id.popup_song_add_playing_queue:
                intent.setAction(PlayerService.ACTION_ADD_QUEUE);
                intent.putExtra("songId", items.get(position).getSongId());
                context.sendBroadcast(intent);
                break;
            case R.id.popup_song_open_album:
                intent.setClass(context, AlbumActivity.class);
                intent.putExtra("albumId", items.get(position).getAlbumId());
                intent.putExtra("albumName", items.get(position).getAlbumName());
                context.startActivity(intent);
                break;
            case R.id.popup_song_open_artist:
                intent.setClass(context, ArtistActivity.class);
                intent.putExtra("name", items.get(position).getArtist());
                intent.putExtra("id", ListSongs.getArtistIdFromName(context,
                        items.get(position).getArtist()));
                context.startActivity(intent);
                break;
            case R.id.popup_song_share:
                new BottomSheet.Builder(activity)
                        .title(R.string.share_as).sheet(R.menu.share_bottom_sheet)
                        .listener(new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                switch (which) {
                                    case R.id.share_file:
                                        shareSongFile(items, position);
                                        break;
                                    case R.id.share_text:
                                        shareSongText(items, position);
                                        break;
                                }
                            }
                        }).show();
                break;
            case R.id.popup_song_details:
                try {
                    showSongDetailDialog(items, position);
                } catch (IllegalArgumentException e) {
                    e.printStackTrace();
                }
                break;
            case R.id.popup_song_delete:
                File songFile = new File(items.get(position).getPath());
                if (songFile.delete()) {
                    Toast.makeText(context, R.string.success, Toast.LENGTH_SHORT).show();
                }
                context.getContentResolver().delete(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                        MediaStore.MediaColumns._ID + "='" + items.get(position).getSongId() + "'", null);
                break;
//            case R.id.popup_song_set_as_ringtone:
//                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
//                    if (Settings.System.canWrite(context)) {
//                        setAsRingtone(items.get(position).getPath());
//                        Toast.makeText(context, R.string.success, Toast.LENGTH_SHORT).show();
//                    } else {
//                        new MaterialDialog.Builder(context).title(R.string.ringtone_permission_title)
//                                .content(R.string.ringtone_permission_content)
//                                .positiveText(R.string.ok)
//                                .negativeText(R.string.cancel)
//                                .onPositive(new MaterialDialog.SingleButtonCallback() {
//                                    @Override
//                                    public void onClick(@NonNull MaterialDialog materialDialog,
//                                                        @NonNull DialogAction dialogAction) {
//                                        intent.setAction(android.provider.Settings.ACTION_MANAGE_WRITE_SETTINGS);
//                                        intent.setData(Uri.parse("package:" + context.getPackageName()));
//                                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//                                        context.startActivity(intent);
//                                    }
//                                })
//                                .onNegative(new MaterialDialog.SingleButtonCallback() {
//                                    @Override
//                                    public void onClick(@NonNull MaterialDialog materialDialog, @NonNull DialogAction dialogAction) {
//                                        materialDialog.dismiss();
//                                    }
//                                }).show();
//                    }
//                else {
//                    setAsRingtone(items.get(position).getPath());
//                }
//                break;
        }
    }

    private void setAsRingtone(String filepath) {
        File ringtoneFile = new File(filepath);

        ContentValues content = new ContentValues();
        content.put(MediaStore.MediaColumns.DATA, ringtoneFile.getAbsolutePath());
        content.put(MediaStore.MediaColumns.TITLE, ringtoneFile.getName());
        content.put(MediaStore.MediaColumns.SIZE, 215454);
        content.put(MediaStore.MediaColumns.MIME_TYPE, "audio/*");
        content.put(MediaStore.Audio.Media.DURATION, 230);
        content.put(MediaStore.Audio.Media.IS_RINGTONE, true);
        content.put(MediaStore.Audio.Media.IS_NOTIFICATION, true);
        content.put(MediaStore.Audio.Media.IS_ALARM, true);
        content.put(MediaStore.Audio.Media.IS_MUSIC, true);

        Uri uri = MediaStore.Audio.Media.getContentUriForPath(
                ringtoneFile.getAbsolutePath());


//        context.getContentResolver().delete(uri, MediaStore.MediaColumns.DATA + "=\"" + ringtoneFile.getAbsolutePath() + "\"",
//                null);
        Uri newUri = context.getContentResolver().insert(uri, content);
        RingtoneManager.setActualDefaultRingtoneUri(
                context, RingtoneManager.TYPE_RINGTONE,
                newUri);
    }

    private void showSongDetailDialog(ArrayList<Song> items, int position) {
        MediaExtractor mex = new MediaExtractor();
        try {
            mex.setDataSource(items.get(position).getPath());
        } catch (IOException e) {
            e.printStackTrace();
        }

        MediaFormat mf = mex.getTrackFormat(0);

        int bitRate = mf.getInteger(MediaFormat.KEY_BIT_RATE);
        int sampleRate = mf.getInteger(MediaFormat.KEY_SAMPLE_RATE);
        String mime = mf.getString(MediaFormat.KEY_MIME);
        File songFile = new File(items.get(position).getPath());
        float file_size = (songFile.length() / 1024);
        StringBuilder content = new StringBuilder();
        content.append(getString(R.string.song_name));
        content.append(items.get(position).getName());
        content.append("\n\n");
        content.append(getString(R.string.album_name));
        content.append(items.get(position).getAlbumName());
        content.append("\n\n");
        content.append(getString(R.string.artist_name));
        content.append(items.get(position).getArtist());
        content.append("\n\n");
        content.append(getString(R.string.file_path));
        content.append(items.get(position).getPath());
        content.append("\n\n");
        content.append(getString(R.string.file_name));
        content.append(songFile.getName());
        content.append("\n\n");
        content.append(getString(R.string.format));
        content.append(mime);
        content.append("\n\n");
        content.append(getString(R.string.file_size));
        content.append(String.valueOf(String.format("%.2f", file_size / 1024)));
        content.append(" MB");
        content.append("\n\n");
        content.append(getString(R.string.bitrate));
        content.append(String.valueOf(bitRate / 1000));
        content.append(" kb/s");
        content.append("\n\n");
        content.append(getString(R.string.samplingrate));
        content.append(sampleRate);
        content.append(" Hz");
        new MaterialDialog.Builder(context)
                .title(R.string.details)
                .content(content.toString())
                .positiveText(R.string.ok)
                .onPositive(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog materialDialog,
                                        @NonNull DialogAction dialogAction) {
                        materialDialog.dismiss();
                    }
                })
                .show();
    }

    private Spanned getString(@StringRes int string) {
        return Html.fromHtml(context.getResources().getString(string));
    }

    private void shareSongText(ArrayList<Song> items, int position) {
        String shareBody = context.getResources().getString(R.string.currently_listening_to) +
                items.get(position).getName() +
                context.getResources().getString(R.string.by) +
                items.get(position).getArtist();
        Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND);
        sharingIntent.setType("text/plain");
        sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, shareBody);
        context.startActivity(Intent.createChooser(sharingIntent,
                context.getResources().getString(R.string.share_using)));
    }

    private void shareSongFile(ArrayList<Song> items, int position) {
        Intent share = new Intent(Intent.ACTION_SEND);
        share.setType("audio/*");
        share.putExtra(Intent.EXTRA_STREAM,
                Uri.parse("file:///" + items.get(position).getPath()));
        context.startActivity(Intent.createChooser(share,
                context.getString(R.string.share_song_file)));
    }

}
