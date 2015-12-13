package com.architjn.acjmusicplayer.task;

import android.content.ContentUris;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.ParcelFileDescriptor;
import android.widget.ImageView;

import com.architjn.acjmusicplayer.utils.ImageBlurAnimator;

import java.io.FileDescriptor;

/**
 * Created by architjn on 29/11/15.
 */
public class PlayerLoader extends AsyncTask<Long, Void, Void> {

    private Context context;
    private ImageView img;
    private Bitmap bmp;

    public PlayerLoader(Context context, ImageView img) {
        this.context = context;
        this.img = img;
    }

    @Override
    protected Void doInBackground(Long... ids) {
        bmp = getAlbumart(ids[0]);
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
        if (img.getDrawable() == null) {
            img.setImageBitmap(bmp);
            return;
        }
        ImageBlurAnimator animator = new ImageBlurAnimator(context, img, 20, bmp);
        animator.animate();
        super.onPostExecute(aVoid);
    }
}
