package com.architjn.acjmusicplayer.ui.layouts.activity;

import android.app.ActivityManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.graphics.Palette;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.transition.Fade;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import com.annimon.paperstyle.PaperSeekBar;
import com.architjn.acjmusicplayer.R;
import com.architjn.acjmusicplayer.elements.adapters.PlayingSongAdapter;
import com.architjn.acjmusicplayer.service.MusicService;
import com.architjn.acjmusicplayer.task.ChangeSeekDetailUpdater;
import com.architjn.acjmusicplayer.task.ColorAnimateAlbumView;
import com.architjn.acjmusicplayer.ui.widget.MyLinearLayoutManager;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by architjn on 01/07/15.
 */

public class MusicPlayer extends AppCompatActivity {

    public static final String ACTION_GET_PLAY_STATE = "get_play_state";
    public static final String ACTION_GET_SEEK_VALUE = "gte_seek_value";
    public static final String ACTION_GET_PLAYING_LIST = "get_playing_list";
    public static int mainColor;
    private Toolbar toolbar;
    private String songPath, songName, songDesc, albumName, songArt;
    private TextView songNameView, songArtistView, currentTimeHolder, totalTimeHolder;
    private long albumId;
    private NestedScrollView playerNestedScroll;
    private RecyclerView rv;
    private LinearLayout detailHolder;
    private CollapsingToolbarLayout collapsingToolbarLayout;
    private ImageView playButton, rewindButton,
            nextButton, shuffleButton, header;
    private PaperSeekBar seekBar;
    private int duration, currentDuration;
    private boolean musicStoped;


