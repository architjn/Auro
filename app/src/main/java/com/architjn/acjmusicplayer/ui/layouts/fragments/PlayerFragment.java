package com.architjn.acjmusicplayer.ui.layouts.fragments;

import android.animation.ArgbEvaluator;
import android.animation.ValueAnimator;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.media.AudioManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.support.v7.graphics.Palette;
import android.support.v7.widget.AppCompatSeekBar;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import com.afollestad.async.Action;
import com.architjn.acjmusicplayer.R;
import com.architjn.acjmusicplayer.service.PlayerService;
import com.architjn.acjmusicplayer.task.ColorChangeAnimation;
import com.architjn.acjmusicplayer.ui.widget.slidinguppanel.SlidingUpPanelLayout;
import com.architjn.acjmusicplayer.utils.ListSongs;
import com.architjn.acjmusicplayer.utils.Utils;
import com.architjn.acjmusicplayer.utils.handlers.PlayerDBHandler;
import com.architjn.acjmusicplayer.utils.handlers.UserPreferenceHandler;
import com.architjn.acjmusicplayer.utils.items.Song;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by architjn on 01/01/16.
 */
public class PlayerFragment extends Fragment {

    private static final String TAG = "PlayerFragment-TAG";
    private static final int MAX_ALPHA = 255, TRANS_ALPHA = 140;
    public static final String ACTION_OPEN_PANEL = "ACTION_OPEN_PANEL";
    public static final String ACTION_RECIEVE_SONG = "ACTION_RECIEVE_SONG";
    public static View miniController;
    private TextView miniSongTitle, largeSongTitle;
    private Context context;
    private View mainView;
    private ImageView artHolder;
    private SlidingUpPanelLayout slidingUpPanelLayout;
    private Utils utils;
    private PlayerState playerState;
    private int size, colorLight, colorTo, colorDark;
    private Timer timer;
    private AppCompatSeekBar volumeSeekBar, seekBar;
    private ImageView next, pause, prev, shuffle, repeat, noVolume,
            fullVolume, playlist, waveIcon, backButton, overflowMenu;
    private LinearLayout controllHolder, currentSongHolder;
    private Song currentSong;
    private TextView totalSeekText, currentSeekText;
    private UserPreferenceHandler preferenceHandler;
    private AudioManager audioManager;

