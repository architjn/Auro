package com.architjn.acjmusicplayer.ui.layouts.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import com.architjn.acjmusicplayer.R;
import com.architjn.acjmusicplayer.utils.MySQLiteHelper;
import com.architjn.acjmusicplayer.utils.adapters.PlaylistActivityAdapter;

/**
 * Created by architjn on 02/09/15.
 */
public class PlaylistActivity extends AppCompatActivity {
    RecyclerView recyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_playlist);
        setSupportActionBar((Toolbar) findViewById(R.id.playlist_toolbar));
        setTitle(getIntent().getStringExtra("title"));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        recyclerView = (RecyclerView) findViewById(R.id.rv_playlist_activity);
        MySQLiteHelper helper = new MySQLiteHelper(this);
        int playlistId = getIntent().getIntExtra("playlistId", -1);
        if (playlistId == -1) {
            finish();
        }
        recyclerView.setAdapter(new PlaylistActivityAdapter(
                this, helper.getPlayListSongs(playlistId),
                playlistId));
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setItemAnimator(new DefaultItemAnimator());
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                super.onBackPressed();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

}
