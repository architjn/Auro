package com.architjn.acjmusicplayer.elements.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.architjn.acjmusicplayer.R;
import com.architjn.acjmusicplayer.elements.items.PlaylistItem;

import java.util.ArrayList;
import java.util.List;

public class PlayListAdapter extends BaseAdapter {
    static// Declare Variables
            Context mContext;
    LayoutInflater inflater;
    private List<PlaylistItem> PlayListItem = null;
    private ArrayList<PlaylistItem> arraylist;

    public PlayListAdapter(Context context,
                           List<PlaylistItem> PlayListItem) {
        mContext = context;
        this.PlayListItem = PlayListItem;
        inflater = LayoutInflater.from(mContext);
        this.arraylist = new ArrayList<PlaylistItem>();
        this.arraylist.addAll(PlayListItem);
    }

    public class ViewHolder {
        TextView songName;
        TextView songDesc;
    }

    @Override
    public int getCount() {
        return PlayListItem.size();
    }

    @Override
    public PlaylistItem getItem(int position) {
        return PlayListItem.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    public View getView(final int position, View view, ViewGroup parent) {
        final ViewHolder holder;
        if (view == null) {
            holder = new ViewHolder();
            view = inflater.inflate(R.layout.song_list_item, null);
            holder.songName = (TextView) view.findViewById(R.id.song_item_name);
            holder.songDesc = (TextView) view.findViewById(R.id.song_item_desc);
            view.setTag(holder);
        } else {
            holder = (ViewHolder) view.getTag();
        }

        holder.songName.setText(PlayListItem.get(position).getSongTitle());
        holder.songDesc.setText(PlayListItem.get(position).getSongDesc());

        return view;
    }

}

