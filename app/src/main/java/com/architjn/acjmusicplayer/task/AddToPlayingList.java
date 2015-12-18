package com.architjn.acjmusicplayer.task;

import android.content.ContentValues;
import android.database.sqlite.SQLiteCantOpenDatabaseException;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;

import com.architjn.acjmusicplayer.utils.PlayerDBHandler;
import com.architjn.acjmusicplayer.utils.items.Song;

import java.util.ArrayList;

/**
 * Created by architjn on 17/12/15.
 */
public class AddToPlayingList extends AsyncTask<Void, Void, Void> {

    private ArrayList<Song> songs;
    private int currentPlaying;
    private SQLiteDatabase db;

    public AddToPlayingList(SQLiteDatabase db, ArrayList<Song> songs, int currentPlaying) {
        this.db = db;
        this.songs = songs;
        this.currentPlaying = currentPlaying;
    }

    @Override
    protected Void doInBackground(Void... voids) {
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
//        db.close();
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
