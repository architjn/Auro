package com.architjn.acjmusicplayer.task;

import android.animation.ArgbEvaluator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.AsyncTask;
import android.support.v7.graphics.Palette;
import android.util.Log;

import com.architjn.acjmusicplayer.R;
import com.architjn.acjmusicplayer.elements.adapters.AlbumsAdapter;

/**
 * Created by architjn on 26/06/15.
 */
public class ColorGridTask extends AsyncTask<Void, Void, Void> {

    private Context context;
    private String artPath;
    AlbumsAdapter.SimpleItemViewHolder holder;
    private ValueAnimator colorAnimation;

    public ColorGridTask(Context context, String artPath, AlbumsAdapter.SimpleItemViewHolder holder) {
        this.context = context;
        this.artPath = artPath;
        this.holder = holder;
        holder.realBackground.setBackgroundColor(context.getResources().getColor(R.color.card_background));
    }

    @Override
    protected Void doInBackground(Void... params) {
        Log.v("artPath", artPath);
        Palette.generateAsync(BitmapFactory.decodeFile(artPath),
                new Palette.PaletteAsyncListener() {
                    @Override
                    public void onGenerated(final Palette palette) {
                        Integer colorFrom = context.getResources().getColor(R.color.card_background);
                        Integer colorTo = palette.getVibrantColor(context.getResources().getColor(R.color.card_background));
                        colorAnimation = ValueAnimator.ofObject(new ArgbEvaluator(), colorFrom, colorTo);
                        colorAnimation.setDuration(1000);
                        colorAnimation.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {

                            @Override
                            public void onAnimationUpdate(ValueAnimator animator) {
                                holder.realBackground.setBackgroundColor((Integer) animator.getAnimatedValue());
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
                                    holder.albumName.setTextColor((Integer) animator.getAnimatedValue());
                                }
                            });
                            colorAnimation.start();
                            Integer colorFrom2 = Color.parseColor("#adb2bb");
                            Integer colorTo2 = palette.getVibrantSwatch().getTitleTextColor();
                            colorAnimation = ValueAnimator.ofObject(new ArgbEvaluator(), colorFrom2, colorTo2);
                            colorAnimation.setDuration(800);
                            colorAnimation.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                                @Override
                                public void onAnimationUpdate(ValueAnimator animator) {
                                    holder.albumDesc.setTextColor((Integer) animator.getAnimatedValue());
                                }
                            });
                            colorAnimation.start();
                        } catch (NullPointerException e) {
                            e.printStackTrace();
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
