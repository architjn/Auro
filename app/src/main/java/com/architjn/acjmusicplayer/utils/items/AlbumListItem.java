package com.architjn.acjmusicplayer.utils.items;

/**
 * Created by architjn on 10/06/15.
 */
public class AlbumListItem {

    long id;
    String name, desc, artString;
    Boolean fav;
    int numOfSongs;

    public AlbumListItem(long id, String name, String desc, Boolean fav, String artString, int numOfSongs) {
        this.desc = desc;
        this.fav = fav;
        this.id = id;
        this.name = name;
        this.artString = artString;
        this.numOfSongs = numOfSongs;
    }

    public AlbumListItem(long id, String name, String desc, Boolean fav, int numOfSongs) {
        this.desc = desc;
        this.fav = fav;
        this.id = id;
        this.name = name;
        this.numOfSongs = numOfSongs;
    }

    public int getNumOfSongs() {
        return numOfSongs;
    }

    public long getId() {
        return this.id;
    }

    public String getName() {
        return this.name;
    }

    public String getDesc() {
        return this.desc;
    }
    public String getArtString() {
        return this.artString;
    }

    public Boolean getFav() {
        return this.fav;
    }

}
