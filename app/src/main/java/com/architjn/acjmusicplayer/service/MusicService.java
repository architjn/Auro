package com.architjn.acjmusicplayer.service;

import android.app.ActivityManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.IBinder;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.widget.RemoteViews;
import android.widget.Toast;

import com.architjn.acjmusicplayer.R;
import com.architjn.acjmusicplayer.elements.items.SongListItem;
import com.architjn.acjmusicplayer.task.ChangeNotificationDetails;
import com.architjn.acjmusicplayer.ui.layouts.activity.MusicPlayer;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by architjn on 29/06/15.
 */
public class MusicService extends Service {

    MediaPlayer mediaPlayer;

    private Notification notificationCompat;
    private NotificationManager notificationManager;
    private RemoteViews notiLayoutBig;
    private String songName, songDesc, songPath, songArt, albumName;
    private long albumId;
    private boolean singleSong;
    private int currentPlaylistSongId = -1, pausedSongPlaylistId = -1, pausedSongSeek;
    long currentPlaylistAlbumId = -1;
    private SongListItem pausedSong;

    public static final int NOTIFICATION_ID = 104;
    public static final String ACTION_PLAY = "play";
    public static final String ACTION_PREV = "prev";
    public static final String ACTION_NEXT = "next";
    public static final String ACTION_STOP = "stop";
    public static final String ACTION_PLAY_ALBUM = "player_play_album";
    public static final String ACTION_PLAY_NEXT = "player_play_next";
    public static final String ACTION_REMOVE_SERVICE = "player_remove_service";
    public static final String ACTION_CLEAR_PLAYLIST = "player_clear_playlist";
    public static final String ACTION_PLAY_SINGLE = "play_single_song";
    public static final String ACTION_ADD_SONG = "add_song_to_playlist";
    public static final String ACTION_ADD_SONG_MULTI = "add_song_to_playlist_multi";
    public static final String ACTION_REQUEST_SONG_DETAILS = "player_request_song_details";
    public static final String ACTION_SEEK_TO = "player_seek_to_song";
    public static final String ACTION_SEEK_GET = "player_seek_get_song";
    public static final String ACTION_SHUFFLE_PLAYLIST = "player_shuffle_playlist";

    private ArrayList<SongListItem> playList;

