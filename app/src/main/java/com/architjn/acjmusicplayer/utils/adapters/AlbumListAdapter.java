package com.architjn.acjmusicplayer.utils.adapters;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.architjn.acjmusicplayer.R;
import com.architjn.acjmusicplayer.task.AlbumItemLoad;
import com.architjn.acjmusicplayer.ui.layouts.activity.AlbumActivity;
import com.architjn.acjmusicplayer.utils.items.Album;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.util.ArrayList;

/**
 * Created by architjn on 28/11/15.
 */
public class AlbumListAdapter extends RecyclerView.Adapter<AlbumListAdapter.SimpleItemViewHolder> {
    private static final int ITEM_VIEW_TYPE_HEADER = 0;
    private static final int ITEM_VIEW_TYPE_ITEM = 1;
    private static final String TAG = "AlbumListAdapter-TAG";

    private ArrayList<Album> items;
    private Context context;
    private View header;

    public AlbumListAdapter(Context context, ArrayList<Album> items, View header) {
        this.context = context;
        this.header = header;
        this.items = items;
    }


    public boolean isHeader(int position) {
        return position == 0;
    }

    @Override
    public AlbumListAdapter.SimpleItemViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == ITEM_VIEW_TYPE_HEADER) {
            return new SimpleItemViewHolder(header);
        }
        View itemView = LayoutInflater.from(parent.getContext()).
                inflate(R.layout.album_grid_item, parent, false);
        return new SimpleItemViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final AlbumListAdapter.SimpleItemViewHolder holder, final int position) {
        if (isHeader(position)) {
            return;
        }
        holder.bottomBg.setBackgroundColor(Color.parseColor("#ffffff"));
        setArt(holder, position - 1);
        holder.name.setText(items.get(position - 1).getAlbumTitle());
        holder.artist.setText(items.get(position - 1).getAlbumArtist());
        holder.mainView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(context, AlbumActivity.class);
                i.putExtra("albumName", items.get(position - 1).getAlbumTitle());
                i.putExtra("albumId", items.get(position - 1).getAlbumId());
                context.startActivity(i);
            }
        });
    }

    private void setArt(SimpleItemViewHolder holder, int position) {
        if (items.get(position).getAlbumArtPath() != null) {
            new AlbumItemLoad(context, items.get(position).getAlbumArtPath(), holder).execute();
            setAlbumArt(position, holder);
        } else {
            int colorPrimary = ContextCompat
                    .getColor(context, R.color.colorPrimary);
            holder.img.setImageDrawable(new ColorDrawable(colorPrimary));
            holder.bottomBg.setBackgroundColor(colorPrimary);
        }
    }

    private void setAlbumArt(int position, SimpleItemViewHolder holder) {
        String art = items.get(position).getAlbumArtPath();
        if (art != null)
            Picasso.with(context).load(new File(art)).resize(dpToPx(180),
                    dpToPx(180)).centerCrop().into(holder.img);
        else Picasso.with(context).load(R.drawable.default_art).into(holder.img);
    }

    public int dpToPx(int dp) {
        DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
        int px = Math.round(dp * (displayMetrics.xdpi / DisplayMetrics.DENSITY_DEFAULT));
        return px;
    }

    @Override
    public int getItemCount() {
        return items.size() + 1;
    }

    @Override
    public int getItemViewType(int position) {
        return isHeader(position) ? ITEM_VIEW_TYPE_HEADER : ITEM_VIEW_TYPE_ITEM;
    }

    public class SimpleItemViewHolder extends RecyclerView.ViewHolder {

        public TextView name, artist;
        public ImageView img;
        public View bottomBg, mainView;

        public SimpleItemViewHolder(View itemView) {
            super(itemView);
            mainView = itemView;
            bottomBg = itemView.findViewById(R.id.album_list_bottom);
            name = (TextView) itemView.findViewById(R.id.album_list_name);
            artist = (TextView) itemView.findViewById(R.id.album_list_artist);
            img = (ImageView) itemView.findViewById(R.id.album_list_img);
        }
    }

}
