package com.architjn.acjmusicplayer.ui.layouts.activity;

import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import com.architjn.acjmusicplayer.R;
import com.architjn.acjmusicplayer.utils.handlers.PlaylistDBHelper;
import com.architjn.acjmusicplayer.utils.decorations.SimpleDividerItemDecoration;
import com.architjn.acjmusicplayer.utils.adapters.PlaylistSongListAdapter;

/**
 * Created by architjn on 30/11/15.
 */
public class PlaylistActivity extends AppCompatActivity {

    private RecyclerView rv;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_playlist);
        initView();
    }

    private void initView() {
        rv = (RecyclerView) findViewById(R.id.songsListContainer);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        addSongList();
    }

    private void addSongList() {
        rv.setLayoutManager(new LinearLayoutManager(this));
        PlaylistDBHelper dbHelper = new PlaylistDBHelper(this);
        rv.addItemDecoration(new SimpleDividerItemDecoration(this, 75));
        rv.setAdapter(new PlaylistSongListAdapter(this,
                dbHelper.getAllPlaylistSongs(getIntent().getIntExtra("id", 0)),
                getIntent().getIntExtra("id", 0)));
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                super.onBackPressed();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    public int getAutoStatColor(int baseColor) {
        float[] hsv = new float[3];
        Color.colorToHSV(baseColor, hsv);
        hsv[2] *= 1.4f;
        return Color.HSVToColor(hsv);
    }

}
