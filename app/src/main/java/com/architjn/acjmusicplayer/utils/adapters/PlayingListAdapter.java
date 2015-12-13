package com.architjn.acjmusicplayer.utils.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.architjn.acjmusicplayer.R;
import com.architjn.acjmusicplayer.utils.items.Song;

import java.util.ArrayList;

/**
 * Created by architjn on 28/11/15.
 */
public class PlayingListAdapter extends RecyclerView.Adapter<PlayingListAdapter.SimpleItemViewHolder> {

    private static final int ITEM_VIEW_TYPE_HEADER = 0;
    private static final int ITEM_VIEW_TYPE_ITEM = 1;

    private ArrayList<Song> items;
    private Context context;
    private View header;

    public PlayingListAdapter(Context context, View header, ArrayList<Song> items) {
        this.context = context;
        this.header = header;
        this.items = items;
    }

    public boolean isHeader(int position) {
        return position == 0;
    }

    @Override
    public PlayingListAdapter.SimpleItemViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == ITEM_VIEW_TYPE_HEADER) {
            return new SimpleItemViewHolder(header);
        }
        View itemView = LayoutInflater.from(parent.getContext()).
                inflate(R.layout.songs_list_item, parent, false);
        return new SimpleItemViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final PlayingListAdapter.SimpleItemViewHolder holder, final int position) {
        if (isHeader(position)) {
            return;
        }
        holder.name.setText(items.get(position - 1).getName());
        holder.artistName.setText(items.get(position - 1).getArtist());
    }

    @Override
    public void onViewRecycled(SimpleItemViewHolder holder) {
        super.onViewRecycled(holder);
        if (!isHeader(holder.getPosition()))
            holder.img.setImageDrawable(null);
    }

    @Override
    public int getItemCount() {
        return items.size() + 1;
    }

    public class SimpleItemViewHolder extends RecyclerView.ViewHolder {

        public TextView name, artistName;
        public View mainView, menu;
        public ImageView img;

        public SimpleItemViewHolder(View itemView) {
            super(itemView);
            mainView = itemView;
            img = (ImageView) itemView.findViewById(R.id.song_item_img);
            name = (TextView) itemView.findViewById(R.id.song_item_name);
            menu = itemView.findViewById(R.id.song_item_menu);
            artistName = (TextView) itemView.findViewById(R.id.song_item_artist);
        }
    }

    @Override
    public int getItemViewType(int position) {
        return isHeader(position) ? ITEM_VIEW_TYPE_HEADER : ITEM_VIEW_TYPE_ITEM;
    }
}
