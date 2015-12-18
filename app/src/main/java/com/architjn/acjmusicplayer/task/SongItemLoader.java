package com.architjn.acjmusicplayer.task;

import android.content.ContentUris;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.ParcelFileDescriptor;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.afollestad.async.Action;
import com.architjn.acjmusicplayer.utils.adapters.PlayingListAdapter;
import com.architjn.acjmusicplayer.utils.adapters.SongListAdapter;

import java.io.FileDescriptor;

public class SongItemLoader extends Action {

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

    @NonNull
    @Override
    public String id() {
        return this.getClass().getSimpleName();
    }

    @Nullable
    @Override
    protected Object run() throws InterruptedException {
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
        } catch (Exception ignored) {
        }
        return bm;
    }

    @Override
    protected void done(@Nullable Object result) {
        if (holder != null) holder.img.setImageBitmap(bmp);
        else holderPlaying.img.setImageBitmap(bmp);
    }
}
