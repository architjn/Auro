package com.architjn.acjmusicplayer.utils;

import android.animation.ArgbEvaluator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.renderscript.Allocation;
import android.renderscript.Element;
import android.renderscript.RenderScript;
import android.renderscript.ScriptIntrinsicBlur;
import android.widget.ImageView;

/**
 * Created by architjn on 12/12/15.
 */
public class ImageBlurAnimator {

    private Context context;
    private ImageView imgView;
    private int animationScale;
    private Bitmap newBitmap;

    public ImageBlurAnimator(Context context, ImageView imgView, int animationScale, Bitmap newBitmap) {
        this.context = context;
        this.imgView = imgView;
        this.animationScale = animationScale;
        this.newBitmap = newBitmap;
    }

    public void animate() {
        final Bitmap bmp = ((BitmapDrawable) imgView.getDrawable()).getBitmap();
        Integer elevationFrom = 0;
        Integer elevationTo = animationScale;
        ValueAnimator colorAnimation =
                ValueAnimator.ofObject(
                        new ArgbEvaluator(), elevationFrom, elevationTo);
        colorAnimation.addUpdateListener(
                new ValueAnimator.AnimatorUpdateListener() {
                    @Override
                    public void onAnimationUpdate(ValueAnimator animator) {
                        imgView.setImageBitmap(createBitmap_ScriptIntrinsicBlur(bmp,
                                (Integer) animator.getAnimatedValue()));
                    }

                });
        colorAnimation.start();
        Integer elevationFrom1 = animationScale;
        Integer elevationTo1 = 0;
        ValueAnimator colorAnimation1 =
                ValueAnimator.ofObject(
                        new ArgbEvaluator(), elevationFrom1, elevationTo1);
        colorAnimation1.addUpdateListener(
                new ValueAnimator.AnimatorUpdateListener() {
                    @Override
                    public void onAnimationUpdate(ValueAnimator animator) {
                        imgView.setImageBitmap(createBitmap_ScriptIntrinsicBlur(newBitmap,
                                (Integer) animator.getAnimatedValue()));
                    }

                });
        colorAnimation1.start();
    }

    private Bitmap createBitmap_ScriptIntrinsicBlur(Bitmap src, float r) {
        //Radius range (0 < r <= 25)
        if (r <= 0) {
            r = 0.1f;
        } else if (r > 25) {
            r = 25.0f;
        }

        Bitmap bitmap = Bitmap.createBitmap(
                src.getWidth(), src.getHeight(),
                Bitmap.Config.ARGB_8888);

        RenderScript renderScript = RenderScript.create(context);

        Allocation blurInput = Allocation.createFromBitmap(renderScript, src);
        Allocation blurOutput = Allocation.createFromBitmap(renderScript, bitmap);

        ScriptIntrinsicBlur blur = ScriptIntrinsicBlur.create(renderScript,
                Element.U8_4(renderScript));
        blur.setInput(blurInput);
        blur.setRadius(r);
        blur.forEach(blurOutput);

        blurOutput.copyTo(bitmap);
        renderScript.destroy();
        return bitmap;
    }


}
