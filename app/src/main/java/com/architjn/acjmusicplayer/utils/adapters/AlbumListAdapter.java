package com.architjn.acjmusicplayer.utils.adapters;

import android.animation.ArgbEvaluator;
import android.animation.ValueAnimator;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.util.Pair;
import android.support.v4.view.animation.FastOutSlowInInterpolator;
import android.support.v7.graphics.Palette;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TableRow;
import android.widget.TextView;

import com.afollestad.async.Action;
import com.architjn.acjmusicplayer.R;
import com.architjn.acjmusicplayer.ui.layouts.activity.AlbumActivity;
import com.architjn.acjmusicplayer.utils.ColorCache;
import com.architjn.acjmusicplayer.utils.Utils;
import com.architjn.acjmusicplayer.utils.items.Album;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.util.ArrayList;

/**
 * Created by architjn on 28/11/15.
 */
public class AlbumListAdapter extends RecyclerView.Adapter<AlbumListAdapter.SimpleItemViewHolder> {
    private static final String TAG = "AlbumListAdapter-TAG";
    private static final long ANIM_DUR = 500;
    private static int ANIM_TILL = 0;
    public static boolean onceAnimated;
    private final Context context;
    private final ArrayList<Album> items;
    private final RecyclerView gv;

    public AlbumListAdapter(Context context, ArrayList<Album> items,
                            RecyclerView gv) {
        this.context = context;
        this.items = items;
        this.gv = gv;
    }

