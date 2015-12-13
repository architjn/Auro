package com.architjn.acjmusicplayer.utils;

import android.content.Context;
import android.media.MediaPlayer;
import android.util.Log;
import android.widget.Toast;

import com.architjn.acjmusicplayer.R;
import com.architjn.acjmusicplayer.service.PlayerService;
import com.architjn.acjmusicplayer.utils.items.Song;

import java.io.IOException;
import java.util.ArrayList;

/**
 * Created by architjn on 11/12/15.
 */
public class PlayerHandler {

    private static final String TAG = "PlayerHandler-TAG";
    private final PlayerDBHandler dbHandler;
    private final UserPreferenceHandler preferenceHandler;
    private Context context;
    private PlayerService service;
    private MediaPlayer mediaPlayer;
    private ArrayList<Song> currentPlayingSongs;


    private int currentPlayingPos = -1;

    public PlayerHandler(Context context, PlayerService service) {
        this.context = context;
        this.service = service;
        this.mediaPlayer = new MediaPlayer();
        dbHandler = new PlayerDBHandler(context);
        currentPlayingSongs = new ArrayList<>();
        preferenceHandler = new UserPreferenceHandler(context);
    }

    public void playAlbumSongs(long albumId) throws IOException {
        playAlbumSongs(albumId, 0);
    }

    public void playAlbumSongs(long albumId, final int startSongPos) throws IOException {
        currentPlayingSongs = ListSongs.getAlbumSongList(context, albumId);
        stopPlayer();
        mediaPlayer.reset();
        mediaPlayer.setDataSource(currentPlayingSongs.get(startSongPos).getPath());
        mediaPlayer.prepare();
        mediaPlayer.start();
        setPlayingPos(startSongPos);
        mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mediaPlayer) {
                try {
                    playNextSong(startSongPos + 1);
                } catch (IOException e) {
                    e.printStackTrace();
                    Toast.makeText(context, R.string.cant_play_song,
                            Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    public void stopPlayer() {
        setPlayingPos(-1);
        if (mediaPlayer != null) {
            mediaPlayer.stop();
        }
    }

    public void playAllSongs(long songId) throws IOException {
        stopPlayer();
        currentPlayingSongs.clear();
        currentPlayingSongs = ListSongs.getSongList(context);
        mediaPlayer.reset();
        final int songToPlayPos = findForASongInArrayList(songId);
        setPlayingPos(songToPlayPos);
        mediaPlayer.setDataSource(currentPlayingSongs
                .get(songToPlayPos).getPath());
        mediaPlayer.prepare();
        mediaPlayer.start();
        mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mediaPlayer) {
                try {
                    playNextSong(getNextSongPosition(songToPlayPos));
                } catch (IOException e) {
                    e.printStackTrace();
                    Toast.makeText(context, R.string.cant_play_song,
                            Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    public void playOrStop() {
        if (mediaPlayer.isPlaying())
            mediaPlayer.pause();
        else
            mediaPlayer.start();
    }

    public int getNextSongPosition(int currentPos) {
        if (preferenceHandler.isRepeatOneEnabled()) {
            //Repeat current song enabled
            return currentPos;
        } else if (currentPos == currentPlayingSongs.size() - 1
                && preferenceHandler.isRepeatAllEnabled()) {
            //Play 1st song as it was last song
            return 0;
        } else if (currentPos == currentPlayingSongs.size() - 1) {
            //Last song but not to repeat
            return currentPos;
        } else {
            //We have more songs in queue to play on
            return currentPos + 1;
        }
    }

    public void playNextSong(final int nextSongPos) throws IOException {
        if (nextSongPos < currentPlayingSongs.size()
                || preferenceHandler.isRepeatAllEnabled()) {
            //If there is some song available play it or repeat is enabled.
            stopPlayer();
            setPlayingPos(nextSongPos);
            mediaPlayer.reset();
            mediaPlayer.setDataSource(currentPlayingSongs
                    .get(nextSongPos).getPath());
            mediaPlayer.prepare();
            mediaPlayer.start();
            service.updatePlayer();
            mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mediaPlayer) {
                    try {
                        playNextSong(getNextSongPosition(nextSongPos));
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            });
        }
    }

    public void playPrevSong(final int prevSongPos) throws IOException {
        if ((mediaPlayer.getCurrentPosition() / 1000) <= 2) {
            if (prevSongPos >= 0) {
                //If song pos is more than 0
                stopPlayer();
                setPlayingPos(prevSongPos);
                mediaPlayer.reset();
                mediaPlayer.setDataSource(currentPlayingSongs.get(prevSongPos).getPath());
                mediaPlayer.prepare();
                mediaPlayer.start();
                mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                    @Override
                    public void onCompletion(MediaPlayer mediaPlayer) {
                        try {
                            if (prevSongPos == 0 && preferenceHandler.isRepeatAllEnabled()) {
                                playPrevSong(currentPlayingSongs.size() - 1);
                            } else if (prevSongPos == 0) {
                                playPrevSong(0);
                            } else {
                                playPrevSong(prevSongPos - 1);
                            }
                        } catch (IOException e) {
                            e.printStackTrace();
                            Toast.makeText(context, R.string.cant_play_song,
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        } else {
            mediaPlayer.seekTo(0);
        }
    }

    private int findForASongInArrayList(long searchId) {
        for (int i = 0; i < currentPlayingSongs.size(); i++) {
            if (currentPlayingSongs.get(i).getSongId() == searchId) {
                return i;
            }
        }
        return -1;
    }

    public void playSingleSong(long songId) throws IOException {
        setPlayingPos(0);
        Song songToPlay = getSongFromId(songId);
        stopPlayer();
        mediaPlayer.reset();
        mediaPlayer.setDataSource(songToPlay.getPath());
        mediaPlayer.prepare();
        currentPlayingSongs.clear();
        currentPlayingSongs.add(songToPlay);
        mediaPlayer.start();
    }

    public void setPlayingPos(int pos) {
        currentPlayingPos = pos;
    }

    public long getCurrentPlayingSongId() {
        if (currentPlayingPos == -1 || currentPlayingPos >= currentPlayingSongs.size())
            return -1;
        return currentPlayingSongs.get(currentPlayingPos).getSongId();
    }

    public MediaPlayer getMediaPlayer() {
        return mediaPlayer;
    }

    public Song getSongFromId(long id) {
        return dbHandler.getSongFromId(id);
    }

    public int getCurrentPlayingPos() {
        return currentPlayingPos;
    }

    public void seek(int seek) {
        mediaPlayer.seekTo(seek);
    }
}
