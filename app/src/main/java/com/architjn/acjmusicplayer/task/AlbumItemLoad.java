package com.architjn.acjmusicplayer.task;

import android.animation.ArgbEvaluator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.graphics.Palette;
import android.view.View;
import android.widget.TextView;

import com.afollestad.async.Action;
import com.architjn.acjmusicplayer.R;
import com.architjn.acjmusicplayer.utils.Utils;
import com.architjn.acjmusicplayer.utils.adapters.AlbumListAdapter;
import com.architjn.acjmusicplayer.utils.adapters.SearchListAdapter;

import java.io.File;

/**
 * Created by architjn on 18/12/15.
 */
public class AlbumItemLoad extends Action {

    private String artPath;
    private OnColorFetchListener onColorFetchListener;
    private int size;
    private Context context;
    private TextView name, artist;
    private View bgView;
    private ValueAnimator colorAnimation;

    public AlbumItemLoad(Context context, String artPath, View header) {
        this.context = context;
        this.artPath = artPath;
        name = (TextView) header.findViewById(R.id.album_list_long_name);
        artist = (TextView) header.findViewById(R.id.album_list_long_artist);
        bgView = header.findViewById(R.id.album_grid_header_bg);
    }

    public AlbumItemLoad(Context context, String artPath, SearchListAdapter.SimpleItemViewHolder holder) {
        this.context = context;
        this.artPath = artPath;
        this.name = holder.albumName;
        this.artist = holder.albumArtist;
        this.bgView = holder.bgView;
    }

    private ValueAnimator setAnimator(int colorFrom, int colorTo) {
        ValueAnimator colorAnimation = ValueAnimator.ofObject(new ArgbEvaluator(), colorFrom, colorTo);
        long duration = 800;
        colorAnimation.setDuration(duration);
        return colorAnimation;
    }

    @NonNull
    @Override
    public String id() {
        return artPath;
    }

    @Nullable
    @Override
    protected Object run() throws InterruptedException {
        Bitmap bmp;
        if (artPath == null || !isFilePathExist(artPath))
            bmp = new Utils(context).getBitmapOfVector(R.drawable.default_art, size, size);
        else
            bmp = BitmapFactory.decodeFile(artPath);
        Palette.from(bmp).generate(
                new Palette.PaletteAsyncListener() {
                    @Override
                    public void onGenerated(final Palette palette) {
                        //animate after 0.7 secs
                        final int[] colors = getAvailableColor(palette);
                        if (onColorFetchListener != null)
                            onColorFetchListener.colorFetched(colors);
                        if (!AlbumListAdapter.onceAnimated)
                            new Handler().postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    try {
                                        animateViews(colors[0], colors[1], colors[2]);
                                    } catch (NullPointerException e) {
                                        e.printStackTrace();
                                        setDefaultColors();
                                    }
                                }
                            }, 700);
                        else {
                            bgView.setBackgroundColor(colors[0]);
                            name.setTextColor(colors[1]);
                            artist.setTextColor(colors[2]);
                        }
                    }
                });
        return null;
    }

    private boolean isFilePathExist(String albumArtPath) {
        File imgFile = new File(albumArtPath);
        return imgFile.exists();
    }

    private void setDefaultColors() {
        bgView.setBackgroundColor(ContextCompat
                .getColor(context, R.color.colorPrimary));
        name.setTextColor(0xffffffff);
        artist.setTextColor(0xffe5e5e5);
    }

    private void animateViews(int colorBg, int colorName, int colorArtist) {
        colorAnimation = setAnimator(0xffe5e5e5,
                colorBg);
        colorAnimation.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {

            @Override
            public void onAnimationUpdate(ValueAnimator animator) {
                bgView.setBackgroundColor((Integer) animator.getAnimatedValue());
            }

        });
        colorAnimation.start();
        colorAnimation = setAnimator(ContextCompat.getColor(context,
                R.color.album_grid_name_default),
                colorName);
        colorAnimation.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animator) {
                name.setTextColor((Integer) animator.getAnimatedValue());
            }
        });
        colorAnimation.start();
        colorAnimation = setAnimator(ContextCompat.getColor(context,
                R.color.album_grid_artist_default),
                colorArtist);
        colorAnimation.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animator) {
                artist.setTextColor((Integer) animator.getAnimatedValue());
            }
        });
        colorAnimation.start();
    }

    public interface OnColorFetchListener {
        void colorFetched(int color[]);
    }

    private int[] getAvailableColor(Palette palette) {
        int[] temp = new int[3];
        if (palette.getVibrantSwatch() != null) {
            temp[0] = palette.getVibrantSwatch().getRgb();
            temp[1] = palette.getVibrantSwatch().getBodyTextColor();
            temp[2] = palette.getVibrantSwatch().getTitleTextColor();
        } else if (palette.getDarkVibrantSwatch() != null) {
            temp[0] = palette.getDarkVibrantSwatch().getRgb();
            temp[1] = palette.getDarkVibrantSwatch().getBodyTextColor();
            temp[2] = palette.getDarkVibrantSwatch().getTitleTextColor();
        } else if (palette.getDarkMutedSwatch() != null) {
            temp[0] = palette.getDarkMutedSwatch().getRgb();
            temp[1] = palette.getDarkMutedSwatch().getBodyTextColor();
            temp[2] = palette.getDarkMutedSwatch().getTitleTextColor();
        } else {
            temp[0] = ContextCompat.getColor(context, R.color.colorPrimary);
            temp[1] = ContextCompat.getColor(context, android.R.color.white);
            temp[2] = 0xffe5e5e5;
        }
        return temp;
    }

}
