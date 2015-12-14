package com.architjn.acjmusicplayer.service;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.MediaPlayer;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.widget.Toast;

import com.architjn.acjmusicplayer.R;
import com.architjn.acjmusicplayer.ui.layouts.activity.PlayerActivity;
import com.architjn.acjmusicplayer.utils.PlayerHandler;

import java.io.IOException;

/**
 * Created by architjn on 11/12/15.
 */
public class PlayerService extends Service {

    private static final String TAG = "PlayerService-TAG";
    private BroadcastReceiver playerServiceBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            try {
                handleBroadcastReceived(context, intent);
            } catch (IOException e) {
                e.printStackTrace();
                Toast.makeText(PlayerService.this, R.string.cant_play_song, Toast.LENGTH_SHORT).show();
            }
        }
    };

    private PlayerHandler musicPlayerHandler;
    private Context context;

    public static final String ACTION_PLAY_SINGLE = "ACTION_PLAY_SINGLE";
    public static final String ACTION_PLAY_ALL_SONGS = "ACTION_PLAY_ALL_SONGS";
    public static final String ACTION_PLAY_ALBUM = "ACTION_PLAY_ALBUM";
    public static final String ACTION_PLAY_PLAYLIST = "ACTION_PLAY_PLAYLIST";
    public static final String ACTION_PLAY_ARTIST = "ACTION_PLAY_ARTIST";
    public static final String ACTION_GET_SONG = "ACTION_GET_SONG";
    public static final String ACTION_CHANGE_SONG = "ACTION_CHANGE_SONG";
    public static final String ACTION_SEEK_SONG = "ACTION_SEEK_SONG";
    public static final String ACTION_NEXT_SONG = "ACTION_NEXT_SONG";
    public static final String ACTION_PREV_SONG = "ACTION_PREV_SONG";
    public static final String ACTION_PAUSE_SONG = "ACTION_PAUSE_SONG";


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        context = this;
        musicPlayerHandler = new PlayerHandler(context, this);
        IntentFilter filter = new IntentFilter();
        filter.addAction(ACTION_PLAY_SINGLE);
        filter.addAction(ACTION_PLAY_ALL_SONGS);
        filter.addAction(ACTION_PLAY_ALBUM);
        filter.addAction(ACTION_GET_SONG);
        filter.addAction(ACTION_NEXT_SONG);
        filter.addAction(ACTION_PREV_SONG);
        filter.addAction(ACTION_PAUSE_SONG);
        filter.addAction(ACTION_SEEK_SONG);
        filter.addAction(ACTION_CHANGE_SONG);
        filter.addAction(ACTION_PLAY_PLAYLIST);
        filter.addAction(ACTION_PLAY_ARTIST);
        registerReceiver(playerServiceBroadcastReceiver, filter);
        return START_STICKY;
    }

    private void handleBroadcastReceived(Context context, Intent intent) throws IOException {
        switch (intent.getAction()) {
            case ACTION_PLAY_SINGLE:
                musicPlayerHandler.playSingleSong(intent.getLongExtra("songId", 0));
                updatePlayer();
                break;
            case ACTION_PLAY_ALL_SONGS:
                musicPlayerHandler.playAllSongs(intent.getLongExtra("songId", 0));
                updatePlayer();
                break;
            case ACTION_PLAY_ALBUM:
                musicPlayerHandler.playAlbumSongs(intent.getLongExtra("albumId", 0),
                        intent.getIntExtra("songPos", 0));
                updatePlayer();
                break;
            case ACTION_GET_SONG:
                updatePlayer();
                break;
            case ACTION_NEXT_SONG:
                musicPlayerHandler.playNextSong(musicPlayerHandler.getCurrentPlayingPos() + 1);
                break;
            case ACTION_PREV_SONG:
                musicPlayerHandler.playPrevSong(musicPlayerHandler.getCurrentPlayingPos() - 1);
                updatePlayer();
                break;
            case ACTION_PAUSE_SONG:
                musicPlayerHandler.playOrStop();
                break;
            case ACTION_SEEK_SONG:
                musicPlayerHandler.seek(intent.getIntExtra("seek", 0));
                break;
            case ACTION_CHANGE_SONG:
                musicPlayerHandler.playNextSong(intent.getIntExtra("pos", 0));
                break;
            case ACTION_PLAY_PLAYLIST:
                musicPlayerHandler.playPlaylist(intent.getIntExtra("id", 0),
                        intent.getIntExtra("pos", 0));
                updatePlayer();
                break;
            case ACTION_PLAY_ARTIST:
                musicPlayerHandler.playArtistSongs(intent.getStringExtra("name"),
                        intent.getIntExtra("pos", 0));
                updatePlayer();
                break;
        }
    }

    public void updatePlayer() {
        Intent i = new Intent();
        i.setAction(PlayerActivity.ACTION_RECIEVE_SONG);
        i.putExtra("songId", musicPlayerHandler.getCurrentPlayingSongId());
        i.putExtra("songName", musicPlayerHandler.getCurrentPlayingSong().getName());
        i.putExtra("albumId", musicPlayerHandler.getCurrentPlayingSong().getAlbumId());
        i.putExtra("seek", musicPlayerHandler.getMediaPlayer().getCurrentPosition());
        i.putExtra("pos", musicPlayerHandler.getCurrentPlayingPos());
        sendBroadcast(i);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        MediaPlayer mp = musicPlayerHandler.getMediaPlayer();
        if (mp != null) {
            mp.stop();
            mp.release();
        }
    }
}
