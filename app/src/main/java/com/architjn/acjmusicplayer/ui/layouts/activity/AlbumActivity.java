package com.architjn.acjmusicplayer.ui.layouts.activity;

import android.app.ActivityManager;
import android.database.Cursor;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.architjn.acjmusicplayer.R;
import com.architjn.acjmusicplayer.utils.ListSongs;
import com.architjn.acjmusicplayer.utils.PermissionChecker;
import com.architjn.acjmusicplayer.utils.Utils;
import com.architjn.acjmusicplayer.utils.adapters.AlbumSongListAdapter;
import com.squareup.picasso.Picasso;

import java.io.File;

/**
 * Created by architjn on 30/11/15.
 */
public class AlbumActivity extends AppCompatActivity {

    private static final String TAG = "AlbumActivity-TAG";
    private RecyclerView rv;
    private ImageView albumArt;
    private PermissionChecker permissionChecker;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        getWindow().setFlags(
                WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS,
                WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        getWindow().setStatusBarColor(ContextCompat.getColor(this, android.R.color.transparent));
        overridePendingTransition(0, 0);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_album);
        initView();
    }

    private void initView() {
        permissionChecker = new PermissionChecker(this, this, findViewById(R.id.base_view_album));
        rv = (RecyclerView) findViewById(R.id.rv_album_activity);
        albumArt = (ImageView) findViewById(R.id.activity_album_art);
        int size = new Utils(this).getWindowWidth();
        int panelSize = (int) getResources().getDimension(R.dimen.album_title_height);
        int height = new Utils(this).getWindowHeight() - panelSize * 2;
        setAlbumArtSize(size, height);
        setAlbumArt(size, new Utils(this).getWindowHeight());
        CollapsingToolbarLayout collapsingToolbarLayout = (CollapsingToolbarLayout)
                findViewById(R.id.collapsingtoolbarlayout_album);
        if (collapsingToolbarLayout != null)
            collapsingToolbarLayout.setTitle(getIntent().getStringExtra("albumName"));
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_album);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null)
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        addSongList();
        setForAnimation();
    }

    private void setAlbumArtSize(int width, int height) {
        LinearLayout.LayoutParams lp = new LinearLayout
                .LayoutParams(width, height);
        albumArt.setLayoutParams(lp);
    }

    private void setForAnimation() {
        rv.scrollTo(0, 100);
    }

    private void addSongList() {
        rv.setLayoutManager(new LinearLayoutManager(this));
        rv.setAdapter(new AlbumSongListAdapter(this, ListSongs.getAlbumSongList(this,
                getIntent().getLongExtra("albumId", 0)), this, permissionChecker));
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        permissionChecker.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    public void setAlbumArt(int width, int height) {
        Cursor cursor = getContentResolver().query(MediaStore.Audio.Albums.EXTERNAL_CONTENT_URI,
                new String[]{MediaStore.Audio.Albums._ID, MediaStore.Audio.Albums.ALBUM_ART},
                MediaStore.Audio.Albums._ID + "=?",
                new String[]{String.valueOf(getIntent().getLongExtra("albumId", 0))},
                null);
        if (cursor != null && cursor.moveToFirst()) {
            String imagePath = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Albums.ALBUM_ART));
            try {
                if (imagePath == null) {
                    Utils utils = new Utils(this);
                    albumArt.setImageBitmap(utils.getBitmapOfVector(R.drawable.default_art,
                            width, width));
                    return;
                }
                Picasso.with(AlbumActivity.this)
                        .load(new File(imagePath))
                        .into(albumArt);
            } catch (NullPointerException e) {
                e.printStackTrace();
            }
        }

        int colorPrimary = getIntent().getIntExtra("albumColor", ContextCompat.getColor(this, R.color.colorPrimary));
        findViewById(R.id.album_song_name_holder).setBackgroundColor(colorPrimary);
        ((CollapsingToolbarLayout) findViewById(R.id.collapsingtoolbarlayout_album))
                .setContentScrimColor(colorPrimary);
        ((CollapsingToolbarLayout) findViewById(R.id.collapsingtoolbarlayout_album))
                .setStatusBarScrimColor(getAutoStatColor(colorPrimary));
        if (Build.VERSION.SDK_INT >= 21) {
            ActivityManager.TaskDescription taskDescription = new
                    ActivityManager.TaskDescription(getIntent().getStringExtra("albumName"),
                    BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher), colorPrimary);
            setTaskDescription(taskDescription);
        }
        cursor.close();
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

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    public int getAutoStatColor(int baseColor) {
        float[] hsv = new float[3];
        Color.colorToHSV(baseColor, hsv);
        hsv[2] *= 1.4f;
        return Color.HSVToColor(hsv);
    }

}
