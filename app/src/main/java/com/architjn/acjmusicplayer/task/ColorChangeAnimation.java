package com.architjn.acjmusicplayer.task;

import android.animation.ArgbEvaluator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.ColorDrawable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.support.v7.graphics.Palette;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.afollestad.async.Action;
import com.architjn.acjmusicplayer.R;

/**
 * Created by architjn on 13/12/15.
 */
public abstract class ColorChangeAnimation extends Action {

    private Context context;
    private LinearLayout detailHolder;
    private TextView textView;
    private ImageView img;
    private String artPath;
    private boolean noBitmap;
    private Integer colorFrom;
    private ValueAnimator colorAnimation;
    private ValueAnimator colorAnimation1;
    private ValueAnimator colorAnimation2;

    public ColorChangeAnimation(Context context, LinearLayout detailHolder, String artPath) {
        this.context = context;
        this.detailHolder = detailHolder;
        this.artPath = artPath;
        colorFrom = ((ColorDrawable) detailHolder.getBackground()).getColor();
        noBitmap = false;
    }

    public ColorChangeAnimation(Context context, LinearLayout detailHolder,
                                TextView textView, ImageView img, String artPath) {
        this.context = context;
        this.detailHolder = detailHolder;
        this.textView = textView;
        this.img = img;
        this.artPath = artPath;
        colorFrom = ((ColorDrawable) detailHolder.getBackground()).getColor();
        noBitmap = false;
    }

    @NonNull
    @Override
    public String id() {
        return artPath;
    }

    @Nullable
    @Override
    protected Object run() throws InterruptedException {
        try {
            Bitmap bmp = BitmapFactory.decodeFile(artPath);
            Palette.from(bmp).generate(
                    new Palette.PaletteAsyncListener() {
                        @Override
                        public void onGenerated(final Palette palette) {
                            Integer colorTo = palette.getVibrantColor(palette.getDarkVibrantColor(
                                    palette.getDarkMutedColor(palette.getMutedColor(
                                            ContextCompat.getColor(context,
                                                    R.color.colorPrimary)))));
                            onColorFetched(palette, colorTo);
                            colorAnimation = ValueAnimator.ofObject(new ArgbEvaluator(), colorFrom, colorTo);
                            colorAnimation.setDuration(2000);
                            colorAnimation.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {

                                @Override
                                public void onAnimationUpdate(ValueAnimator animator) {
                                    detailHolder.setBackgroundColor((Integer) animator.getAnimatedValue());
                                }

                            });
                            colorAnimation.start();
                            if (textView != null) {
                                Integer colorTo1 = 0xffffffff;
                                if (palette.getVibrantSwatch() != null)
                                    colorTo1 = palette.getVibrantSwatch().getBodyTextColor();
                                colorAnimation1 = ValueAnimator.ofObject(new ArgbEvaluator(),
                                        textView.getCurrentTextColor(), colorTo1);
                                colorAnimation1.setDuration(2000);
                                colorAnimation1.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {

                                    @Override
                                    public void onAnimationUpdate(ValueAnimator animator) {
                                        textView.setTextColor((Integer) animator.getAnimatedValue());
                                    }

                                });
                                colorAnimation1.start();
                                colorAnimation2 = ValueAnimator.ofObject(new ArgbEvaluator(),
                                        textView.getCurrentTextColor(), colorTo1);
                                colorAnimation2.setDuration(2000);
                                colorAnimation2.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {

                                    @Override
                                    public void onAnimationUpdate(ValueAnimator animator) {
                                        DrawableCompat.setTint(img.getDrawable(), (Integer) animator.getAnimatedValue());
                                    }

                                });
                                colorAnimation2.start();
                            }
                        }
                    }
            );
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
            noBitmap = true;
        }
        return null;
    }

    @Override
    protected void done(@Nullable Object result) {
        if (noBitmap) {
            //Animate to default color
            colorAnimation = ValueAnimator.ofObject(new ArgbEvaluator(), colorFrom,
                    ContextCompat.getColor(context, R.color.color400));
            colorAnimation.setDuration(2000);

            colorAnimation.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {

                @Override
                public void onAnimationUpdate(ValueAnimator animator) {
                    detailHolder.setBackgroundColor((Integer) animator.getAnimatedValue());
                }

            });
            colorAnimation.start();
        }
    }

    public abstract void onColorFetched(Palette palette, Integer colorPrimary);

}
