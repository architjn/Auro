package com.architjn.acjmusicplayer.elements.items;

import android.graphics.Bitmap;

/**
 * Created by architjn on 10/06/15.
 */
public class ArtistListItem {

    long id;
    String name;
    Bitmap art;
    int numOfTracks, numOfAlbums;

    public ArtistListItem(long id, String name, Bitmap art, int numOfTracks, int numOfAlbums) {
        this.art = art;
        this.id = id;
        this.name = name;
        this.numOfTracks = numOfTracks;
        this.numOfAlbums = numOfAlbums;
    }

    public ArtistListItem(long id, String name, int numOfTracks, int numOfAlbums) {
        this.id = id;
        this.name = name;
        this.numOfTracks = numOfTracks;
        this.numOfAlbums = numOfAlbums;
    }

    public int getNumOfAlbums() {
        return numOfAlbums;
    }

    public int getNumOfTracks() {
        return numOfTracks;
    }

    public long getId() {
        return this.id;
    }

    public String getName() {
        return this.name;
    }

    public Bitmap getArt() {
        return this.art;
    }

}
