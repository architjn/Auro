package com.architjn.acjmusicplayer.utils;

import android.content.Context;
import android.database.Cursor;
import android.provider.MediaStore;

import com.architjn.acjmusicplayer.utils.items.Song;

/**
 * Created by architjn on 11/12/15.
 */
public class PlayerDBHandler {

    private Context context;

    public PlayerDBHandler(Context context) {
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

}
