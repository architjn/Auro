package com.architjn.acjmusicplayer.utils.items;

import android.graphics.Bitmap;

import com.architjn.acjmusicplayer.utils.Mood;

/**
 * Created by architjn on 10/06/15.
 */
public class SongListItem {

    long id, albumId;
    String name, desc, path, albumName;
    Boolean fav;
    Bitmap art;
    int count;
    com.architjn.acjmusicplayer.utils.Mood mood;

    public SongListItem(long id, String name, String desc, String path,
                        Boolean fav, long albumId, String albumName, int count, com.architjn.acjmusicplayer.utils.Mood mood) {
        this.desc = desc;
        this.fav = fav;
        this.path = path;
        this.id = id;
        this.name = name;
        this.albumId = albumId;
        this.count = count;
        this.albumName = albumName;
        this.mood = mood;
        if (mood == null)
            this.mood = Mood.UNKNOWN;
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

    public com.architjn.acjmusicplayer.utils.Mood getMood() {
        return mood;
    }

    public void setMood(com.architjn.acjmusicplayer.utils.Mood mood) {
        this.mood = mood;
    }
}
