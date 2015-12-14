package com.architjn.acjmusicplayer.utils;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteStatement;
import android.provider.MediaStore;

import com.architjn.acjmusicplayer.utils.items.Playlist;
import com.architjn.acjmusicplayer.utils.items.Song;

import java.util.ArrayList;

/**
 * Created by architjn on 09/12/15.
 */
public class PlaylistDBHelper extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "PlaylistDB";

    private static final String TABLE_PLAYLIST_SONGS = "playlistSongs";
    private static final String TABLE_PLAYLIST = "playlist";

    private static final String SONG_KEY_ID = "song_id";
    private static final String SONG_KEY_REAL_ID = "song_real_id";
    private static final String SONG_KEY_PLAYLIST_ID = "song_playlist_id";

    private static final String PLAYLIST_KEY_ID = "playlist_id";
    private static final String PLAYLIST_KEY_NAME = "playlist_name";
    private static final String TAG = "PlaylistDBHelper-TAG";

    private Context context;

    public PlaylistDBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        this.context = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_PLAYLIST_SONG_TABLE = "CREATE TABLE playlistSongs (" +
                "song_id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "song_real_id INTEGER," +
                "song_playlist_id INTEGER)";
        String CREATE_PLAYLIST_TABLE = "CREATE TABLE playlist (" +
                "playlist_id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "playlist_name TEXT)";
        db.execSQL(CREATE_PLAYLIST_SONG_TABLE);
        db.execSQL(CREATE_PLAYLIST_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS playlistSongs");
        db.execSQL("DROP TABLE IF EXISTS playlist");
        this.onCreate(db);
    }

    public void renamePlaylist(String name, int playlistId) {
        String query = "UPDATE " + TABLE_PLAYLIST + " SET " + PLAYLIST_KEY_NAME
                + "='" + name + "' WHERE "
                + PLAYLIST_KEY_ID + "='" + playlistId + "'";
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL(query);
        db.close();
    }

    public void addSong(int song, int playlistId) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.putNull(SONG_KEY_ID);
        values.put(SONG_KEY_REAL_ID, song);
        values.put(SONG_KEY_PLAYLIST_ID, playlistId);

        db.insert(TABLE_PLAYLIST_SONGS, null, values);
        db.close();
    }

    public void addSong(ArrayList<Integer> songs, int playlistId) {
        SQLiteDatabase db = this.getWritableDatabase();
        for (int i = 0; i < songs.size(); i++) {
            ContentValues values = new ContentValues();
            values.putNull(SONG_KEY_ID);
            values.put(SONG_KEY_REAL_ID, songs.get(i));
            values.put(SONG_KEY_PLAYLIST_ID, playlistId);

            db.insert(TABLE_PLAYLIST_SONGS, null, values);
        }
        db.close();
    }

    public ArrayList<Playlist> getAllPlaylist() {
        String query = "SELECT  * FROM " + TABLE_PLAYLIST;
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(query, null);
        ArrayList<Playlist> playlist = new ArrayList<>();
        if (cursor.moveToFirst()) {
            do {
                playlist.add(getPlaylistFromCursor(cursor));
            } while (cursor.moveToNext());
        }
        db.close();
        return playlist;
    }

    private Playlist getPlaylistFromCursor(Cursor cursor) {
        int playlistId = Integer.parseInt(cursor.getString(0));
        return new Playlist(playlistId, cursor.getString(1),
                getPlaylistSongCount(playlistId));
    }

    public void removeSong(int songId, int playlistId) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("DELETE FROM " + TABLE_PLAYLIST_SONGS + " WHERE " +
                SONG_KEY_REAL_ID + "='" + songId + "' AND "
                + SONG_KEY_PLAYLIST_ID + "='" + playlistId + "'");
        db.close();
    }

    public void deletePlaylist(int playlistId) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("DELETE FROM " + TABLE_PLAYLIST + " WHERE " +
                PLAYLIST_KEY_ID + "='" + playlistId + "'");
        db.execSQL("DELETE FROM " + TABLE_PLAYLIST_SONGS + " WHERE " +
                SONG_KEY_PLAYLIST_ID + "='" + playlistId + "'");
        db.close();
    }

    public void createPlaylist(String name) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.putNull(PLAYLIST_KEY_ID);
        values.put(PLAYLIST_KEY_NAME, name);

        db.insert(TABLE_PLAYLIST, null, values);
        db.close();
    }

    public ArrayList<Song> getAllPlaylistSongs(int playlistId) {
        SQLiteDatabase db = this.getWritableDatabase();
        ArrayList<Integer> songsIds = getAllPlaylistSongsIds(db, playlistId);

        ArrayList<Song> songList = new ArrayList<>();
        System.gc();
        for (int i = 0; i < songsIds.size(); i++) {
            final String where = MediaStore.Audio.Media.IS_MUSIC + "=1 AND " +
                    MediaStore.Audio.Media._ID + "=" + songsIds.get(i).toString();
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
                        (MediaStore.Audio.Media.DATE_ADDED);
                do {
                    songList.add(new Song(musicCursor.getLong(idColumn),
                            musicCursor.getString(titleColumn),
                            musicCursor.getString(artistColumn),
                            musicCursor.getString(pathColumn), false,
                            musicCursor.getLong(albumIdColumn),
                            musicCursor.getString(albumColumn),
                            musicCursor.getLong(addedDateColumn),
                            musicCursor.getLong(songDurationColumn)));
                }
                while (musicCursor.moveToNext());
            }
            if (musicCursor != null)
                musicCursor.close();
        }
        db.close();
        return songList;
    }

    private ArrayList<Integer> getAllPlaylistSongsIds(SQLiteDatabase db, int playlistId) {
        String query = "SELECT  * FROM " + TABLE_PLAYLIST_SONGS + " WHERE "
                + SONG_KEY_PLAYLIST_ID + "='" + playlistId + "'";
        Cursor cursor = db.rawQuery(query, null);
        ArrayList<Integer> songsId = new ArrayList<>();
        if (cursor.moveToFirst()) {
            do {
                songsId.add(Integer.parseInt(cursor.getString(1)));
            } while (cursor.moveToNext());
        }
        return songsId;
    }

    public long getPlaylistSongCount(int playlistId) {
        String query = "select count(*) from " + TABLE_PLAYLIST_SONGS + " where "
                + SONG_KEY_PLAYLIST_ID + "='" + playlistId + "'";
        SQLiteDatabase db = this.getWritableDatabase();
        SQLiteStatement s = db.compileStatement(query);
        long count = s.simpleQueryForLong();
        db.close();
        return count;
    }
}