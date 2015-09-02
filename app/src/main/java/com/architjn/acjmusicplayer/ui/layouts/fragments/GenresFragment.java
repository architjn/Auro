package com.architjn.acjmusicplayer.ui.layouts.fragments;

import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.architjn.acjmusicplayer.R;
import com.architjn.acjmusicplayer.utils.adapters.GenresAdapter;
import com.architjn.acjmusicplayer.utils.items.GenresListItem;

import java.util.ArrayList;

public class GenresFragment extends Fragment {

    View mainView;
    RecyclerView lv;
    private SharedPreferences settingsPref;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.genres_fragment, container, false);
        this.mainView = v;

        initialize();
        Handler mainHandler = new Handler(mainView.getContext().getMainLooper());

        Runnable myRunnable = new Runnable() {
            @Override
            public void run() {
                getAlbumList();
            }
        };
        mainHandler.post(myRunnable);

        return v;
    }

    private void initialize() {
        settingsPref = PreferenceManager.getDefaultSharedPreferences(mainView.getContext());
        lv = (RecyclerView) mainView.findViewById(R.id.genres_list);
        LinearLayoutManager layoutManager = new LinearLayoutManager(mainView.getContext());
        // Control orientation of the items
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        layoutManager.scrollToPosition(0);
        // Attach layout manager
        lv.setLayoutManager(layoutManager);
        lv.setHasFixedSize(true);
    }

    private void getAlbumList() {
        ArrayList<GenresListItem> genresList = new ArrayList<>();
        System.gc();
        final String orderBy = MediaStore.Audio.Genres.NAME;
        Cursor musicCursor = mainView.getContext().getContentResolver().
                query(MediaStore.Audio.Genres.EXTERNAL_CONTENT_URI, null, null, null, orderBy);

        if (musicCursor != null && musicCursor.moveToFirst()) {
            //get columns
            int titleColumn = musicCursor.getColumnIndex
                    (MediaStore.Audio.Genres.NAME);
            int idColumn = musicCursor.getColumnIndex
                    (android.provider.MediaStore.Audio.Genres._ID);
            do {
                genresList.add(new GenresListItem(musicCursor.getLong(idColumn),
                        musicCursor.getString(titleColumn)));
            }
            while (musicCursor.moveToNext());
        }
        lv.setAdapter(new GenresAdapter(mainView.getContext(), genresList));
    }
}