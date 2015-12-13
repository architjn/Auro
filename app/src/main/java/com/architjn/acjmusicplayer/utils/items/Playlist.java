package com.architjn.acjmusicplayer.utils.items;

/**
 * Created by architjn on 09/12/15.
 */
public class Playlist {

    private int playlistId;
    private String playlistName;
    private long songCount;

    public Playlist(int playlistId, String playlistName, long songCount) {
        this.playlistId = playlistId;
        this.playlistName = playlistName;
        this.songCount = songCount;
    }

    public int getPlaylistId() {
        return playlistId;
    }

    public String getPlaylistName() {
        return playlistName;
    }

    public long getSongCount() {
        return songCount;
    }
}
