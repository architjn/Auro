package com.architjn.acjmusicplayer.task;

import android.animation.ArgbEvaluator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.AsyncTask;
import android.support.v7.graphics.Palette;

import com.architjn.acjmusicplayer.R;
import com.architjn.acjmusicplayer.utils.adapters.AlbumListAdapter;

/**
 * Created by architjn on 29/11/15.
 */
public class AlbumItemLoad extends AsyncTask<Void, Void, Void> {

    private Context context;
    private String artPath;
    private AlbumListAdapter.SimpleItemViewHolder holder;
    private ValueAnimator colorAnimation;
    private Bitmap bmp;

    public AlbumItemLoad(Context context, String artPath, AlbumListAdapter.SimpleItemViewHolder holder) {
        this.context = context;
        this.artPath = artPath;
        this.holder = holder;
    }

    @Override
    protected Void doInBackground(Void... voids) {
        bmp = BitmapFactory.decodeFile(artPath);
        Palette.generateAsync(bmp,
                new Palette.PaletteAsyncListener() {
                    @Override
                    public void onGenerated(final Palette palette) {
                        Integer colorFrom = context.getResources().getColor(R.color.colorPrimary);
                        Integer colorTo = palette.getVibrantColor(palette.getDarkVibrantColor(
                                palette.getDarkMutedColor(palette.getMutedColor(
                                        context.getResources().getColor(R.color.colorPrimary)))));
                        colorAnimation = ValueAnimator.ofObject(new ArgbEvaluator(), colorFrom, colorTo);
                        colorAnimation.setDuration(800);
                        colorAnimation.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {

                            @Override
                            public void onAnimationUpdate(ValueAnimator animator) {
                                holder.bottomBg.setBackgroundColor((Integer) animator.getAnimatedValue());
                            }

                        });
                        colorAnimation.start();
                        try {
                            Integer colorFrom1 = Color.parseColor("#ffffff");
                            Integer colorTo1 = palette.getVibrantSwatch().getBodyTextColor();
                            colorAnimation = ValueAnimator.ofObject(new ArgbEvaluator(), colorFrom1, colorTo1);
                            colorAnimation.setDuration(800);
                            colorAnimation.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                                @Override
                                public void onAnimationUpdate(ValueAnimator animator) {
                                    holder.name.setTextColor((Integer) animator.getAnimatedValue());
                                }
                            });
                            colorAnimation.start();
                            Integer colorFrom2 = Color.parseColor("#E5E5E5");
                            Integer colorTo2 = palette.getVibrantSwatch().getTitleTextColor();
                            colorAnimation = ValueAnimator.ofObject(new ArgbEvaluator(), colorFrom2, colorTo2);
                            colorAnimation.setDuration(800);
                            colorAnimation.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                                @Override
                                public void onAnimationUpdate(ValueAnimator animator) {
                                    holder.artist.setTextColor((Integer) animator.getAnimatedValue());
                                }
                            });
                            colorAnimation.start();
                        } catch (NullPointerException e) {
                            e.printStackTrace();
                            holder.bottomBg.setBackgroundColor(context.getResources()
                                    .getColor(R.color.colorPrimary));
                        }
                    }
                });
        return null;
    }


    @Override
    protected void onPostExecute(Void aVoid) {
        super.onPostExecute(aVoid);
    }
}
