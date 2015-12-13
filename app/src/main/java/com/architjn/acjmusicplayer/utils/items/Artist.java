package com.architjn.acjmusicplayer.utils.items;

/**
 * Created by architjn on 07/12/15.
 */
public class Artist {

    private final long artistId;
    private final String artistName;
    private final int numberOfAlbums;
    private final int numberOfSongs;

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
