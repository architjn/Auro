package com.architjn.acjmusicplayer.task;

import android.animation.ArgbEvaluator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.graphics.Palette;
import android.view.View;
import android.widget.TextView;

import com.afollestad.async.Action;
import com.architjn.acjmusicplayer.R;
import com.architjn.acjmusicplayer.utils.adapters.AlbumListAdapter;
import com.architjn.acjmusicplayer.utils.adapters.SearchListAdapter;

/**
 * Created by architjn on 18/12/15.
 */
public class AlbumItemLoad extends Action {

    private String artPath;
    private long duration = 800;
    private Context context;
    private TextView name, artist;
    private View bgView;
    private Bitmap bmp;
    private ValueAnimator colorAnimation;

    public AlbumItemLoad(Context context, String artPath, View header) {
        this.context = context;
        this.artPath = artPath;
        name = (TextView) header.findViewById(R.id.album_list_long_name);
        artist = (TextView) header.findViewById(R.id.album_list_long_artist);
        bgView = header.findViewById(R.id.album_grid_header_bg);
    }

    public AlbumItemLoad(Context context, String artPath, AlbumListAdapter.SimpleItemViewHolder holder) {
        this.context = context;
        this.artPath = artPath;
        this.name = holder.name;
        this.artist = holder.artist;
        this.bgView = holder.bottomBg;
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
        bmp = BitmapFactory.decodeFile(artPath);
        Palette.from(bmp).generate(
                new Palette.PaletteAsyncListener() {
                    @Override
                    public void onGenerated(final Palette palette) {
                        try {
                            colorAnimation = setAnimator(ContextCompat.getColor(context, android.R.color.white),
                                    palette.getVibrantColor(palette.getDarkVibrantColor(
                                            palette.getDarkMutedColor(palette.getMutedColor(
                                                    ContextCompat.getColor(context, R.color.colorPrimary))))));
                            colorAnimation.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {

                                @Override
                                public void onAnimationUpdate(ValueAnimator animator) {
                                    bgView.setBackgroundColor((Integer) animator.getAnimatedValue());
                                }

                            });
                            colorAnimation.start();
                            colorAnimation = setAnimator(ContextCompat.getColor(context,
                                    R.color.album_grid_name_default),
                                    palette.getVibrantSwatch().getBodyTextColor());
                            colorAnimation.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                                @Override
                                public void onAnimationUpdate(ValueAnimator animator) {
                                    name.setTextColor((Integer) animator.getAnimatedValue());
                                }
                            });
                            colorAnimation.start();
                            colorAnimation = setAnimator(ContextCompat.getColor(context,
                                    R.color.album_grid_artist_default),
                                    palette.getVibrantSwatch().getTitleTextColor());
                            colorAnimation.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                                @Override
                                public void onAnimationUpdate(ValueAnimator animator) {
                                    artist.setTextColor((Integer) animator.getAnimatedValue());
                                }
                            });
                            colorAnimation.start();
                        } catch (NullPointerException e) {
                            e.printStackTrace();
                            bgView.setBackgroundColor(ContextCompat
                                    .getColor(context, R.color.colorPrimary));
                        }
                    }
                });
        return null;
    }
}
