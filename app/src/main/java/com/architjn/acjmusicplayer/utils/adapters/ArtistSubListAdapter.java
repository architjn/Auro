package com.architjn.acjmusicplayer.utils.adapters;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;
import android.support.v7.graphics.Palette;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.architjn.acjmusicplayer.R;
import com.architjn.acjmusicplayer.ui.layouts.activity.AlbumActivity;
import com.architjn.acjmusicplayer.utils.ListSongs;
import com.architjn.acjmusicplayer.utils.Utils;
import com.architjn.acjmusicplayer.utils.items.Album;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import java.io.File;
import java.util.ArrayList;

/**
 * Created by architjn on 28/11/15.
 */
public class ArtistSubListAdapter extends RecyclerView.Adapter<ArtistSubListAdapter.SimpleItemViewHolder> {

    private static final String TAG = "ArtistSubListAdapter-TAG";
    private ArrayList<Album> items;
    private Context context;

    public ArtistSubListAdapter(Context context, long artistId) {
        this.context = context;
        this.items = ListSongs.getAlbumListOfArtist(context, artistId);
    }

    @Override
    public ArtistSubListAdapter.SimpleItemViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).
                inflate(R.layout.artist_sub_list_item, parent, false);
        return new SimpleItemViewHolder(itemView);
    }

    public int dpToPx(int dp) {
        DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
        return Math.round(dp * (displayMetrics.xdpi / DisplayMetrics.DENSITY_DEFAULT));
    }

    @Override
    public void onBindViewHolder(final ArtistSubListAdapter.SimpleItemViewHolder holder, final int position) {
        holder.name.setText(items.get(position).getAlbumTitle());
        setOnClickListeners(holder, position);
        setAlbumArt(position, holder);
    }

    private void setAlbumArt(final int position, final SimpleItemViewHolder holder) {
        String path = ListSongs.getAlbumArt(context,
                items.get(position).getAlbumId());
        Utils utils = new Utils(context);
        int size = (utils.getWindowWidth() - (2 * utils.dpToPx(1))) / 2;
        if (path != null)
            Picasso.with(context).load(new File(path)).resize(size,
                    size).centerCrop().into(new Target() {
                @Override
                public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                    holder.img.setImageBitmap(bitmap);
                    Palette.generateAsync(bitmap,
                            new Palette.PaletteAsyncListener() {
                                @Override
                                public void onGenerated(final Palette palette) {
                                    holder.nameHolder.setBackgroundColor(palette.getVibrantColor(
                                            palette.getDarkVibrantColor(
                                                    palette.getDarkMutedColor(palette.getMutedColor(
                                                            ContextCompat.getColor(context,
                                                                    R.color.colorPrimary))))));
                                }
                            }
                    );
                }

                @Override
                public void onBitmapFailed(Drawable errorDrawable) {

                }

                @Override
                public void onPrepareLoad(Drawable placeHolderDrawable) {

                }
            });
        else {
            holder.img.setImageBitmap(utils.getBitmapOfVector(R.drawable.default_art, size, size));
            holder.nameHolder.setBackgroundColor(ContextCompat.getColor(context, R.color.colorPrimary));
        }
    }

    private void setOnClickListeners(SimpleItemViewHolder holder, final int position) {
        holder.mainView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new Thread(new Runnable() {
                    public void run() {
                        Intent i = new Intent(context, AlbumActivity.class);
                        i.putExtra("albumId", items.get(position).getAlbumId());
                        i.putExtra("albumName", items.get(position).getAlbumTitle());
                        context.startActivity(i);
                    }
                }).start();
            }
        });
    }

    public void updateList(ArrayList<Album> albums) {
        this.items = albums;
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public class SimpleItemViewHolder extends RecyclerView.ViewHolder {

        public TextView name;
        public View mainView, nameHolder;
        public ImageView img;

        public SimpleItemViewHolder(View itemView) {
            super(itemView);
            mainView = itemView;
            nameHolder = itemView.findViewById(R.id.artist_sub_name_holder);
            img = (ImageView) itemView.findViewById(R.id.artist_sub_img);
            name = (TextView) itemView.findViewById(R.id.artist_sub_name);
        }
    }

}
