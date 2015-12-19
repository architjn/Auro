package com.architjn.acjmusicplayer.utils.items;

/**
 * Created by architjn on 07/12/15.
 */
public class Artist {

    private long artistId;
    private String artistName;
    private int numberOfAlbums;
    private int numberOfSongs;

    public Artist() {
        super();
    }

    public Artist(long artistId, String artistName, int numberOfAlbums, int numberOfSongs) {
        this.artistId = artistId;
        this.artistName = artistName;
        this.numberOfAlbums = numberOfAlbums;
        this.numberOfSongs = numberOfSongs;
    }

    public int getNumberOfSongs() {
        return numberOfSongs;
    }

    public int getNumberOfAlbums() {
        return numberOfAlbums;
    }

    public String getArtistName() {
        return artistName;
    }

    public long getArtistId() {
        return artistId;
    }
}
