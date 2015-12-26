package com.architjn.acjmusicplayer.task;

import android.animation.Animator;
import android.animation.ArgbEvaluator;
import android.animation.ValueAnimator;
import android.app.Activity;
import android.app.ActivityManager;
import android.content.ContentUris;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.ParcelFileDescriptor;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v4.content.ContextCompat;
import android.support.v7.graphics.Palette;
import android.view.View;
import android.widget.ImageView;

import com.afollestad.async.Action;
import com.architjn.acjmusicplayer.R;
import com.architjn.acjmusicplayer.utils.ImageBlurAnimator;

import java.io.FileDescriptor;

/**
 * Created by architjn on 29/11/15.
 */
public class PlayerLoader extends Action {

    private Context context;
    private ImageView img;
    private long albumId;
    private Bitmap bmp;
    private View seekHolder;
    private View controlHolder;
    private CollapsingToolbarLayout toolbar;

    public PlayerLoader(Context context, ImageView img, long albumId,
                        View seekHolder, View controlHolder, CollapsingToolbarLayout toolbar) {
        this.context = context;
        this.img = img;
        this.albumId = albumId;
        this.seekHolder = seekHolder;
        this.controlHolder = controlHolder;
        this.toolbar = toolbar;
    }

    @NonNull
    @Override
    public String id() {
        return String.valueOf(albumId);
    }

    @Nullable
    @Override
    protected Object run() throws InterruptedException {
        bmp = getAlbumart(albumId);
        return null;
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
            animateColorChange();
        }
    }

    private void animateColorChange() {
        Palette.generateAsync(bmp,
                new Palette.PaletteAsyncListener() {
                    public ValueAnimator colorAnimation, colorAnimation1;

                    @Override
                    public void onGenerated(final Palette palette) {
                        final int toolbarColor = getAnyColor(palette);
                        colorAnimation = setBasicAnimator(getBackgroundColor(controlHolder),
                                toolbarColor, 2000);
                        colorAnimation.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {

                            @Override
                            public void onAnimationUpdate(ValueAnimator animator) {
                                int temp = (Integer) animator.getAnimatedValue();
                                controlHolder.setBackgroundColor(temp);
                                toolbar.setContentScrimColor(temp);
                            }

                        });
                        colorAnimation.addListener(new Animator.AnimatorListener() {
                            @Override
                            public void onAnimationStart(Animator animator) {
                            }

                            @Override
                            public void onAnimationEnd(Animator animator) {
                                if (Build.VERSION.SDK_INT >= 21) {
                                    ActivityManager.TaskDescription taskDescription = new
                                            ActivityManager.TaskDescription(context.getResources()
                                            .getString(R.string.app_name),
                                            BitmapFactory.decodeResource(context.getResources()
                                                    , R.mipmap.ic_launcher), toolbarColor);
                                    ((Activity) context).setTaskDescription(taskDescription);
                                }
                            }

                            @Override
                            public void onAnimationCancel(Animator animator) {

                            }

                            @Override
                            public void onAnimationRepeat(Animator animator) {

                            }
                        });
                        colorAnimation.start();
                        colorAnimation1 = setBasicAnimator(getBackgroundColor(seekHolder),
                                getDarkColor(getAnyColor(palette)), 2000);
                        colorAnimation1.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {

                            @Override
                            public void onAnimationUpdate(ValueAnimator animator) {
                                int temp = (Integer) animator.getAnimatedValue();
                                seekHolder.setBackgroundColor(temp);
                                toolbar.setStatusBarScrimColor(temp);
                            }

                        });
                        colorAnimation1.start();
                    }
                }
        );
    }

    private ValueAnimator setBasicAnimator(int from, int to, int duration) {
        ValueAnimator colorAnimation = ValueAnimator
                .ofObject(new ArgbEvaluator(), from, to);
        colorAnimation.setDuration(duration);
        return colorAnimation;
    }

    public int getDarkColor(int baseColor) {
        float[] hsv = new float[3];
        Color.colorToHSV(baseColor, hsv);
        hsv[2] *= 0.7f;
        return Color.HSVToColor(hsv);
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
        } catch (Exception e) {
        }
        return bm;
    }

    public int getAnyColor(Palette palette) {
        return palette.getVibrantColor(palette.getDarkVibrantColor(
                palette.getDarkMutedColor(palette.getMutedColor(
                        ContextCompat.getColor(context, R.color.colorPrimary)))));
    }

    public int getBackgroundColor(View view) {
        if (view == null)
            return ContextCompat.getColor(context, R.color.colorPrimary);
        return ((ColorDrawable) view.getBackground()).getColor();
    }
}