    private final BroadcastReceiver br = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            switch (intent.getAction()) {
                case PlayerFragment.ACTION_RECIEVE_SONG:
                    if (intent.getLongExtra("songId", 0) != -1) {
                        if (slidingUpPanelLayout.isPanelHidden())
                            slidingUpPanelLayout.showPanel();
                        updatePlayer(intent);
                        if (intent.getBooleanExtra("running", false))
                            play();
                        else
                            pause();
                    } else {
                        slidingUpPanelLayout.hidePanel();
                        pause();
                    }
                    break;
                case ACTION_OPEN_PANEL:
                    if (!slidingUpPanelLayout.isPanelExpanded())
                        slidingUpPanelLayout.expandPanel();
                    break;
            }
        }
    };

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_player, container, false);
        context = view.getContext();
        mainView = view;
        init(view);
        return view;
    }

    private void init(View view) {
        playerState = PlayerState.PAUSED;
        miniController = view.findViewById(R.id.small_panel);
        miniSongTitle = (TextView) view.findViewById(R.id.mini_player_song_name);
        artHolder = (ImageView) mainView.findViewById(R.id.player_album_art);
        utils = new Utils(context);
        size = utils.getWindowWidth();
        IntentFilter filter = new IntentFilter();
        filter.addAction(PlayerFragment.ACTION_RECIEVE_SONG);
        filter.addAction(ACTION_OPEN_PANEL);
        getActivity().registerReceiver(br, filter);
        setLargePlayer();
    }

    private void setLargePlayer() {
        preferenceHandler = new UserPreferenceHandler(context);
        volumeSeekBar = (AppCompatSeekBar) mainView.findViewById(R.id.controls_volume_seekbar);
        backButton = (ImageView) mainView.findViewById(R.id.player_back_button);
        overflowMenu = (ImageView) mainView.findViewById(R.id.player_overflow_button);
        currentSongHolder = (LinearLayout) mainView.findViewById(R.id.player_current_song);
        next = (ImageView) mainView.findViewById(R.id.controller_next);
        prev = (ImageView) mainView.findViewById(R.id.controller_prev);
        pause = (ImageView) mainView.findViewById(R.id.controller_play);
        repeat = (ImageView) mainView.findViewById(R.id.controller_repeat);
        shuffle = (ImageView) mainView.findViewById(R.id.controller_shuffle);
        controllHolder = (LinearLayout) mainView.findViewById(R.id.controller_holder);
        noVolume = (ImageView) mainView.findViewById(R.id.controls_volume_icon_empty);
        fullVolume = (ImageView) mainView.findViewById(R.id.controls_volume_icon_full);
        waveIcon = (ImageView) mainView.findViewById(R.id.wave_large_player);
        playlist = (ImageView) mainView.findViewById(R.id.playlist_large_player);
        largeSongTitle = (TextView) mainView.findViewById(R.id.name_large_player);
        seekBar = (AppCompatSeekBar) mainView.findViewById(R.id.control_seek_bar);
        currentSeekText = (TextView) mainView.findViewById(R.id.controls_current_pos);
        totalSeekText = (TextView) mainView.findViewById(R.id.controls_total_pos);
        updateRepeat();
        updateShuffle();
        setVolumeControls();
        setListeners();
        askUpdate();
        setControllerSize();
        changeButtonColor(backButton, overflowMenu);
        setActionButtonClicks(backButton, overflowMenu);
        handleStatusBarColor();
    }

    private void setVolumeControls() {
        audioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
        int maxVolume = audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
        int curVolume = audioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
        Log.v(TAG, maxVolume + " " + curVolume);
        volumeSeekBar.setMax(maxVolume);
        volumeSeekBar.setProgress(curVolume);
    }

    private void setListeners() {
        volumeSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onStopTrackingTouch(SeekBar arg0) {
            }

            @Override
            public void onStartTrackingTouch(SeekBar arg0) {
            }

            @Override
            public void onProgressChanged(SeekBar arg0, int arg1, boolean arg2) {
                audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, arg1, 0);
            }
        });
        controllHolder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //to disable clicking
            }
        });
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
                context.sendBroadcast(i);
            }
        });
        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new Action() {
                    @NonNull
                    @Override
                    public String id() {
                        return TAG;
                    }

                    @Nullable
                    @Override
                    protected Object run() throws InterruptedException {
                        context.sendBroadcast(new Intent(PlayerService.ACTION_NEXT_SONG));
                        return null;
                    }
                }.execute();
            }
        });
        prev.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new Action() {
                    @NonNull
                    @Override
                    public String id() {
                        return TAG;
                    }

                    @Nullable
                    @Override
                    protected Object run() throws InterruptedException {
                        context.sendBroadcast(new Intent(PlayerService.ACTION_PREV_SONG));
                        return null;
                    }
                }.execute();
            }
        });
        pause.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                context.sendBroadcast(new Intent(PlayerService.ACTION_PAUSE_SONG));
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
    }

    private void updateRepeat() {
        if (preferenceHandler.isRepeatAllEnabled()) {
            repeat.getDrawable().setAlpha(MAX_ALPHA);
            repeat.setImageDrawable(ContextCompat.getDrawable(context,
                    R.drawable.ic_repeat_white_48dp));
            DrawableCompat.setTint(repeat.getDrawable(), colorTo);
        } else if (preferenceHandler.isRepeatOneEnabled()) {
            repeat.setImageDrawable(ContextCompat.getDrawable(context,
                    R.drawable.ic_repeat_one_white_48dp));
            repeat.getDrawable().setAlpha(MAX_ALPHA);
            DrawableCompat.setTint(repeat.getDrawable(), colorTo);
        } else {
            repeat.setImageDrawable(ContextCompat
                    .getDrawable(context, R.drawable.ic_repeat_white_48dp));
            repeat.getDrawable().setAlpha(TRANS_ALPHA);
            DrawableCompat.setTint(repeat.getDrawable(), colorTo);
        }
    }

    private void play() {
        playerState = PlayerState.PLAYING;
        pause.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ic_pause_white_48dp));
        DrawableCompat.setTint(pause.getDrawable(), colorTo);
    }

    private void pause() {
        playerState = PlayerState.PAUSED;
        pause.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ic_play_arrow_white_48dp));
        DrawableCompat.setTint(pause.getDrawable(), colorTo);
    }

    private void updateShuffle() {
        if (preferenceHandler.isShuffleEnabled()) {
            shuffle.getDrawable().setAlpha(MAX_ALPHA);
        } else
            shuffle.getDrawable().setAlpha(TRANS_ALPHA);
    }

    public void onResume() {
        super.onResume();
        askUpdate();
    }

    private void askUpdate() {
        Intent i = new Intent();
        i.setAction(PlayerService.ACTION_GET_SONG);
        context.sendBroadcast(i);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        context.unregisterReceiver(br);
    }

    private void handleStatusBarColor() {
        slidingUpPanelLayout.setPanelSlideListener(
                new SlidingUpPanelLayout.PanelSlideListener() {
                    @Override
                    public void onPanelSlide(View panel, float slideOffset) {
                        if (slideOffset == 1 && colorLight != 0)
                            getActivity().getWindow().setStatusBarColor(colorDark);
                        else
                            getActivity().getWindow().setStatusBarColor(ContextCompat
                                    .getColor(context, R.color.colorPrimaryDark));
                        View nowPlayingCard = PlayerFragment.miniController;
                        nowPlayingCard.setAlpha(1 - slideOffset);
                    }

                    @Override
                    public void onPanelCollapsed(View panel) {
                        View nowPlayingCard = PlayerFragment.miniController;
                        nowPlayingCard.setAlpha(1);
                    }

                    @Override
                    public void onPanelExpanded(View panel) {
                        View nowPlayingCard = PlayerFragment.miniController;
                        nowPlayingCard.setAlpha(0);
                        int curVolume = audioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
                        volumeSeekBar.setProgress(curVolume);
                    }

                    @Override
                    public void onPanelAnchored(View panel) {

                    }

                    @Override
                    public void onPanelHidden(View panel) {

                    }
                });
    }

    private void setControllerSize() {
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(size, size);
        artHolder.setLayoutParams(lp);
    }

    private void setActionButtonClicks(ImageView backButton, ImageView overflowMenu) {
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                slidingUpPanelLayout.collapsePanel();
            }
        });
        overflowMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });
    }

    public void setSlidingUpPanelLayout(SlidingUpPanelLayout slidingUpPanelLayout) {
        this.slidingUpPanelLayout = slidingUpPanelLayout;
    }

    private void changeButtonColor(ImageView backButton, ImageView overflowMenu) {
        DrawableCompat.setTint(backButton.getDrawable(), 0xff444d5d);
        DrawableCompat.setTint(overflowMenu.getDrawable(), 0xff444d5d);
        DrawableCompat.setTint(volumeSeekBar.getThumb(), 0xffffffff);
        DrawableCompat.setTint(volumeSeekBar.getProgressDrawable(), 0xffffffff);
    }

    private void updatePlayer(Intent intent) {
        String name = intent.getStringExtra("songName");
        currentSong = new PlayerDBHandler(context)
                .getSongFromId(intent.getLongExtra("songId", 0));
        String path = ListSongs.getAlbumArt(context,
                currentSong.getAlbumId());
        updateMiniPlayer(name, path);
        updateMainPlayer(intent, path);
    }

    private void updateMainPlayer(Intent intent, final String path) {
        new Action() {

            private Bitmap img;

            @NonNull
            @Override
            public String id() {
                return TAG;
            }

            @Override
            protected void done(@Nullable Object result) {
                if (path != null) {
                    Picasso.with(context).load(new File(path)).resize(size, size)
                            .centerCrop().into(artHolder);
                    return;
                }
                artHolder.setImageBitmap(img);
            }

            @Nullable
            @Override
            protected Object run() throws InterruptedException {
                if (path != null) {
                    return null;
                } else {
                    img = utils.getBitmapOfVector(R.drawable.default_art,
                            size, size);
                }

                return null;
            }
        }.execute();
        totalSeekText.setText(currentSong.getDuration());
        seekBar.setMax((int) currentSong.getDurationLong());
        seekBar.setProgress(intent.getIntExtra("seek", 0));
//            if (adapter != null)
//                adapter.setPointOnShifted(intent.getIntExtra("pos", 0));
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
                    ((Activity) context).runOnUiThread(new Runnable() {
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

    private void updateMiniPlayer(String name, String path) {
        miniSongTitle.setText(name);
        largeSongTitle.setText(name);
        new ColorChangeAnimation(PlayerFragment.this.context, (LinearLayout) miniController,
                miniSongTitle, (ImageView) mainView.findViewById(R.id.mini_player_img), path) {
            @Override
            public void onColorFetched(Palette palette, Integer colorPrimary) {
                colorLight = palette.getVibrantColor(ContextCompat
                        .getColor(context, R.color.colorPrimaryDark));
                colorDark = getDarkColor(colorLight);
                colorTo = 0xffffffff;
                if (palette.getVibrantSwatch() != null)
                    colorTo = palette.getVibrantSwatch().getBodyTextColor();
                if (slidingUpPanelLayout.isPanelExpanded()) {
                    getActivity().getWindow().setStatusBarColor(colorDark);
                }
                animateColorChangeView(controllHolder, colorLight);
                animateColorChangeView(currentSongHolder, colorDark);
                DrawableCompat.setTint(pause.getDrawable(), colorTo);
                DrawableCompat.setTint(prev.getDrawable(), colorTo);
                DrawableCompat.setTint(next.getDrawable(), colorTo);
                DrawableCompat.setTint(shuffle.getDrawable(), colorTo);
                DrawableCompat.setTint(repeat.getDrawable(), colorTo);
                DrawableCompat.setTint(noVolume.getDrawable(), colorTo);
                DrawableCompat.setTint(fullVolume.getDrawable(), colorTo);
                DrawableCompat.setTint(playlist.getDrawable(), colorTo);
                DrawableCompat.setTint(waveIcon.getDrawable(), colorTo);
                DrawableCompat.setTint(backButton.getDrawable(), colorTo);
                DrawableCompat.setTint(overflowMenu.getDrawable(), colorTo);
                DrawableCompat.setTint(volumeSeekBar.getProgressDrawable(), colorTo);
                largeSongTitle.setTextColor(colorTo);
                currentSeekText.setTextColor(colorTo);
                totalSeekText.setTextColor(colorTo);
//                controllHolder.setSystemUiVisibility(controllHolder.getSystemUiVisibility()
//                        | View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
            }
        }.execute();
    }

    private void animateColorChangeView(final LinearLayout ll, int color) {
        ValueAnimator colorAnimation = ValueAnimator.ofObject(new ArgbEvaluator(),
                ((ColorDrawable) ll.getBackground()).getColor(), color);
        colorAnimation.setDuration(2000);
        colorAnimation.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {

            @Override
            public void onAnimationUpdate(ValueAnimator animator) {
                ll.setBackgroundColor((Integer) animator.getAnimatedValue());
            }

        });
        colorAnimation.start();
    }

    public int getDarkColor(int baseColor) {
        float[] hsv = new float[3];
        Color.colorToHSV(baseColor, hsv);
        hsv[2] *= 0.78f;
        return Color.HSVToColor(hsv);
    }

    public boolean onKeyEvent(KeyEvent event) {
        boolean handled = false;
        switch (event.getKeyCode()) {
            case KeyEvent.KEYCODE_VOLUME_UP:
                if (slidingUpPanelLayout.isPanelExpanded()) {
                    volumeSeekBar.setProgress(volumeSeekBar.getProgress() + 1);
                    audioManager.setStreamVolume(AudioManager.STREAM_MUSIC,
                            volumeSeekBar.getProgress() + 1, 0);
                    handled = true;
                }
                break;
            case KeyEvent.KEYCODE_VOLUME_DOWN:
                if (slidingUpPanelLayout.isPanelExpanded()) {
                    volumeSeekBar.setProgress(volumeSeekBar.getProgress() - 1);
                    audioManager.setStreamVolume(AudioManager.STREAM_MUSIC,
                            volumeSeekBar.getProgress() - 1, 0);
                    handled = true;
                }
                break;
        }
        return handled;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        try {
            getActivity().unregisterReceiver(br);
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        }
    }

    private enum PlayerState {
        PLAYING, PAUSED
    }
}
