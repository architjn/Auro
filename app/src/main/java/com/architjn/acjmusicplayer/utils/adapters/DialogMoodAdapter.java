package com.architjn.acjmusicplayer.utils.adapters;

import android.content.Context;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.architjn.acjmusicplayer.R;
import com.architjn.acjmusicplayer.utils.MySQLiteHelper;
import com.architjn.acjmusicplayer.utils.items.Mood;
import com.architjn.acjmusicplayer.utils.items.SongListItem;

import java.util.List;

public class DialogMoodAdapter extends RecyclerView.Adapter<DialogMoodAdapter.SimpleItemViewHolder> {

    private final List<Mood> items;
    private Context context;
    private SongListItem songToAdd;
    private AlertDialog dialog;

    public final static class SimpleItemViewHolder extends RecyclerView.ViewHolder {
        public TextView title;
        public View view;
        public ImageView img;

        public SimpleItemViewHolder(View itemView) {
            super(itemView);
            view = itemView;
            title = (TextView) itemView.findViewById(R.id.mood_name);
            img = (ImageView) itemView.findViewById(R.id.mood_img);
        }
    }

    public DialogMoodAdapter(Context context, List<Mood> items, SongListItem songListItem, AlertDialog dialog) {
        this.context = context;
        this.items = items;
        this.songToAdd = songListItem;
        this.dialog = dialog;
    }

    @Override
    public DialogMoodAdapter.SimpleItemViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).
                inflate(R.layout.mood_item, parent, false);
        return new SimpleItemViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(SimpleItemViewHolder holder, final int position) {
        holder.title.setText(items.get(position).getName());
        if (items.get(position).getImgRes() != 0)
            holder.img.setImageResource(items.get(position).getImgRes());
        holder.view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MySQLiteHelper helper = new MySQLiteHelper(context);
                if (songToAdd.getId() != -1)
                    helper.updateMood(songToAdd.getId(), items.get(position).getMood());
                else
                    helper.updateMood(songToAdd.getName(), items.get(position).getMood());
                dialog.dismiss();
            }
        });
    }

    @Override
    public int getItemCount() {
        return this.items.size();
    }
}
