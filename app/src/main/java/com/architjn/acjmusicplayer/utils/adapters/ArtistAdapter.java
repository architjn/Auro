package com.architjn.acjmusicplayer.utils.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.architjn.acjmusicplayer.R;
import com.architjn.acjmusicplayer.utils.items.ArtistListItem;

import java.util.List;

public class ArtistAdapter extends RecyclerView.Adapter<ArtistAdapter.SimpleItemViewHolder> {

    private final List<ArtistListItem> items;
    private Context context;

    public final static class SimpleItemViewHolder extends RecyclerView.ViewHolder {
        public TextView artistName, artistDesc;

        public SimpleItemViewHolder(View itemView) {
            super(itemView);
//            counter = (TextView) itemView.findViewById(R.id.album_song_item_count);
            artistName = (TextView) itemView.findViewById(R.id.grid_name);
            artistDesc = (TextView) itemView.findViewById(R.id.grid_desc);
        }
    }

    public ArtistAdapter(Context context, List<ArtistListItem> items) {
        this.context = context;
        this.items = items;
    }

    @Override
    public ArtistAdapter.SimpleItemViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).
                inflate(R.layout.grid_item, parent, false);
        return new SimpleItemViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(SimpleItemViewHolder holder, int position) {
        holder.artistName.setText(items.get(position).getName());
        holder.artistDesc.setText("Albums- " + items.get(position).getNumOfAlbums()
                + ", Tracks- " + items.get(position).getNumOfTracks());
    }

    @Override
    public int getItemCount() {
        return this.items.size();
    }
}
