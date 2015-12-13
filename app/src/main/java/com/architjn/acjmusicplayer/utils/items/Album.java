package com.architjn.acjmusicplayer.utils.items;

/**
 * Created by architjn on 29/11/15.
 */
public class Album {

    private long albumId;
    private String albumTitle, albumArtist, albumArtPath;
    private boolean fav;
    private int songNumber;

    public Album(long albumId, String albumTitle, String albumArtist, boolean fav, String albumArtPath, int songNumber) {
        this.albumId = albumId;
        this.albumTitle = albumTitle;
        this.albumArtist = albumArtist;
        this.fav = fav;
        this.albumArtPath = albumArtPath;
        this.songNumber = songNumber;
    }

    public long getAlbumId() {
        return albumId;
    }

    public String getAlbumArtist() {
        return albumArtist;
    }

    public String getAlbumTitle() {
        return albumTitle;
    }

    public int getSongNumber() {
        return songNumber;
    }

    public String getAlbumArtPath() {
        return albumArtPath;
    }

    public boolean isFav() {
        return fav;
    }
}