    private BroadcastReceiver musicPlayer = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals(ACTION_GET_SEEK_VALUE)) {
                seekBar.setProgress(intent.getIntExtra("songSeekVal", 0));
                currentDuration = intent.getIntExtra("songSeekVal", 0);
                if (intent.getBooleanExtra("isPlaying", false)) {
                    playButton.setImageResource(R.drawable.ic_pause_white_48dp);
                    musicStoped = false;
                } else {
                    playButton.setImageResource(R.drawable.ic_play_arrow_white_48dp);
                    musicStoped = true;
                }
            } else if (intent.getAction().equals(ACTION_GET_PLAY_STATE)) {
                if (intent.getBooleanExtra("isPlaying", false)) {
                    playButton.setImageResource(R.drawable.ic_pause_white_48dp);
                    musicStoped = false;
                } else {
                    playButton.setImageResource(R.drawable.ic_play_arrow_white_48dp);
                    musicStoped = true;
                }
            } else if (intent.getAction().equals(ACTION_GET_PLAYING_LIST)) {
                ArrayList<String> name = new ArrayList<>(), desc = new ArrayList<>(), songId = new ArrayList<>();
                name = intent.getStringArrayListExtra("name");
                desc = intent.getStringArrayListExtra("desc");
                songId = intent.getStringArrayListExtra("songId");
                rv = (RecyclerView) findViewById(R.id.player_playlist);
                PlayingSongAdapter adapter = new PlayingSongAdapter(context, name, desc, songId);
                MyLinearLayoutManager layoutManager = new MyLinearLayoutManager(context,
                        LinearLayoutManager.VERTICAL, false);
                layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
                rv.setLayoutManager(layoutManager);
                rv.setHasFixedSize(true);
                rv.setAdapter(adapter);
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_player);

        IntentFilter filter = new IntentFilter();
        filter.addAction(ACTION_GET_SEEK_VALUE);
        filter.addAction(ACTION_GET_PLAY_STATE);
        filter.addAction(ACTION_GET_PLAYING_LIST);
        registerReceiver(musicPlayer, filter);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
            getWindow().setEnterTransition(new Fade());

        songPath = getIntent().getStringExtra("songPath");
        songName = getIntent().getStringExtra("songName");
        songDesc = getIntent().getStringExtra("songDesc");
        albumId = getIntent().getLongExtra("songAlbumId", 0);
        albumName = getIntent().getStringExtra("songAlbumName");
        duration = getIntent().getIntExtra("songDuration", 0);
        currentDuration = getIntent().getIntExtra("songCurrTime", 0);

        init();

        setButtons();

        updateSeeker();

        setSupportActionBar((Toolbar) findViewById(R.id.toolbar_player));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Cursor cursor = getContentResolver().query(MediaStore.Audio.Albums.EXTERNAL_CONTENT_URI,
                new String[]{MediaStore.Audio.Albums._ID, MediaStore.Audio.Albums.ALBUM_ART},
                MediaStore.Audio.Albums._ID + "=?",
                new String[]{String.valueOf(albumId)},
                null);
        if (cursor.moveToFirst()) {
            songArt = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Albums.ALBUM_ART));
        }

        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = false;
        options.inPreferredConfig = Bitmap.Config.RGB_565;
        options.inDither = true;
        Drawable artWork = new BitmapDrawable(BitmapFactory.decodeFile(songArt, options));
        ((ImageView) findViewById(R.id.header)).setImageDrawable(artWork);
        songNameView.setText(songName);
        songArtistView.setText(songDesc);


        if (songArt != null) {
            Palette.generateAsync(BitmapFactory.decodeFile(songArt, options),
                    new Palette.PaletteAsyncListener() {
                        @Override
                        public void onGenerated(final Palette palette) {
                            try {
                                mainColor = palette.getDarkVibrantSwatch().getRgb();
                                if (Build.VERSION.SDK_INT >= 21) {
                                    ActivityManager.TaskDescription taskDescription = new
                                            ActivityManager.TaskDescription(getResources().getString(R.string.app_name),
                                            BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher),
                                            palette.getDarkVibrantSwatch().getRgb());
                                    setTaskDescription(taskDescription);
                                }
                                new ColorAnimateAlbumView(MusicPlayer.this, detailHolder, palette).execute();
                            } catch (NullPointerException e) {
                                e.printStackTrace();
                                detailHolder.setBackgroundColor(mainColor);
                            } finally {
                                collapsingToolbarLayout.setContentScrimColor(mainColor);
                            }
                        }
                    }
            );
        } else {
            header.setImageResource(R.drawable.default_artwork_dark);
        }
    }

    private void updateSeeker() {
        seekBar.setMax(duration);
        seekBar.setProgress(currentDuration);
        totalTimeHolder.setText(String.format("%02d", ((duration / 1000) / 60)) +
                ":" + String.format("%02d", ((duration / 1000) % 60)));
        musicStoped = false;
        new Timer().schedule(new TimerTask() {
            @Override
            public void run() {
                runOnUiThread(new Runnable() {
                    public void run() {
                        if (!musicStoped) {
                            int seekProg = seekBar.getProgress();
                            if (seekProg < duration)
                                seekBar.setProgress(seekProg + 100);
                            else
                                seekBar.setProgress(100);
//                        currentTimeHolder.setText((seekProg / 1000) / 60 + ":" +
//                                (seekProg / 1000) % 60);
                            new ChangeSeekDetailUpdater(seekProg, currentTimeHolder).execute();
//                        mHandler.postDelayed(this, duration / 100);
                        }
                    }
                });
            }
        }, 0, 100);
    }

    private void setButtons() {

        playButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent stopMusic = new Intent();
                stopMusic.setAction(MusicService.ACTION_STOP);
                sendBroadcast(stopMusic);
            }
        });

        nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent nextMusic = new Intent();
                nextMusic.setAction(MusicService.ACTION_NEXT);
                sendBroadcast(nextMusic);
            }
        });

        rewindButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent prevMusic = new Intent();
                prevMusic.setAction(MusicService.ACTION_PREV);
                sendBroadcast(prevMusic);
            }
        });

        shuffleButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent shuffleMusic = new Intent();
                shuffleMusic.setAction(MusicService.ACTION_SHUFFLE_PLAYLIST);
                sendBroadcast(shuffleMusic);
            }
        });

        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (fromUser) {
                    Intent changeCurrentTime = new Intent();
                    changeCurrentTime.setAction(MusicService.ACTION_SEEK_TO);
                    changeCurrentTime.putExtra("changeSeek", progress);
                    sendBroadcast(changeCurrentTime);
                    musicStoped = false;
                } else {
                    if (seekBar.getProgress() == duration) {
                        seekBar.setProgress(100);
                        musicStoped = true;
                    }
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

    }

    private void init() {
        toolbar = (Toolbar) findViewById(R.id.toolbar_player);
        playerNestedScroll = (NestedScrollView) findViewById(R.id.player_nested_scroll);
        header = (ImageView) findViewById(R.id.header);
        playButton = (ImageView) findViewById(R.id.player_play);
        rewindButton = (ImageView) findViewById(R.id.player_rewind);
        nextButton = (ImageView) findViewById(R.id.player_forward);
        shuffleButton = (ImageView) findViewById(R.id.player_shuffle);
        songNameView = (TextView) findViewById(R.id.player_song_name);
        songArtistView = (TextView) findViewById(R.id.player_song_artist);
        seekBar = (PaperSeekBar) findViewById(R.id.player_seekbar);
        currentTimeHolder = (TextView) findViewById(R.id.player_current_time);
        totalTimeHolder = (TextView) findViewById(R.id.player_total_time);
        detailHolder = (LinearLayout) findViewById(R.id.detail_holder);
        collapsingToolbarLayout = (CollapsingToolbarLayout) findViewById(R.id.collapsingtoolbarlayout_player);
    }

    @Override
    protected void onPause() {
        super.onPause();
        try {
            unregisterReceiver(musicPlayer);
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        }
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

    @Override
    public void onResume() {
        super.onResume();
        Intent i = new Intent();
        i.setAction(MusicService.ACTION_SEEK_GET);
        sendBroadcast(i);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

}
