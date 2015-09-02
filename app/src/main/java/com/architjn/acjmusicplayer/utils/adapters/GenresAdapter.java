package com.architjn.acjmusicplayer.utils.adapters;



import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.architjn.acjmusicplayer.R;
import com.architjn.acjmusicplayer.utils.items.GenresListItem;

import java.util.List;

public class GenresAdapter extends RecyclerView.Adapter<GenresAdapter.SimpleItemViewHolder> {

    private final List<GenresListItem> items;
    private Context context;

    public final static class SimpleItemViewHolder extends RecyclerView.ViewHolder {
        public TextView genresName;

        public SimpleItemViewHolder(View itemView) {
            super(itemView);
//            counter = (TextView) itemView.findViewById(R.id.album_song_item_count);
            genresName = (TextView) itemView.findViewById(R.id.genres_name);
        }
    }

    public GenresAdapter(Context context, List<GenresListItem> items) {
        this.context = context;
        this.items = items;
    }

    @Override
    public GenresAdapter.SimpleItemViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).
                inflate(R.layout.genres_list_item, parent, false);
        return new SimpleItemViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(SimpleItemViewHolder holder, int position) {
        holder.genresName.setText(items.get(position).getName());
    }

    @Override
    public int getItemCount() {
        return this.items.size();
    }
}