    private BroadcastReceiver musicPlayer = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, final Intent intent) {
            if (intent.getAction().equals(ACTION_PLAY_SINGLE)) {
                playList.clear();
                playMusic(intent.getStringExtra("songPath"), intent.getStringExtra("songName"),
                        intent.getStringExtra("songDesc"), intent.getStringExtra("songArt"),
                        intent.getLongExtra("songAlbumId", 0), intent.getStringExtra("songAlbumName"), true);
                playList.add(new SongListItem(intent.getIntExtra("songId", 0), intent.getStringExtra("songName"), intent.getStringExtra("songDesc"),
                        intent.getStringExtra("songPath"), false,
                        intent.getLongExtra("songAlbumId", 0), intent.getStringExtra("songAlbumName"), 0));
                currentPlaylistSongId = 0;
                pausedSongSeek = 0;
            } else if (intent.getAction().equals(ACTION_PLAY_ALBUM)) {
                playList.clear();
                System.gc();
                Cursor musicCursor;

                String where = MediaStore.Audio.Media.ALBUM_ID + "=?";
                String whereVal[] = {intent.getLongExtra("albumId", 0) + ""};
                String orderBy = MediaStore.Audio.Media._ID;

                musicCursor = getContentResolver().query(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                        null, where, whereVal, orderBy);
                if (musicCursor != null && musicCursor.moveToFirst()) {
                    //get columns
                    int titleColumn = musicCursor.getColumnIndex
                            (android.provider.MediaStore.Audio.Media.TITLE);
                    int idColumn = musicCursor.getColumnIndex
                            (android.provider.MediaStore.Audio.Media._ID);
                    int artistColumn = musicCursor.getColumnIndex
                            (android.provider.MediaStore.Audio.Media.ARTIST);
                    int pathColumn = musicCursor.getColumnIndex
                            (MediaStore.Audio.Media.DATA);
                    int albumIdColumn = musicCursor.getColumnIndex
                            (MediaStore.Audio.Media.ALBUM_ID);
                    int albumNameColumn = musicCursor.getColumnIndex
                            (MediaStore.Audio.Media.ALBUM);
                    int count = 0;
                    do {
                        count++;
                        addSong(new SongListItem(musicCursor.getLong(idColumn),
                                musicCursor.getString(titleColumn),
                                musicCursor.getString(artistColumn),
                                musicCursor.getString(pathColumn), false,
                                musicCursor.getLong(albumIdColumn),
                                musicCursor.getString(albumNameColumn),
                                count), count);
                    }
                    while (musicCursor.moveToNext());
                }
            } else if (intent.getAction().equals(ACTION_CLEAR_PLAYLIST)) {
                if (playList != null) {
                    if (!playList.isEmpty()) {
                        playList.clear();
                    }
                }
            } else if (intent.getAction().equals(ACTION_REMOVE_SERVICE)) {
                MusicService.this.stopSelf();
                if (Build.VERSION.SDK_INT >= 21) {
                    ActivityManager am = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
                    if (am != null) {
                        List<ActivityManager.AppTask> tasks = am.getAppTasks();
                        if (tasks != null) {
                            tasks.get(0).finishAndRemoveTask();
                        }
                    }
                    unregisterReceiver(musicPlayer);
                }
            } else if (intent.getAction().equals(ACTION_NEXT)) {
                pausedSongSeek = 0;
                nextSong();
            } else if (intent.getAction() == ACTION_PREV) {
                if (mediaPlayer.getCurrentPosition() >= 5000) {
                    mediaPlayer.seekTo(0);
                } else {
                    pausedSongSeek = 0;
                    prevSong();
                }
            } else if (intent.getAction().equals(ACTION_REQUEST_SONG_DETAILS)) {
                if (songPath == null) {
                    Toast.makeText(MusicService.this, "Nothing to Play", Toast.LENGTH_SHORT).show();
                } else {
                    Intent sendDetails = new Intent(MusicService.this, MusicPlayer.class);
                    sendDetails.putExtra("songPath", songPath);
                    sendDetails.putExtra("songName", songName);
                    sendDetails.putExtra("songDesc", songDesc);
                    sendDetails.putExtra("songAlbumId", albumId);
                    sendDetails.putExtra("songAlbumName", albumName);
                    try {
                        sendDetails.putExtra("songDuration", mediaPlayer.getDuration());
                        sendDetails.putExtra("songCurrTime", mediaPlayer.getCurrentPosition());
                    } catch (NullPointerException e) {
                        e.printStackTrace();
                    }
                    sendDetails.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(sendDetails);
                }
            } else if (intent.getAction().equals(ACTION_STOP)) {
                if (mediaPlayer != null && mediaPlayer.isPlaying()) {
                    if (!singleSong) {
                        pausedSong = playList.get(currentPlaylistSongId);
                        pausedSongPlaylistId = currentPlaylistSongId;
                    }
                    pausedSongSeek = mediaPlayer.getCurrentPosition();
                    stopMusic();
                } else {
                    if (!singleSong) {
                        playMusic(pausedSongPlaylistId);
                    } else {
                        playMusic(pausedSong);
                    }
                }
                Intent i = new Intent(MusicPlayer.ACTION_GET_PLAY_STATE);
                if (mediaPlayer != null && mediaPlayer.isPlaying())
                    i.putExtra("isPlaying", true);
                sendBroadcast(i);
            } else if (intent.getAction().equals(ACTION_SEEK_TO)) {
                try {
                    mediaPlayer.seekTo(intent.getIntExtra("changeSeek", 0));
                } catch (NullPointerException e) {
                    e.printStackTrace();
                    pausedSongSeek = intent.getIntExtra("changeSeek", 0);
                    playMusic(pausedSong);
                    pausedSongSeek = 0;
                }
            } else if (intent.getAction().equals(ACTION_SEEK_GET)) {
                Intent i = new Intent();
                i.setAction(MusicPlayer.ACTION_GET_SEEK_VALUE);
                if (mediaPlayer != null && mediaPlayer.isPlaying())
                    i.putExtra("isPlaying", true);
                if (mediaPlayer != null)
                    i.putExtra("songSeekVal", mediaPlayer.getCurrentPosition());
                sendBroadcast(i);
                ArrayList<String> name = new ArrayList<>(), desc = new ArrayList<>(), songId = new ArrayList<>();
                Intent playlistIntent = new Intent();
                playlistIntent.setAction(MusicPlayer.ACTION_GET_PLAYING_LIST);
                for (int j = 0; j < playList.size(); j++) {
                    name.add(playList.get(j).getName());
                    desc.add(playList.get(j).getDesc());
                    songId.add(playList.get(j).getId() + "");
                }
                playlistIntent.putStringArrayListExtra("name", name);
                playlistIntent.putStringArrayListExtra("desc", desc);
                playlistIntent.putStringArrayListExtra("songId", songId);
                sendBroadcast(playlistIntent);
            } else if (intent.getAction().equals(ACTION_SHUFFLE_PLAYLIST)) {
                Collections.shuffle(playList);
            } else if (intent.getAction().equals(ACTION_PLAY_NEXT)) {
                if (currentPlaylistSongId == playList.size() - 1) {
                    playList.add(new SongListItem(intent.getIntExtra("songId", 0), intent.getStringExtra("songName"), intent.getStringExtra("songDesc"),
                            intent.getStringExtra("songPath"), false,
                            intent.getLongExtra("songAlbumId", 0), intent.getStringExtra("songAlbumName"), 0));
                } else if (currentPlaylistSongId != -1) {
                    playList.add(currentPlaylistSongId + 1, new SongListItem(intent.getIntExtra("songId", 0), intent.getStringExtra("songName"), intent.getStringExtra("songDesc"),
                            intent.getStringExtra("songPath"), false,
                            intent.getLongExtra("songAlbumId", 0), intent.getStringExtra("songAlbumName"), 0));
                } else {
                    Intent i = intent;
                    i.setAction(ACTION_PLAY_SINGLE);
                    sendBroadcast(i);
                }
            } else if (intent.getAction().matches(ACTION_ADD_SONG)) {
                if (playList.size() != 0 && currentPlaylistSongId != -1) {
                    playList.add(new SongListItem(intent.getIntExtra("songId", 0), intent.getStringExtra("songName"), intent.getStringExtra("songDesc"),
                            intent.getStringExtra("songPath"), false,
                            intent.getLongExtra("songAlbumId", 0), intent.getStringExtra("songAlbumName"), 0));
                } else {
                    Intent i = intent;
                    i.setAction(ACTION_PLAY_SINGLE);
                    sendBroadcast(i);
                }
            }
        }

    };

    public void nextSong() {
        if (currentPlaylistSongId < (playList.size() - 1)) {
            playMusic(currentPlaylistSongId + 1);
        } else {
            playMusic(0);
        }
    }

    public void prevSong() {
        if (currentPlaylistSongId > 0) {
            playMusic(currentPlaylistSongId - 1);
        } else {
            playMusic(playList.size() - 1);
        }
    }

    public void addSong(SongListItem song, int count) {
        playList.add(song);
//        if (count == 1) {
        if (mediaPlayer != null) {
            if (currentPlaylistAlbumId != song.getAlbumId()) {
                pausedSongSeek = 0;
                playMusic(0);
            }
        } else {
            pausedSongSeek = 0;
            playMusic(0);
        }
//        }
    }

    public void stopMusic() {
        if (mediaPlayer != null) {
            mediaPlayer.stop();
            mediaPlayer.release();
            mediaPlayer = null;
            setNotificationPlayer(true);
        }
    }

    public void playMusic(int playingPos) {
        if (playList.size() != 0) {
            playMusic(playList.get(playingPos).getPath(),
                    playList.get(playingPos).getName(),
                    playList.get(playingPos).getDesc(),
                    "", playList.get(playingPos).getAlbumId(),
                    playList.get(playingPos).getAlbumName(), false);
            currentPlaylistSongId = playingPos;
            currentPlaylistAlbumId = playList.get(playingPos).getAlbumId();
        } else {
            Toast.makeText(MusicService.this, "Nothing to play", Toast.LENGTH_SHORT).show();
        }
    }

    public void playMusic(SongListItem song) {
        playMusic(song.getPath(), song.getName(), song.getDesc(), "", song.getAlbumId(), song.getAlbumName(), true);
    }

    public void playMusic(final String songPath, final String songName, final String songDesc,
                          final String songArt, final long albumId, final String albumName, @Nullable boolean singlePlay) {
        if (singlePlay) {
            playList.clear();
            currentPlaylistSongId = -1;
            currentPlaylistAlbumId = -1;
            pausedSong = new SongListItem(0, songName, songDesc, songPath, false, albumId, albumName, 0);
            pausedSongPlaylistId = -1;
            singleSong = true;
        } else {
            singleSong = false;
        }
        try {
            stopMusic();
            mediaPlayer = new MediaPlayer();
            mediaPlayer.setDataSource(songPath);
            mediaPlayer.prepare();
            mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mp) {
                    if (currentPlaylistSongId < playList.size() - 1) {
                        pausedSongSeek = 0;
                        currentPlaylistSongId++;
                        playMusic(playList.get(currentPlaylistSongId).getPath(), playList.get(currentPlaylistSongId).getName(),
                                playList.get(currentPlaylistSongId).getDesc(), "", playList.get(currentPlaylistSongId).getAlbumId(),
                                playList.get(currentPlaylistSongId).getAlbumName(), false);
                    } else {
                        currentPlaylistSongId = -1;
                        currentPlaylistAlbumId = -1;
                        pausedSongPlaylistId = -1;
                        pausedSong = new SongListItem(0, songName, songDesc, songPath, false, albumId, albumName, 0);
                        pausedSongSeek = 0;
                        stopMusic();
                    }
                }
            });
            mediaPlayer.start();
            mediaPlayer.seekTo(pausedSongSeek);
            setNotificationPlayer(false);
            changeNotificationDetails(songPath, songName, songDesc, songArt, albumId, albumName);
            if (Build.VERSION.SDK_INT >= 16) {
                notificationCompat.bigContentView.setImageViewResource(R.id.noti_play_button,
                        R.drawable.ic_pause_white_36dp);
            }
        } catch (IOException e) {
            Toast.makeText(MusicService.this, "File not valid", Toast.LENGTH_SHORT).show();
        }
    }

    private void setNotificationPlayer(boolean stop) {
        if (stop)
            notificationCompat = createBuiderNotificationRemovable().build();
        else
            notificationCompat = createBuiderNotification().build();
        notiLayoutBig = new RemoteViews(getPackageName(), R.layout.notification_layout);
        if (Build.VERSION.SDK_INT >= 16) {
            notificationCompat.bigContentView = notiLayoutBig;
            notificationCompat.bigContentView.setImageViewResource(R.id.noti_play_button,
                    R.drawable.ic_play_arrow_white_36dp);
        }
        notificationCompat.priority = Notification.PRIORITY_MAX;
        notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        notificationManager.notify(NOTIFICATION_ID, notificationCompat);
    }

    public void changeNotificationDetails(String songPathArg, String songNameArg, String songDescArg,
                                          String songArtArg, long albumIdArg, String albumNameArg) {
        this.songName = songNameArg;
        this.albumName = albumNameArg;
        this.songDesc = songDescArg;
        this.songArt = songArtArg;
        this.songPath = songPathArg;
        this.albumId = albumIdArg;

        if (Build.VERSION.SDK_INT >= 16) {
            notificationCompat.bigContentView.setTextViewText(R.id.noti_song_name, songName);
            notificationCompat.bigContentView.setTextViewText(R.id.noti_song_artist, songDesc);
            notificationCompat.bigContentView.setTextViewText(R.id.noti_song_album, albumName);
            Intent playClick = new Intent();
            playClick.setAction(MusicService.ACTION_STOP);
            PendingIntent playClickIntent = PendingIntent.getBroadcast(MusicService.this, 21021, playClick, 0);
            notificationCompat.bigContentView.setOnClickPendingIntent(R.id.noti_play_button, playClickIntent);
            Intent prevClick = new Intent();
            prevClick.setAction(MusicService.ACTION_PREV);
            PendingIntent prevClickIntent = PendingIntent.getBroadcast(MusicService.this, 21121, prevClick, 0);
            notificationCompat.bigContentView.setOnClickPendingIntent(R.id.noti_prev_button, prevClickIntent);
            Intent nextClick = new Intent();
            nextClick.setAction(MusicService.ACTION_NEXT);
            PendingIntent nextClickIntent = PendingIntent.getBroadcast(MusicService.this, 21221, nextClick, 0);
            notificationCompat.bigContentView.setOnClickPendingIntent(R.id.noti_next_button, nextClickIntent);
            notificationManager.notify(NOTIFICATION_ID, notificationCompat);
        }
        new ChangeNotificationDetails(MusicService.this, albumId, notificationManager, notificationCompat).execute();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        playList = new ArrayList<SongListItem>();
        IntentFilter commandFilter = new IntentFilter();
        commandFilter.addAction(ACTION_PLAY);
        commandFilter.addAction(ACTION_STOP);
        commandFilter.addAction(ACTION_PLAY_SINGLE);
        commandFilter.addAction(ACTION_ADD_SONG);
        commandFilter.addAction(ACTION_REQUEST_SONG_DETAILS);
        commandFilter.addAction(ACTION_SEEK_TO);
        commandFilter.addAction(ACTION_SEEK_GET);
        commandFilter.addAction(ACTION_CLEAR_PLAYLIST);
        commandFilter.addAction(ACTION_NEXT);
        commandFilter.addAction(ACTION_PLAY_NEXT);
        commandFilter.addAction(ACTION_PREV);
        commandFilter.addAction(ACTION_REMOVE_SERVICE);
        commandFilter.addAction(ACTION_ADD_SONG_MULTI);
        commandFilter.addAction(ACTION_PLAY_ALBUM);
        commandFilter.addAction(ACTION_SHUFFLE_PLAYLIST);
        registerReceiver(musicPlayer, commandFilter);
        return START_STICKY;
    }

    private NotificationCompat.Builder createBuiderNotification() {
        Intent notificationIntent = new Intent();
        notificationIntent.setAction(MusicService.ACTION_REQUEST_SONG_DETAILS);
        PendingIntent contentIntent = PendingIntent.getBroadcast(MusicService.this, 0, notificationIntent, 0);
        Intent deleteIntent = new Intent();
        deleteIntent.setAction(MusicService.ACTION_REMOVE_SERVICE);
        PendingIntent deletePendingIntent = PendingIntent.getBroadcast(MusicService.this, 0, deleteIntent, 0);
        return new NotificationCompat.Builder(this)
                .setOngoing(true)
                .setSmallIcon(R.drawable.ic_audiotrack_white_24dp)
                .setContentIntent(contentIntent)
                .setDeleteIntent(deletePendingIntent);
    }

    private NotificationCompat.Builder createBuiderNotificationRemovable() {
        Intent notificationIntent = new Intent();
        notificationIntent.setAction(MusicService.ACTION_REQUEST_SONG_DETAILS);
        PendingIntent contentIntent = PendingIntent.getActivity(MusicService.this, 0, notificationIntent, 0);
        Intent deleteIntent = new Intent();
        deleteIntent.setAction(MusicService.ACTION_REMOVE_SERVICE);
        PendingIntent deletePendingIntent = PendingIntent.getBroadcast(MusicService.this, 0, deleteIntent, 0);
        return new NotificationCompat.Builder(this)
                .setOngoing(false)
                .setSmallIcon(R.drawable.ic_audiotrack_white_24dp)
                .setContentIntent(contentIntent)
                .setDeleteIntent(deletePendingIntent);
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

}
