package com.architjn.acjmusicplayer.utils.items;

/**
 * Created by architjn on 02/09/15.
 */
public class Mood {

    private int imgRes;
    private String name;
    private com.architjn.acjmusicplayer.utils.Mood mood;

    public Mood(int imgRes, String name, com.architjn.acjmusicplayer.utils.Mood mood) {
        this.imgRes = imgRes;
        this.name = name;
        this.mood = mood;
    }

    public com.architjn.acjmusicplayer.utils.Mood getMood() {
        return mood;
    }

    public String getName() {
        return name;
    }

    public int getImgRes() {
        return imgRes;
    }

}
