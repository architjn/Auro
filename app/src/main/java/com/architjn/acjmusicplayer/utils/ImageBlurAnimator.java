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
        Bitmap bmp;
        try {
            bmp = ((BitmapDrawable) imgView.getDrawable()).getBitmap();
        } catch (ClassCastException | NullPointerException e) {
            imgView.setImageBitmap(newBitmap);
            return;
        }
        Integer elevationFrom = 0;
        Integer elevationTo = animationScale;
        ValueAnimator colorAnimation =
                ValueAnimator.ofObject(
                        new ArgbEvaluator(), elevationFrom, elevationTo);
        final Bitmap finalBmp = bmp;
        colorAnimation.addUpdateListener(
                new ValueAnimator.AnimatorUpdateListener() {
                    @Override
                    public void onAnimationUpdate(ValueAnimator animator) {
                        try {
                            imgView.setImageBitmap(createBitmap_ScriptIntrinsicBlur(finalBmp,
                                    (Integer) animator.getAnimatedValue()));
                        } catch (NullPointerException e) {
                            e.printStackTrace();
                            imgView.setImageBitmap(finalBmp);
                        }
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
                        try {
                            imgView.setImageBitmap(createBitmap_ScriptIntrinsicBlur(newBitmap,
                                    (Integer) animator.getAnimatedValue()));
                        } catch (NullPointerException e) {
                            e.printStackTrace();
                            imgView.setImageBitmap(newBitmap);
                        }
                    }

                });
        colorAnimation1.start();
    }
/*
 Code below can be used for converting VectorDrawable to Bitmap
 */
//    private Bitmap getBitmapOfVector(int id) {
//        Utils utils = new Utils(context);
//        Drawable vectorDrawable = context.getDrawable(id);
//        int h = utils.dpToPx((int) context.getResources()
//                .getDimension(R.dimen.parallax_img_height_player));
//        int w = utils.getWindowWidth();
//        if (vectorDrawable != null)
//            vectorDrawable.setBounds(0, 0, w, h);
//        Bitmap bm = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
//        Canvas canvas = new Canvas(bm);
//        if (vectorDrawable != null)
//            vectorDrawable.draw(canvas);
//        return bm;
//    }

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
