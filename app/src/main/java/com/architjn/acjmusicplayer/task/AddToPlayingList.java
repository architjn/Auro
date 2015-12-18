package com.architjn.acjmusicplayer.task;

import android.content.ContentValues;
import android.database.sqlite.SQLiteCantOpenDatabaseException;
import android.database.sqlite.SQLiteDatabase;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.afollestad.async.Action;
import com.architjn.acjmusicplayer.utils.PlayerDBHandler;
import com.architjn.acjmusicplayer.utils.items.Song;

import java.util.ArrayList;

public class AddToPlayingList extends Action {

    private ArrayList<Song> songs;
    private int currentPlaying;
    private SQLiteDatabase db;

    public AddToPlayingList(SQLiteDatabase db, ArrayList<Song> songs, int currentPlaying) {
        this.db = db;
        this.songs = songs;
        this.currentPlaying = currentPlaying;
    }

    @NonNull
    @Override
    public String id() {
        return this.getClass().getSimpleName();
    }

    @Nullable
    @Override
    protected Object run() throws InterruptedException {
        clearList(db);
        for (int i = 0; i < songs.size(); i++) {
            ContentValues values = new ContentValues();
            values.putNull(PlayerDBHandler.SONG_KEY_ID);
            values.put(PlayerDBHandler.SONG_KEY_REAL_ID, songs.get(i).getSongId());
            if (i == currentPlaying)
                values.put(PlayerDBHandler.SONG_KEY_LAST_PLAYED, true);
            else
                values.put(PlayerDBHandler.SONG_KEY_LAST_PLAYED, false);

            db.insert(PlayerDBHandler.TABLE_PLAYBACK, null, values);
        }
        return null;
    }

    private void clearList(SQLiteDatabase db) {
        try {
            db.execSQL("DELETE FROM " + PlayerDBHandler.TABLE_PLAYBACK);
        } catch (SQLiteCantOpenDatabaseException e) {
            e.printStackTrace();
        }
    }
}
