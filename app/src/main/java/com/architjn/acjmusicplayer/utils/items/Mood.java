package com.architjn.acjmusicplayer.utils.items;

import java.util.ArrayList;
import java.util.List;

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

    public static List<Mood> getAllMoods() {
        List<Mood> moods = new ArrayList<>();
        moods.add(new Mood(0, "Sad", com.architjn.acjmusicplayer.utils.Mood.SAD));
        moods.add(new Mood(0, "Happy", com.architjn.acjmusicplayer.utils.Mood.HAPPY));
        moods.add(new Mood(0, "Angry", com.architjn.acjmusicplayer.utils.Mood.ANGRY));
        return moods;
    }

}
