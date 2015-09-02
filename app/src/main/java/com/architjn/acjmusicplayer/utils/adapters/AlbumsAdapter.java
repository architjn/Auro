package com.architjn.acjmusicplayer.utils.adapters;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.util.Pair;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.architjn.acjmusicplayer.R;
import com.architjn.acjmusicplayer.utils.items.AlbumListItem;
import com.architjn.acjmusicplayer.task.ColorGridTask;
import com.architjn.acjmusicplayer.ui.layouts.activity.AlbumActivity;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.util.List;

public class AlbumsAdapter extends RecyclerView.Adapter<AlbumsAdapter.SimpleItemViewHolder> {

    private final List<AlbumListItem> items;
    private Context context;

    public final static class SimpleItemViewHolder extends RecyclerView.ViewHolder {
        public TextView albumName, albumDesc;
        public ImageView albumArt;
        public View realBackground, mainView;

        public SimpleItemViewHolder(View view) {
            super(view);

            albumName = (TextView) view.findViewById(R.id.grid_name);
            albumDesc = (TextView) view.findViewById(R.id.grid_desc);
            albumArt = (ImageView) view.findViewById(R.id.grid_art);
            mainView = view;
            realBackground = view.findViewById(R.id.real_background);
        }
    }

    public AlbumsAdapter(Context context, List<AlbumListItem> items) {
        this.context = context;
        this.items = items;
    }

    @Override
    public AlbumsAdapter.SimpleItemViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).
                inflate(R.layout.grid_item, parent, false);


        return new SimpleItemViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final SimpleItemViewHolder holder, final int position) {

        holder.albumName.setText(items.get(position).getName());
        holder.albumDesc.setText(items.get(position).getDesc());
        int backCardColor = context.getResources().getColor(R.color.card_background);
        if (((ColorDrawable) holder.realBackground.getBackground()).getColor() != backCardColor)
            holder.realBackground.setBackgroundColor(backCardColor);
        try {
            Picasso.with(context).load(new File(items.get(position).getArtString()))
                    .error(R.drawable.default_artwork_dark)
                    .into(holder.albumArt, new Callback() {
                        @Override
                        public void onSuccess() {
                            new ColorGridTask(context, items.get(position).getArtString(), holder).execute();
                        }

                        @Override
                        public void onError() {

                        }
                    });
        } catch (Exception e) {
            Picasso.with(context).load(R.drawable.default_artwork_dark)
                    .into(holder.albumArt);
        }
        holder.realBackground.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, AlbumActivity.class);
                intent.putExtra("albumName", items.get(position).getName().toString());
                intent.putExtra("albumId", items.get(position).getId());
                String transitionName = "albumArt";
                ActivityOptionsCompat options =
                        ActivityOptionsCompat.makeSceneTransitionAnimation((Activity) context,
                                new Pair<View, String>(holder.albumArt, transitionName)
                        );
                ActivityCompat.startActivity((Activity) context, intent, options.toBundle());
            }
        });
    }


    @Override
    public int getItemCount() {
        return this.items.size();
    }
}