    @Override
    public AlbumListAdapter.SimpleItemViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).
                inflate(R.layout.album_grid_item, parent, false);
        return new SimpleItemViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final AlbumListAdapter.SimpleItemViewHolder holder, final int position) {
        holder.name.setText(items.get(position).getAlbumTitle());
        holder.artist.setText(items.get(position).getAlbumArtist());
        int size = setSize(holder);
        if (position == 0 && ANIM_TILL == 0)
            setAnimTill(size);
        setArtistImg(holder, position, size);
        holder.mainView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                gv.smoothScrollToPosition(position);
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        openAlbum(holder, position);
                    }
                }, 100);
            }
        });
    }

    private void setArtistImg(final SimpleItemViewHolder holder, final int position, final int size) {
        String path = items.get(position).getAlbumArtPath();
        if (isPathValid(path))
            Picasso.with(context).load(new File(path))
                    .centerCrop().resize(size, size).into(holder.img);
        else
            holder.img.setImageBitmap(new Utils(context)
                    .getBitmapOfVector(R.drawable.default_art, size, size));
        handleRevealAnimation(holder, position);
        handleColorAnimation(holder, path, size, position);
    }

    private void handleRevealAnimation(SimpleItemViewHolder holder, int position) {
        if (!onceAnimated && position < ANIM_TILL)
            animateEnter(holder, position);
        else onceAnimated = true;
    }

    private void handleColorAnimation(final SimpleItemViewHolder holder, String path, int size, int position) {
        if (ColorCache.getInstance().getLru().get(items.get(position)
                .getAlbumId()) != null && onceAnimated) {
            int[] colors = ColorCache.getInstance().getLru().get(items.get(position)
                    .getAlbumId());
            holder.bottomBg.setBackgroundColor(colors[0]);
            holder.name.setTextColor(colors[1]);
            holder.artist.setTextColor(colors[2]);
            holder.defaultAlbumColor = colors[0];
        } else {
            new ArtHandler(path, holder, size, items.get(position).getAlbumId(), position) {
                @Override
                public void onColorFetched(int[] colors, long albumId) {
                    ColorCache.getInstance().getLru().put(albumId, colors);
                    holder.defaultAlbumColor = colors[0];
                }
            }.execute();
        }
    }

    private void animateEnter(SimpleItemViewHolder holder, int position) {
        holder.mainView.setAlpha(0f);
        holder.mainView.setTranslationY(800.0f);
        holder.mainView.animate()
                .setInterpolator(new FastOutSlowInInterpolator())
                .translationY(0.0f)
                .alpha(1.0f)
                .setDuration(ANIM_DUR)
                .setStartDelay(10 + (position * 100))
                .start();
    }

    private void openAlbum(SimpleItemViewHolder holder, int position) {
        Intent i = new Intent(context, AlbumActivity.class);
        i.putExtra("albumName", items.get(position).getAlbumTitle());
        i.putExtra("albumId", items.get(position).getAlbumId());
        i.putExtra("albumColor", holder.defaultAlbumColor);
        ActivityOptionsCompat options = ActivityOptionsCompat.makeSceneTransitionAnimation(
                (Activity) context,
                new Pair<View, String>(holder.img, "transition:imgholder"),
                new Pair<View, String>(holder.mainView.findViewById(R.id.album_list_bottom),
                        "transition:nameholder")
        );
        ActivityCompat.startActivity((Activity) context, i, options.toBundle());
    }

    private int setSize(SimpleItemViewHolder holder) {
        Utils utils = new Utils(context);
        int size = (utils.getWindowWidth()
                - utils.dpToPx(1)) / 2;
        TableRow.LayoutParams layoutParams = new TableRow.LayoutParams(size, size);
        holder.img.setLayoutParams(layoutParams);
        return size;
    }

    private boolean fileExist(String albumArtPath) {
        File imgFile = new File(albumArtPath);
        return imgFile.exists();
    }

    public boolean isPathValid(String path) {
        return path != null && fileExist(path);
    }

    public int dpToPx(int dp) {
        DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
        return Math.round(dp * (displayMetrics.xdpi / DisplayMetrics.DENSITY_DEFAULT));
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public void setAnimTill(int size) {
        Log.v(TAG, String.valueOf(gv.getHeight()));
        Log.v(TAG, String.valueOf(new Utils(context).getWindowHeight()));
        int count = (int) ((gv.getHeight() / (size +
                context.getResources().getDimension(R.dimen.album_grid_text_panel_height)
                + dpToPx(1))) * 2);
        ANIM_TILL = count;
        Log.v(TAG, String.valueOf(count));
    }

    public class SimpleItemViewHolder extends RecyclerView.ViewHolder {

        public TextView name, artist;
        public ImageView img;
        public View bottomBg, mainView;
        public int defaultAlbumColor;

        public SimpleItemViewHolder(View itemView) {
            super(itemView);
            mainView = itemView;
            bottomBg = itemView.findViewById(R.id.album_list_bottom);
            name = (TextView) itemView.findViewById(R.id.album_list_name);
            artist = (TextView) itemView.findViewById(R.id.album_list_artist);
            img = (ImageView) itemView.findViewById(R.id.album_list_img);
        }

    }

    private class ArtHandler extends Action {

        private Bitmap bitmap;
        private String path;
        private SimpleItemViewHolder holder;
        private int size;
        private long albumId;
        private int position;
        private ValueAnimator colorAnimation, colorAnimation1, colorAnimation2;

        public ArtHandler(String path, SimpleItemViewHolder holder,
                          int size, long albumId, int position) {
            this.path = path;
            this.holder = holder;
            this.size = size;
            this.albumId = albumId;
            this.position = position;
        }

        @NonNull
        @Override
        public String id() {
            return TAG;
        }

        @Nullable
        @Override
        protected Object run() throws InterruptedException {
            if (!onceAnimated)
                Thread.sleep(ANIM_DUR + (100 * position));
            getBitmap();
            Palette.from(bitmap).generate(new Palette.PaletteAsyncListener() {
                @Override
                public void onGenerated(Palette palette) {
                    final int[] colors = getAvailableColor(palette);
                    onColorFetched(colors, albumId);
                    holder.bottomBg.setBackgroundColor(colors[0]);
                    holder.name.setTextColor(colors[1]);
                    holder.artist.setTextColor(colors[2]);
                    animateViews(colors[0], colors[1], colors[2]);
                }
            });
            return null;
        }

        @Override
        protected void done(@Nullable Object result) {
            holder.img.setImageBitmap(bitmap);
        }

        private void animateViews(int colorBg, int colorName, int colorArtist) {
            colorAnimation = setAnimator(0xffe5e5e5,
                    colorBg);
            colorAnimation.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {

                @Override
                public void onAnimationUpdate(ValueAnimator animator) {
                    holder.bottomBg.setBackgroundColor((Integer) animator.getAnimatedValue());
                }

            });
            colorAnimation.start();
            colorAnimation1 = setAnimator(ContextCompat.getColor(context,
                    R.color.album_grid_name_default),
                    colorName);
            colorAnimation1.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator animator) {
                    holder.name.setTextColor((Integer) animator.getAnimatedValue());
                }
            });
            colorAnimation1.start();
            colorAnimation2 = setAnimator(ContextCompat.getColor(context,
                    R.color.album_grid_artist_default),
                    colorArtist);
            colorAnimation2.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator animator) {
                    holder.artist.setTextColor((Integer) animator.getAnimatedValue());
                }
            });
            colorAnimation2.start();
        }

        private ValueAnimator setAnimator(int colorFrom, int colorTo) {
            ValueAnimator colorAnimation = ValueAnimator.ofObject(new ArgbEvaluator(), colorFrom, colorTo);
            long duration = 800;
            colorAnimation.setDuration(duration);
            return colorAnimation;
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

        public void getBitmap() {
            if (path == null || !fileExist(path))
                bitmap = new Utils(context)
                        .getBitmapOfVector(R.drawable.default_art, size, size);
            else {
                BitmapFactory.Options options = new BitmapFactory.Options();
                options.inJustDecodeBounds = false;
                options.inPreferredConfig = Bitmap.Config.RGB_565;
                options.inDither = true;
                bitmap = BitmapFactory.decodeFile(path);
            }
        }

        //Will be used for overriding
        public void onColorFetched(int[] colors, long albumId) {
        }

    }
}
