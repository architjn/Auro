package com.architjn.acjmusicplayer.utils.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.architjn.acjmusicplayer.R;

import java.util.ArrayList;

/**
 * Created by architjn on 28/11/15.
 */
public class SearchListAdapter extends RecyclerView.Adapter<SearchListAdapter.SimpleItemViewHolder> {
    private static final String TAG = "SearchListAdapter-TAG";

    private ArrayList<String> items;
    private Context context;

    public SearchListAdapter(Context context, ArrayList<String> items) {
        this.context = context;
        this.items = items;
    }

    @Override
    public SearchListAdapter.SimpleItemViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).
                inflate(R.layout.search_list_item, parent, false);
        return new SimpleItemViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final SearchListAdapter.SimpleItemViewHolder holder, final int position) {
        holder.name.setText(items.get(position));
        holder.mainView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(context, items.get(position), Toast.LENGTH_SHORT).show();
            }
        });
    }


    @Override
    public int getItemCount() {
        return items.size();
    }

    public class SimpleItemViewHolder extends RecyclerView.ViewHolder {

        public TextView name;
        public View mainView;

        public SimpleItemViewHolder(View itemView) {
            super(itemView);
            mainView = itemView;
            name = (TextView) itemView.findViewById(R.id.search_item_name);
        }
    }


}
