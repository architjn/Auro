package com.architjn.acjmusicplayer.elements;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.architjn.acjmusicplayer.elements.items.Playlist;
import com.architjn.acjmusicplayer.elements.items.SongListItem;

import java.util.LinkedList;
import java.util.List;

public class MySQLiteHelper extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "PlaylistDB";

    public MySQLiteHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_PLAYLIST_TABLE = "CREATE TABLE playlist ( " +
                "playlist_id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "playlist_title TEXT)";

        String CREATE_SONGS_TABLE = "CREATE TABLE song (" +
                "song_id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "song_playlist_id INTEGER," +
                "song_album_id INTEGER," +
                "song_desc TEXT," +
                "song_fav INTEGER," +
                "song_path TEXT," +
                "song_name TEXT," +
                "song_count INTEGER," +
                "song_album_name TEXT)";

        db.execSQL(CREATE_PLAYLIST_TABLE);
        db.execSQL(CREATE_SONGS_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS playlist");
        db.execSQL("DROP TABLE IF EXISTS song");

        this.onCreate(db);
    }

    private static final String TABLE_PLAYLIST = "playlist";
    private static final String TABLE_SONG = "song";

    private static final String PLAYLIST_KEY_ID = "playlist_id";
    private static final String PLAYLIST_KEY_TITLE = "playlist_title";
    private static final String SONG_KEY_ID = "song_id";
    private static final String SONG_KEY_PLAYLISTID = "song_playlist_id";
    private static final String SONG_KEY_ALBUMID = "song_album_id";
    private static final String SONG_KEY_DESC = "song_desc";
    private static final String SONG_KEY_FAV = "song_fav";
    private static final String SONG_KEY_PATH = "song_path";
    private static final String SONG_KEY_NAME = "song_name";
    private static final String SONG_KEY_COUNT = "song_count";
    private static final String SONG_KEY_ALBUM_NAME = "song_album_name";

    private static final String[] COLUMNS = {PLAYLIST_KEY_ID, PLAYLIST_KEY_TITLE};

    public int createNewPlayList(String name) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(PLAYLIST_KEY_TITLE, name);
        long id = db.insert(TABLE_PLAYLIST, null,
                values);
        db.close();
        return (int) id;
    }

    public void renamePlaylist(String newName, int playlistId) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("UPDATE " + TABLE_PLAYLIST + " SET " + PLAYLIST_KEY_TITLE + "='" +
                newName + "' WHERE " + PLAYLIST_KEY_ID + "='" + playlistId + "'");
    }

    public void removePlayList(int playlistId) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("DELETE FROM " + TABLE_PLAYLIST + " WHERE " + PLAYLIST_KEY_ID
                + "='" + playlistId + "'");
        db.execSQL("DELETE FROM " + TABLE_SONG + " WHERE " + SONG_KEY_PLAYLISTID
                + "='" + playlistId + "'");
    }

    public List<Playlist> getAllPlaylist() {
        List<Playlist> playlists = new LinkedList<Playlist>();
        String query = "SELECT  * FROM " + TABLE_PLAYLIST;
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(query, null);
        Playlist playlist = null;
        if (cursor.moveToFirst()) {
            do {
                playlist = new Playlist(Integer.parseInt(cursor.getString(0)), cursor.getString(1));
                playlists.add(playlist);
            } while (cursor.moveToNext());
        }

        return playlists;
    }

    public void addSong(SongListItem song, int playlistId) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(SONG_KEY_ID, (int) song.getId());
        values.put(SONG_KEY_PLAYLISTID, playlistId);
        values.put(SONG_KEY_ALBUMID, song.getAlbumId());
        values.put(SONG_KEY_DESC, song.getDesc());
        values.put(SONG_KEY_FAV, song.getFav());
        values.put(SONG_KEY_PATH, song.getPath());
        values.put(SONG_KEY_NAME, song.getName());
        values.put(SONG_KEY_COUNT, song.getCount());
        values.put(SONG_KEY_ALBUM_NAME, song.getAlbumName());

        db.insert(TABLE_SONG, null, values);
        db.close();
    }

    public List<SongListItem> getPlayListSongs(int playlistId) {
        List<SongListItem> songs = new LinkedList<SongListItem>();
        String query = "SELECT  * FROM " + TABLE_SONG + " WHERE "
                + SONG_KEY_PLAYLISTID + "='" + playlistId + "'";
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(query, null);
        SongListItem song = null;
        if (cursor.moveToFirst()) {
            do {
                boolean fav;
                if (cursor.getString(4).matches("0")) {
                    fav = false;
                } else {
                    fav = true;
                }
                song = new SongListItem(Long.valueOf(cursor.getString(0)),
                        cursor.getString(6), cursor.getString(3),
                        cursor.getString(5), fav, Long.parseLong(cursor.getString(2)),
                        cursor.getString(8), Integer.parseInt(cursor.getString(7)));
                songs.add(song);
            } while (cursor.moveToNext());
        }

        return songs;
    }

    public void removeSong(long songId, int playlistId) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("DELETE FROM " + TABLE_SONG + " WHERE " + SONG_KEY_ID
                + "='" + songId + "' AND " + SONG_KEY_PLAYLISTID + "='" + playlistId + "'");
    }

}