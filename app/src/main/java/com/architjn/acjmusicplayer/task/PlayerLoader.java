package com.architjn.acjmusicplayer.task;

import android.animation.Animator;
import android.animation.ArgbEvaluator;
import android.animation.ValueAnimator;
import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v4.content.ContextCompat;
import android.support.v7.graphics.Palette;
import android.view.View;
import android.widget.ImageView;

import com.architjn.acjmusicplayer.R;
import com.architjn.acjmusicplayer.utils.ImageBlurAnimator;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import java.io.File;

/**
 * Created by architjn on 29/11/15.
 */
public class PlayerLoader {

    private Context context;
    private ImageView img;
    private String path;
    private Bitmap bmp;
    private View seekHolder;
    private View controlHolder;
    private CollapsingToolbarLayout toolbar;

    public PlayerLoader(Context context, ImageView img, String path,
                        View seekHolder, View controlHolder, CollapsingToolbarLayout toolbar) {
        this.context = context;
        this.img = img;
        this.path = path;
        this.seekHolder = seekHolder;
        this.controlHolder = controlHolder;
        this.toolbar = toolbar;
        load();
    }

    private void load() {

        if (path != null)
            Picasso.with(context).load(new File(path)).into(new Target() {
                @Override
                public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                    if (bitmap != null) {
                        bmp = bitmap;
                        ImageBlurAnimator animator = new ImageBlurAnimator(context, img, 20, bitmap);
                        animator.animate();
                        animateColorChange();
                    }
                }

                @Override
                public void onBitmapFailed(Drawable errorDrawable) {
                    img.setImageResource(R.drawable.default_art);
                }

                @Override
                public void onPrepareLoad(Drawable placeHolderDrawable) {

                }
            });
    }

    private void animateColorChange() {
        Palette.from(bmp).generate(
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
