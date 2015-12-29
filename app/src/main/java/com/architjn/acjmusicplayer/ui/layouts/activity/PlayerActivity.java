package com.architjn.acjmusicplayer.ui.layouts.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatSeekBar;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.architjn.acjmusicplayer.R;
import com.architjn.acjmusicplayer.service.PlayerService;
import com.architjn.acjmusicplayer.task.PlayerLoader;
import com.architjn.acjmusicplayer.ui.widget.PointShiftingArrayList;
import com.architjn.acjmusicplayer.utils.ListSongs;
import com.architjn.acjmusicplayer.utils.handlers.PlayerDBHandler;
import com.architjn.acjmusicplayer.utils.decorations.PlayingListDividerItemDecoration;
import com.architjn.acjmusicplayer.utils.handlers.UserPreferenceHandler;
import com.architjn.acjmusicplayer.utils.adapters.PlayingListAdapter;
import com.architjn.acjmusicplayer.utils.items.Song;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by architjn on 12/12/15.
 */
public class PlayerActivity extends AppCompatActivity {

    public static final String ACTION_RECIEVE_SONG = "action_recieve_song";
    private static final String TAG = "PlayerActivity-TAG";
    public AppCompatSeekBar seekBar;
    private ImageView artHolder;
    private RecyclerView rv;
    private Song currentSong;
    private PlayerState playerState;
    private PlayingListAdapter adapter;
    private TextView currentSeekText;
    private TextView totalSeekText;
    private Timer timer;
    private int lightColor;
    private int darkColor;
    private UserPreferenceHandler preferenceHandler;
    private ImageView pause, repeat, shuffle;
    private final BroadcastReceiver br = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            switch (intent.getAction()) {
                case ACTION_RECIEVE_SONG:
                    if (intent.getLongExtra("songId", 0) != -1) {
                        updateView(context, intent);
                        play();
                    } else {
                        Toast.makeText(PlayerActivity.this, R.string.nothing_to_play, Toast.LENGTH_SHORT).show();
                        pause();
                    }
                    break;
            }
        }
    };
    private View seekHolder;
    private View controlHolder;
    private CollapsingToolbarLayout collapsingToolbar;

    private static Drawable convertDrawableToGrayScale(Drawable drawable) {
        if (drawable == null)
            return null;

        Drawable res = drawable.mutate();
        res.setColorFilter(Color.parseColor("#9cffffff"), PorterDuff.Mode.SRC_IN);
        return res;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        getWindow().setFlags(
                WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS,
                WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        super.onCreate(savedInstanceState);

        setContentView(R.layout.large_player);
        playerState = PlayerState.PAUSED;
        init();
        setRecyclerView();
    }

    private void init() {
        setSupportActionBar((Toolbar) findViewById(R.id.toolbar_player));
        if (getSupportActionBar() != null)
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("");
        collapsingToolbar = (CollapsingToolbarLayout)
                findViewById(R.id.collapsingtoolbarlayout_player);
        askUpdate();
        IntentFilter filter = new IntentFilter();
        filter.addAction(ACTION_RECIEVE_SONG);
        registerReceiver(br, filter);
        preferenceHandler = new UserPreferenceHandler(this);
    }

    private void askUpdate() {
        Intent i = new Intent();
        i.setAction(PlayerService.ACTION_GET_SONG);
        sendBroadcast(i);
    }

    private void updateRepeat() {
        if (preferenceHandler.isRepeatAllEnabled()) {
            repeat.setImageDrawable(ContextCompat.getDrawable(this,
                    R.drawable.ic_repeat_white_48dp));
        } else if (preferenceHandler.isRepeatOneEnabled()) {
            repeat.setImageDrawable(ContextCompat.getDrawable(this,
                    R.drawable.ic_repeat_one_white_48dp));
        } else {
            repeat.setImageDrawable(convertDrawableToGrayScale(ContextCompat
                    .getDrawable(this, R.drawable.ic_repeat_white_48dp)));
        }
    }

    private void setRecyclerView() {
        rv = (RecyclerView) findViewById(R.id.rv_player);
        LinearLayoutManager manager = new LinearLayoutManager(this);
        rv.setLayoutManager(manager);
        final View header = setHeader();
        final PlayerDBHandler dbHandler = new PlayerDBHandler(this);
        final PointShiftingArrayList<Song> songPointShiftingArrayList = new PointShiftingArrayList<>();
        ArrayList<Song> normalList = dbHandler.getAllPlaybackSongs();
        songPointShiftingArrayList.copy(normalList);
        songPointShiftingArrayList.setPointOnShifted(dbHandler.getFetchedPlayingPos());
        adapter = new PlayingListAdapter(PlayerActivity.this,
                header, songPointShiftingArrayList);
        rv.addItemDecoration(new PlayingListDividerItemDecoration(PlayerActivity.this, 75, manager));
        rv.setAdapter(adapter);
    }

    private void updateView(Context context, Intent intent) {
        currentSong = new PlayerDBHandler(context)
                .getSongFromId(intent.getLongExtra("songId", 0));
        String path = ListSongs.getAlbumArt(context,
                currentSong.getAlbumId());
        controlHolder = findViewById(R.id.controller_holder);
        seekHolder = findViewById(R.id.control_seek_bar_holder);
        artHolder = (ImageView) findViewById(R.id.activity_player_art);
        new PlayerLoader(context, artHolder, path,
                seekHolder, controlHolder, collapsingToolbar,
                lightColor,darkColor) {

            @Override
            public void onColorFetched(int lightColor, int darkColor) {
                PlayerActivity.this.lightColor = lightColor;
                PlayerActivity.this.darkColor = darkColor;
                adapter.setCurrentColor(lightColor, darkColor);
            }
        };
        totalSeekText.setText(currentSong.getDuration());
        seekBar.setMax((int) currentSong.getDurationLong());
        seekBar.setProgress(intent.getIntExtra("seek", 0));
        if (adapter != null)
            adapter.setPointOnShifted(intent.getIntExtra("pos", 0));
        currentSeekText.setText(currentSong.getFormatedTime(
                intent.getIntExtra("seek", 0)));
        updateSeekBar();
    }

    private void updateSeekBar() {
        if (timer == null) {
            timer = new Timer();
            TimerTask task = new TimerTask() {
                @Override
                public void run() {
                    runOnUiThread(new Runnable() {
                        public void run() {
                            if (playerState == PlayerState.PLAYING) {
                                seekBar.setProgress(seekBar.getProgress() + 100);
                                currentSeekText.setText(currentSong.getFormatedTime(seekBar.getProgress()));
                            }
                        }
                    });
                }
            };
            timer.schedule(task, 0, 100);
        }
    }

    private View setHeader() {
        View header = LayoutInflater.from(this).inflate(
                R.layout.player_controller, rv, false);
        ImageView next = (ImageView) header.findViewById(R.id.controller_next);
        final ImageView prev = (ImageView) header.findViewById(R.id.controller_prev);
        pause = (ImageView) header.findViewById(R.id.controller_play);
        repeat = (ImageView) header.findViewById(R.id.controller_repeat);
        shuffle = (ImageView) header.findViewById(R.id.controller_shuffle);
        updateRepeat();
        updateShuffle();
        seekBar = (AppCompatSeekBar) header.findViewById(R.id.control_seek_bar);
        currentSeekText = (TextView) header.findViewById(R.id.controls_current_pos);
        totalSeekText = (TextView) header.findViewById(R.id.controls_total_pos);
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                Intent i = new Intent(PlayerService.ACTION_SEEK_SONG);
                i.putExtra("seek", seekBar.getProgress());
                sendBroadcast(i);
            }
        });
        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendBroadcast(new Intent(PlayerService.ACTION_NEXT_SONG));
            }
        });
        prev.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendBroadcast(new Intent(PlayerService.ACTION_PREV_SONG));
            }
        });
        pause.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendBroadcast(new Intent(PlayerService.ACTION_PAUSE_SONG));
                if (playerState == PlayerState.PLAYING)
                    pause();
                else
                    play();
            }
        });
        repeat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                preferenceHandler.setRepeatEnable();
                updateRepeat();
            }
        });
        shuffle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                preferenceHandler.setShuffle();
                updateShuffle();
            }
        });
        return header;
    }

    private void play() {
        playerState = PlayerState.PLAYING;
        pause.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.ic_pause_white_48dp));
    }

    private void pause() {
        playerState = PlayerState.PAUSED;
        pause.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.ic_play_arrow_white_48dp));
    }

    private void updateShuffle() {
        if (preferenceHandler.isShuffleEnabled()) {
            shuffle.setImageDrawable(ContextCompat
                    .getDrawable(this, R.drawable.ic_shuffle_white_48dp));
        } else
            shuffle.setImageDrawable(convertDrawableToGrayScale(ContextCompat
                    .getDrawable(this, R.drawable.ic_shuffle_white_48dp)));
    }

    @Override
    protected void onResume() {
        super.onResume();
        askUpdate();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(br);
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

    private enum PlayerState {
        PLAYING, PAUSED
    }
}
