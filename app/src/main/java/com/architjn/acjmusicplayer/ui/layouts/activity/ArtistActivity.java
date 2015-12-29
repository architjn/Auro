package com.architjn.acjmusicplayer.ui.layouts.activity;

import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;

import com.architjn.acjmusicplayer.R;
import com.architjn.acjmusicplayer.utils.decorations.ArtistAlbumListSpacesItemDecoration;
import com.architjn.acjmusicplayer.utils.decorations.ArtistDividerItemDecoration;
import com.architjn.acjmusicplayer.utils.ListSongs;
import com.architjn.acjmusicplayer.utils.adapters.ArtistSongListAdapter;
import com.architjn.acjmusicplayer.utils.adapters.ArtistSubListAdapter;

/**
 * Created by architjn on 30/11/15.
 */
public class ArtistActivity extends AppCompatActivity {

    private static final String TAG = "ArtistActivity-TAG";
    private RecyclerView rv;
    private View header;

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
        if (getSupportActionBar() != null)
            getSupportActionBar().setTitle(getIntent().getStringExtra("name"));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        addSongList();
    }

    private void addSongList() {
        rv.setLayoutManager(new LinearLayoutManager(this));
        rv.addItemDecoration(new ArtistDividerItemDecoration(this, 75));
        header = LayoutInflater.from(this).
                inflate(R.layout.artist_activity_header, rv, false);
        rv.setAdapter(new ArtistSongListAdapter(this, header,
                ListSongs.getSongsListOfArtist(this, getIntent().getStringExtra("name"))));
        setHeader(header);
    }

    private void setHeader(View header) {
        RecyclerView albumRv = (RecyclerView) header.findViewById(R.id.artist_album_rv);
        if (albumRv.getAdapter() == null) {
            albumRv.setLayoutManager(new LinearLayoutManager(this,
                    LinearLayoutManager.HORIZONTAL, false));
            albumRv.addItemDecoration(
                    new ArtistAlbumListSpacesItemDecoration(this, dpToPx(5)));
        }
        ArtistSubListAdapter albumAdapter = new ArtistSubListAdapter(this,
                getIntent().getLongExtra("id", 0));
        albumRv.setAdapter(albumAdapter);
    }

    public int dpToPx(int dp) {
        DisplayMetrics displayMetrics = getResources().getDisplayMetrics();
        return Math.round(dp * (displayMetrics.xdpi / DisplayMetrics.DENSITY_DEFAULT));
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
