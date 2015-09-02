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
import com.architjn.acjmusicplayer.utils.Mood;
import com.architjn.acjmusicplayer.utils.adapters.SongsAdapter;
import com.architjn.acjmusicplayer.utils.items.SongListItem;

import java.util.ArrayList;

public class SongsFragment extends Fragment {

    Cursor musicCursor;
    View mainView;
    RecyclerView rv;
    private SongsAdapter songAdapter;
    private SharedPreferences settingsPref;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.songs_fragment, container, false);
        mainView = v;

        initalize();
        Handler mainHandler = new Handler(mainView.getContext().getMainLooper());

        Runnable myRunnable = new Runnable() {
            @Override
            public void run() {
                setSongList();
            }
        };
        mainHandler.post(myRunnable);

        return v;
    }

    private void initalize() {
        settingsPref = PreferenceManager.getDefaultSharedPreferences(mainView.getContext());
        rv = (RecyclerView) mainView.findViewById(R.id.songs_fragment_list);
        LinearLayoutManager layoutManager = new LinearLayoutManager(mainView.getContext());
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        layoutManager.scrollToPosition(0);
        rv.setLayoutManager(layoutManager);
        rv.setHasFixedSize(true);
    }

    private void setSongList() {
        final ArrayList<SongListItem> songList = new ArrayList<>();
        System.gc();
//        String[] STAR = {"*"};
//        String selection = MediaStore.Audio.Media.IS_MUSIC + " != 0";
        final String where = MediaStore.Audio.Media.IS_MUSIC + "=1";
        final String orderBy = MediaStore.Audio.Media.TITLE;
        musicCursor = mainView.getContext().getContentResolver().query(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                null, where, null, orderBy);
        if (musicCursor != null && musicCursor.moveToFirst()) {
            //get columns
            int titleColumn = musicCursor.getColumnIndex
                    (android.provider.MediaStore.Audio.Media.TITLE);
            int idColumn = musicCursor.getColumnIndex
                    (android.provider.MediaStore.Audio.Media._ID);
            int artistColumn = musicCursor.getColumnIndex
                    (android.provider.MediaStore.Audio.Media.ARTIST);
            int pathColumn = musicCursor.getColumnIndex
                    (MediaStore.Audio.Media.DATA);
            int albumIdColumn = musicCursor.getColumnIndex
                    (MediaStore.Audio.Media.ALBUM_ID);
            int albumColumn = musicCursor.getColumnIndex
                    (MediaStore.Audio.Media.ALBUM);
            int i = 1;
            do {
                songList.add(new SongListItem(musicCursor.getLong(idColumn),
                        musicCursor.getString(titleColumn),
                        musicCursor.getString(artistColumn),
                        musicCursor.getString(pathColumn), false,
                        musicCursor.getLong(albumIdColumn),
                        musicCursor.getString(albumColumn), i, Mood.UNKNOWN));
                i++;
            }
            while (musicCursor.moveToNext());
            songAdapter = new SongsAdapter(mainView.getContext(), songList);
            rv.setAdapter(songAdapter);
        }
    }

}