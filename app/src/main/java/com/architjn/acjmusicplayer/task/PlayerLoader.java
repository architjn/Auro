package com.architjn.acjmusicplayer.task;

import android.content.ContentUris;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.ParcelFileDescriptor;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.widget.ImageView;

import com.afollestad.async.Action;
import com.architjn.acjmusicplayer.utils.ImageBlurAnimator;

import java.io.FileDescriptor;

public class PlayerLoader extends Action {

    private Context context;
    private ImageView img;
    private Bitmap bmp;
    private long albumId;

    public PlayerLoader(Context context, ImageView img, long albumId) {
        this.context = context;
        this.img = img;
        this.albumId = albumId;
    }

    @NonNull
    @Override
    public String id() {
        return this.getClass().getSimpleName();
    }

    @Nullable
    @Override
    protected Object run() throws InterruptedException {
        bmp = getAlbumart(albumId);
        return null;
    }

    public Bitmap getAlbumart(Long album_id) {
        Bitmap bm = null;
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inSampleSize = Math.max(options.outWidth / img.getWidth(), options.outHeight / img.getHeight());
        try {
            final Uri sArtworkUri = Uri
                    .parse("content://media/external/audio/albumart");

            Uri uri = ContentUris.withAppendedId(sArtworkUri, album_id);

            ParcelFileDescriptor pfd = context.getContentResolver()
                    .openFileDescriptor(uri, "r");

            if (pfd != null) {
                FileDescriptor fd = pfd.getFileDescriptor();
                bm = BitmapFactory.decodeFileDescriptor(fd, null, options);
            }
        } catch (Exception ignored) {
        }
        return bm;
    }

    @Override
    protected void done(@Nullable Object result) {
        if (img.getDrawable() == null) {
            img.setImageBitmap(bmp);
            return;
        }
        if (bmp != null) {
            ImageBlurAnimator animator = new ImageBlurAnimator(context, img, 20, bmp);
            animator.animate();
        }
    }
}
