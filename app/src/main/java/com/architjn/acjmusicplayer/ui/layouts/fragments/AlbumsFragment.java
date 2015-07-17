package com.architjn.acjmusicplayer.ui.layouts.fragments;

import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.architjn.acjmusicplayer.R;
import com.architjn.acjmusicplayer.elements.SpacesItemDecoration;
import com.architjn.acjmusicplayer.elements.adapters.AlbumsAdapter;
import com.architjn.acjmusicplayer.elements.items.AlbumListItem;

import java.util.ArrayList;

public class AlbumsFragment extends Fragment {

    View mainView;
    RecyclerView gv;
    private SharedPreferences settingsPref;
    private FloatingActionButton fab;

    public AlbumsFragment(FloatingActionButton fab) {
        this.fab = fab;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.albums_fragment, container, false);
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
        gv = (RecyclerView) mainView.findViewById(R.id.album_grid);
    }

    private void getAlbumList() {
        final ArrayList<AlbumListItem> albumList = new ArrayList<>();
        System.gc();
        final String orderBy = MediaStore.Audio.Albums.ALBUM;
        Cursor musicCursor = mainView.getContext().getContentResolver().
                query(MediaStore.Audio.Albums.EXTERNAL_CONTENT_URI, null, null, null, orderBy);

        if (musicCursor != null && musicCursor.moveToFirst()) {
            //get columns
            int titleColumn = musicCursor.getColumnIndex
                    (android.provider.MediaStore.Audio.Albums.ALBUM);
            int idColumn = musicCursor.getColumnIndex
                    (android.provider.MediaStore.Audio.Albums._ID);
            int artistColumn = musicCursor.getColumnIndex
                    (android.provider.MediaStore.Audio.Albums.ARTIST);
            int numOfSongsColumn = musicCursor.getColumnIndex
                    (android.provider.MediaStore.Audio.Albums.NUMBER_OF_SONGS);
            int albumArtColumn = musicCursor.getColumnIndex
                    (android.provider.MediaStore.Audio.Albums.ALBUM_ART);
            //add albums to list
            do {
                albumList.add(new AlbumListItem(musicCursor.getLong(idColumn),
                        musicCursor.getString(titleColumn),
                        musicCursor.getString(artistColumn),
                        false, musicCursor.getString(albumArtColumn),
                        musicCursor.getInt(numOfSongsColumn)));
            }
            while (musicCursor.moveToNext());
        }
        GridLayoutManager gridLayoutManager = new GridLayoutManager(mainView.getContext(), 2);
        gridLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        gridLayoutManager.scrollToPosition(0);
        gv.setLayoutManager(gridLayoutManager);
        gv.addItemDecoration(new SpacesItemDecoration(8));
        gv.setHasFixedSize(true);
        gv.setAdapter(new AlbumsAdapter(mainView.getContext(), albumList, fab));

    }
}