package com.architjn.acjmusicplayer.utils;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.MediaStore;
import android.util.Log;

import com.architjn.acjmusicplayer.utils.items.Song;

import java.util.ArrayList;

/**
 * Created by architjn on 11/12/15.
 */
public class PlayerDBHandler extends SQLiteOpenHelper {

    private static final String TAG = "PlayerDBHandler-TAG";
    private Context context;

    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "PlaybackDB";

    private static final String TABLE_PLAYBACK = "songs";

    private static final String SONG_KEY_ID = "song_id";
    private static final String SONG_KEY_REAL_ID = "song_real_id";
    private static final String SONG_KEY_LAST_PLAYED = "song_last_played";
    private int fetchedPlayingPos = -1;


    public PlayerDBHandler(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        this.context = context;
    }

    public Song getSongFromId(long id) {
        System.gc();
        final String where = MediaStore.Audio.Media._ID + "=" + id;
        Cursor musicCursor = context.getContentResolver().query(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                null, where, null, null);
        if (musicCursor != null && musicCursor.moveToFirst()) {
            int titleColumn = musicCursor.getColumnIndex
                    (android.provider.MediaStore.Audio.Media.TITLE);
            int idColumn = musicCursor.getColumnIndex
                    (android.provider.MediaStore.Audio.Media._ID);
            int artistColumn = musicCursor.getColumnIndex
                    (android.provider.MediaStore.Audio.Media.ARTIST);
            int pathColumn = musicCursor.getColumnIndex
                    (MediaStore.Audio.Media.DATA);
            int albumIdColumn = musicCursor.getColumnIndex
                    (MediaStore.Audio.Media.ALBUM_ID);
            int albumColumn = musicCursor.getColumnIndex
                    (MediaStore.Audio.Media.ALBUM);
            int addedDateColumn = musicCursor.getColumnIndex
                    (MediaStore.Audio.Media.DATE_ADDED);
            int songDurationColumn = musicCursor.getColumnIndex
                    (MediaStore.Audio.Media.DURATION);
            return new Song(musicCursor.getLong(idColumn),
                    musicCursor.getString(titleColumn),
                    musicCursor.getString(artistColumn),
                    musicCursor.getString(pathColumn), false,
                    musicCursor.getLong(albumIdColumn),
                    musicCursor.getString(albumColumn),
                    musicCursor.getLong(addedDateColumn),
                    musicCursor.getLong(songDurationColumn));
        }
        return null;
    }

    public String setAlbumArt(long albumId) {
        Cursor cursor = context.getContentResolver().query(MediaStore.Audio.Albums.EXTERNAL_CONTENT_URI,
                new String[]{MediaStore.Audio.Albums._ID, MediaStore.Audio.Albums.ALBUM_ART},
                MediaStore.Audio.Albums._ID + "=?",
                new String[]{String.valueOf(albumId)},
                null);
        if (cursor.moveToFirst()) {
            return cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Albums.ALBUM_ART));
        }
        return null;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_PLAYBACK_SONG_TABLE = "CREATE TABLE " + TABLE_PLAYBACK + " (" +
                SONG_KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                SONG_KEY_REAL_ID + " INTEGER," +
                SONG_KEY_LAST_PLAYED + " INTEGER)";
        db.execSQL(CREATE_PLAYBACK_SONG_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int i, int i1) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_PLAYBACK);
    }

    public void changePlaybackList(ArrayList<Song> songs, int currentPlaying) {
        clearList();
        SQLiteDatabase db = this.getWritableDatabase();
        for (int i = 0; i < songs.size(); i++) {
            ContentValues values = new ContentValues();
            values.putNull(SONG_KEY_ID);
            values.put(SONG_KEY_REAL_ID, songs.get(i).getSongId());
            if (i == currentPlaying)
                values.put(SONG_KEY_LAST_PLAYED, true);
            values.put(SONG_KEY_LAST_PLAYED, false);

            db.insert(TABLE_PLAYBACK, null, values);
        }
        db.close();
    }

    public ArrayList<Song> getAllPlaybackSongs() {
        SQLiteDatabase db = this.getWritableDatabase();
        String query = "SELECT  * FROM " + TABLE_PLAYBACK;
        Cursor cursor = db.rawQuery(query, null);
        ArrayList<Song> playbackSongs = new ArrayList<>();
        if (cursor.moveToFirst()) {
            int counter = 0;
            do {
                playbackSongs.add(ListSongs.getSong(context, cursor.getInt(1)));
                if (cursor.getInt(2) == 1)
                    fetchedPlayingPos = counter;
                counter++;
            } while (cursor.moveToNext());
        }
        db.close();
        return playbackSongs;
    }

    public int getFetchedPlayingPos() {
        return fetchedPlayingPos;
    }

    public void addSong(Song song) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.putNull(SONG_KEY_ID);
        values.put(SONG_KEY_REAL_ID, song.getSongId());
        values.put(SONG_KEY_LAST_PLAYED, false);
        db.insert(TABLE_PLAYBACK, null, values);
        db.close();
    }

    public void updatePlayingPosition(long songId) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("UPDATE " + TABLE_PLAYBACK + " SET " + SONG_KEY_LAST_PLAYED + "='0' WHERE " + SONG_KEY_LAST_PLAYED + "='1'");
        db.execSQL("UPDATE " + TABLE_PLAYBACK + " SET " + SONG_KEY_LAST_PLAYED + "='1' WHERE "
                + SONG_KEY_REAL_ID + "='" + songId + "'");
        db.close();
    }

    private void clearList() {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("DELETE FROM " + TABLE_PLAYBACK);
        db.close();
    }

}
