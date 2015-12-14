package com.architjn.acjmusicplayer.task;

import android.content.ContentUris;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.ParcelFileDescriptor;

import com.architjn.acjmusicplayer.utils.adapters.PlayingListAdapter;
import com.architjn.acjmusicplayer.utils.adapters.SongListAdapter;

import java.io.FileDescriptor;

/**
 * Created by architjn on 29/11/15.
 */
public class SongItemLoader extends AsyncTask<Void, Void, Void> {

    private Context context;
    private SongListAdapter.SimpleItemViewHolder holder;
    private PlayingListAdapter.SimpleItemViewHolder holderPlaying;
    private long albumId;
    private int size;
    private Bitmap bmp;

    public SongItemLoader(Context context, SongListAdapter.SimpleItemViewHolder holder,
                          long albumId, int size) {
        this.context = context;
        this.holder = holder;
        this.albumId = albumId;
        this.size = size;
    }

    public SongItemLoader(Context context, PlayingListAdapter.SimpleItemViewHolder holder,
                          long albumId, int size) {
        this.context = context;
        this.holderPlaying = holder;
        this.albumId = albumId;
        this.size = size;
    }

    @Override
    protected Void doInBackground(Void... voids) {
        bmp = ThumbnailUtils.extractThumbnail(getAlbumart(albumId), size, size);
        return null;
    }

    public Bitmap getAlbumart(Long album_id) {
        Bitmap bm = null;
        try {
            final Uri sArtworkUri = Uri
                    .parse("content://media/external/audio/albumart");

            Uri uri = ContentUris.withAppendedId(sArtworkUri, album_id);

            ParcelFileDescriptor pfd = context.getContentResolver()
                    .openFileDescriptor(uri, "r");

            if (pfd != null) {
                FileDescriptor fd = pfd.getFileDescriptor();
                bm = BitmapFactory.decodeFileDescriptor(fd);
            }
        } catch (Exception e) {
        }
        return bm;
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        if (holder != null) {
            holder.img.setImageBitmap(bmp);
        }else
            holderPlaying.img.setImageBitmap(bmp);
        super.onPostExecute(aVoid);
    }
}
