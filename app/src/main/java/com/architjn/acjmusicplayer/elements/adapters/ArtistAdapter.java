package com.architjn.acjmusicplayer.elements.adapters;

//public class ArtistAdapter extends BaseAdapter {
//    static// Declare Variables
//            Context mContext;
//    LayoutInflater inflater;
//    private List<ArtistListItem> ArtistListItem = null;
//    private ArrayList<ArtistListItem> arraylist;
//    View mView;
//
//    public ArtistAdapter(Context context,
//                         List<ArtistListItem> ArtistListItem) {
//        mContext = context;
//        this.ArtistListItem = ArtistListItem;
//        inflater = LayoutInflater.from(mContext);
//        this.arraylist = new ArrayList<ArtistListItem>();
//        this.arraylist.addAll(ArtistListItem);
//    }
//
//    public class ViewHolder {
//        TextView artistName;
//        TextView artistDesc;
//    }
//
//    @Override
//    public int getCount() {
//        return ArtistListItem.size();
//    }
//
//    @Override
//    public ArtistListItem getItem(int position) {
//        return ArtistListItem.get(position);
//    }
//
//    @Override
//    public long getItemId(int position) {
//        return position;
//    }
//
//    public View getView(final int position, View view, ViewGroup parent) {
//        final ViewHolder holder;
//        if (view == null) {
//            holder = new ViewHolder();
//            view = inflater.inflate(R.layout.grid_item, null);
//            this.mView = view;
//            holder.artistName = (TextView) view.findViewById(R.id.grid_name);
//            holder.artistDesc = (TextView) view.findViewById(R.id.grid_desc);
//            view.setTag(holder);
//        } else {
//            holder = (ViewHolder) view.getTag();
//        }
//
//        holder.artistName.setText(ArtistListItem.get(position).getName());
//        holder.artistDesc.setText("Albums- " + ArtistListItem.get(position).getNumOfAlbums()
//                + ", Tracks- " + ArtistListItem.get(position).getNumOfTracks());
//
//        return view;
//    }
//
//}


import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.architjn.acjmusicplayer.R;
import com.architjn.acjmusicplayer.elements.items.ArtistListItem;

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
