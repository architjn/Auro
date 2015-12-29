package com.architjn.acjmusicplayer.utils.handlers;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by architjn on 11/12/15.
 */
public class UserPreferenceHandler {

    private static final String PREF_NAME = "com.architjn";
    private static final String REPEAT_ALL = "repeat_all";
    private static final String REPEAT_ONE = "repeat_one";
    private static final String SHUFFLE = "shuffle";
    private final SharedPreferences shp;

    public UserPreferenceHandler(Context context) {
        shp = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
    }

    public void setRepeatAllEnable(boolean enable) {
        shp.edit().putBoolean(REPEAT_ALL, enable).apply();
    }

    public void setRepeatOneEnable(boolean enable) {
        shp.edit().putBoolean(REPEAT_ONE, enable).apply();
    }

    public boolean isRepeatEnabled() {
        if (isRepeatAllEnabled() || isRepeatOneEnabled())
            return true;
        return false;
    }

    public void setRepeatEnable() {
        if (isRepeatAllEnabled()) {
            setRepeatAllEnable(false);
            setRepeatOneEnable(true);
        } else if (isRepeatOneEnabled()) {
            setRepeatOneEnable(false);
        } else {
            setRepeatAllEnable(true);
        }
    }

    public boolean isRepeatAllEnabled() {
        return shp.getBoolean(REPEAT_ALL, false);
    }

    public boolean isRepeatOneEnabled() {
        return shp.getBoolean(REPEAT_ONE, false);
    }

    public void setShuffle() {
        if (isShuffleEnabled())
            setShuffleEnabled(false);
        else
            setShuffleEnabled(true);
    }

    public boolean isShuffleEnabled() {
        return shp.getBoolean(SHUFFLE, false);
    }

    public void setShuffleEnabled(boolean value) {
        shp.edit().putBoolean(SHUFFLE, value).apply();
    }

}
