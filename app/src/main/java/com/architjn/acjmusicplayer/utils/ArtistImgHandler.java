package com.architjn.acjmusicplayer.utils;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.architjn.acjmusicplayer.task.FetchArtistArtWork;

import java.io.File;
import java.util.Arrays;
import java.util.Collections;

/**
 * Created by architjn on 15/12/15.
 */
public abstract class ArtistImgHandler extends SQLiteOpenHelper {

    private static final String TAG = "ArtistImgHandler-TAG";
    private final Integer[] randomNumbers;
    private int pos = 0;
    private Context context;

    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "ArtistDB";

    private static final String TABLE_PLAYBACK = "artist";

    private static final String ARTIST_KEY_ID = "artist_id";
    private static final String ARTIST_KEY_NAME = "artist_name";
    private static final String ARTIST_KEY_URL = "artist_img";

    public ArtistImgHandler(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        randomNumbers = randomNumbers(1000);
        this.context = context;
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        String CREATE_PLAYBACK_SONG_TABLE = "CREATE TABLE " + TABLE_PLAYBACK + " (" +
                ARTIST_KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                ARTIST_KEY_NAME + " TEXT," +
                ARTIST_KEY_URL + " TEXT)";
        sqLiteDatabase.execSQL(CREATE_PLAYBACK_SONG_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + TABLE_PLAYBACK);
    }

    public String getArtistArtWork(final String name, int pos) {
        String url = getArtistImgFromDB(name);
        if (url != null) {
            if ((new File(url)).exists())
                return url;
            else
                removeArtistImgFromDB(name);
        } else {
            new FetchArtistArtWork(context, name, randomNumbers[pos]) {
                @Override
                public void onDownloadComplete(String url) {
                    updateArtistArtWorkInDB(name, url);
                    ArtistImgHandler.this.onDownloadComplete(url);
                }
            }.execute();
        }
        return null;
    }


    public abstract void onDownloadComplete(String url);

    public void updateArtistArtWorkInDB(String name, String url) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.putNull(ARTIST_KEY_ID);
        values.put(ARTIST_KEY_NAME, name);
        values.put(ARTIST_KEY_URL, url);
        db.insert(TABLE_PLAYBACK, null, values);
        db.close();
    }

    public String getArtistImgFromDB(String name) {
        SQLiteDatabase db = this.getWritableDatabase();
        String query = "SELECT  * FROM " + TABLE_PLAYBACK + " WHERE "
                + ARTIST_KEY_NAME + "='" + name.replace("'", "''") + "'";
        Cursor cursor = db.rawQuery(query, null);
        if (cursor.moveToFirst()) {
            return cursor.getString(2);
        }
        db.close();
        return null;
    }

    public void removeArtistImgFromDB(String name) {
        SQLiteDatabase db = this.getWritableDatabase();
        String query = "DELETE  FROM " + TABLE_PLAYBACK + " WHERE "
                + ARTIST_KEY_NAME + "='" + name + "'";
        db.rawQuery(query, null);
        db.close();
    }

    public Integer[] randomNumbers(int range) {
        Integer[] arr = new Integer[range];
        for (int i = 0; i < arr.length; i++) {
            arr[i] = i;
        }
        Collections.shuffle(Arrays.asList(arr));
        return arr;
    }

}
