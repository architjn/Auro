package com.architjn.acjmusicplayer.elements.items;

import android.graphics.Bitmap;

/**
 * Created by architjn on 10/06/15.
 */
public class SongListItem {

    long id, albumId;
    String name, desc, path, albumName;
    Boolean fav;
    Bitmap art;
    int count;

    public SongListItem(long id, String name, String desc, String path,
                        Boolean fav, long albumId, String albumName, int count) {
        this.desc = desc;
        this.fav = fav;
        this.path = path;
        this.id = id;
        this.name = name;
        this.albumId = albumId;
        this.count = count;
        this.albumName = albumName;
    }

    public long getId() {
        return this.id;
    }

    public String getName() {
        return this.name;
    }

    public String getPath() {
        return this.path;
    }

    public String getAlbumName() {
        return this.albumName;
    }

    public String getDesc() {
        return this.desc;
    }

    public long getAlbumId() {
        return this.albumId;
    }


    public Boolean getFav() {
        return this.fav;
    }

    public Bitmap getArt() {
        return this.art;
    }

    public int getCount() {
        return this.count;
    }

}
