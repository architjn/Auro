package com.architjn.acjmusicplayer.ui.layouts.fragments;

import android.animation.Animator;
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
import android.graphics.drawable.Drawable;
import android.media.AudioManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.support.v4.view.animation.FastOutSlowInInterpolator;
import android.support.v7.graphics.Palette;
import android.support.v7.widget.AppCompatSeekBar;
import android.support.v7.widget.PopupMenu;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import com.afollestad.async.Action;
import com.architjn.acjmusicplayer.R;
import com.architjn.acjmusicplayer.service.PlayerService;
import com.architjn.acjmusicplayer.task.ColorChangeAnimation;
import com.architjn.acjmusicplayer.ui.layouts.activity.AlbumActivity;
import com.architjn.acjmusicplayer.ui.layouts.activity.ArtistActivity;
import com.architjn.acjmusicplayer.ui.layouts.activity.MainActivity;
import com.architjn.acjmusicplayer.ui.widget.slidinguppanel.SlidingUpPanelLayout;
import com.architjn.acjmusicplayer.utils.ListSongs;
import com.architjn.acjmusicplayer.utils.Utils;
import com.architjn.acjmusicplayer.utils.handlers.PlayerDBHandler;
import com.architjn.acjmusicplayer.utils.handlers.UserPreferenceHandler;
import com.architjn.acjmusicplayer.utils.items.Song;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import java.io.File;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by architjn on 01/01/16.
 */
public class PlayerFragment extends Fragment {

    private static final String TAG = "PlayerFragment-TAG";
    private static final int MAX_ALPHA = 255, TRANS_ALPHA = 140;
    private static int MAX_VOL;

    public static final String ACTION_OPEN_PANEL = "ACTION_OPEN_PANEL";
    public static final String ACTION_RECIEVE_SONG = "ACTION_RECIEVE_SONG";
    public static View miniController;

    private TextView miniSongTitle, largeSongTitle, navTitle, navSubTitle;
    private Context context;
    private View mainView, revealView;
    private ImageView artHolder, navArt;
    private SlidingUpPanelLayout slidingUpPanelLayout;
    private Utils utils;
    private PlayerState playerState;
    private int size, colorLight, colorTo = 0xffffffff, colorDark;
    private Timer timer;
    private AppCompatSeekBar volumeSeekBar, seekBar;
    private ImageView next, pause, prev, shuffle, repeat, noVolume,
            fullVolume, playlist, waveIcon, backButton, overflowMenu;
    private LinearLayout controllHolder, currentSongHolder;
    private Song currentSong;
    private TextView totalSeekText, currentSeekText;
    private UserPreferenceHandler preferenceHandler;
    private AudioManager audioManager;
    private UpNextFragment upNextFragment;

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
        updateNavigationHeader(null);
    }

    private void updateNavigationHeader(Intent intent) {
        if (navArt.getDrawable() == null || intent == null) {
            navArt.setImageBitmap(utils.getBitmapOfVector(R.drawable.default_art,
                    size, size));
            navTitle.setText(null);
            navSubTitle.setText(null);
        } else {
            navTitle.setText(intent.getStringExtra("songName"));
            navSubTitle.setText(intent.getStringExtra("albumName"));
        }
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
        revealView = mainView.findViewById(R.id.player_reveal_view);
        updateRepeat();
        updateShuffle();
        setVolumeControls();
        setListeners();
        askUpdate();
        setControllerSize();
        changeButtonColor();
        setActionButtonClicks(backButton, overflowMenu);
        handleStatusBarColor();
    }

    private void setVolumeControls() {
        audioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
        MAX_VOL = audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
        int curVolume = audioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
        volumeSeekBar.setMax(MAX_VOL);
        volumeSeekBar.setProgress(curVolume);
    }

    private void setListeners() {
        overflowMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PopupMenu menu = new PopupMenu(context, view);
                menu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        switch (item.getItemId()) {
                            case R.id.overflow_player_open_album:
                                Intent i = new Intent(context, AlbumActivity.class);
                                i.putExtra("albumName", currentSong.getAlbumName());
                                i.putExtra("albumId", currentSong.getAlbumId());
                                i.putExtra("albumColor", colorLight);
                                startActivity(i);
                                return true;
                            case R.id.overflow_player_open_artist:
                                Intent a = new Intent(context, ArtistActivity.class);
                                a.putExtra("name", currentSong.getArtist());
                                a.putExtra("id", ListSongs.getArtistIdFromName(context,
                                        currentSong.getArtist()));
                                context.startActivity(a);
                                return true;
                        }
                        return false;
                    }
                });
                menu.inflate(R.menu.overflow_player);
                menu.show();
            }
        });
        artHolder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!slidingUpPanelLayout.isPanelExpanded())
                    slidingUpPanelLayout.expandPanel();
            }
        });
        noVolume.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, 0, 0);
                volumeSeekBar.setProgress(0);
            }
        });
        fullVolume.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, MAX_VOL, 0);
                volumeSeekBar.setProgress(MAX_VOL);
            }
        });
        playlist.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int[] loc = new int[2];
                revealView.setBackgroundColor(colorLight);
                playlist.getLocationOnScreen(loc);
                DrawableCompat.setTint(backButton.getDrawable(), 0xff444d5d);
                DrawableCompat.setTint(overflowMenu.getDrawable(), 0xff444d5d);
                Animator anim = ViewAnimationUtils
                        .createCircularReveal(revealView, loc[0], loc[1], 0,
                                (new Utils(context).getWindowWidth()) * 2);
                anim.setDuration(800);
                anim.setInterpolator(new FastOutSlowInInterpolator());
                revealView.setVisibility(View.VISIBLE);
                anim.addListener(new Animator.AnimatorListener() {
                    @Override
                    public void onAnimationStart(Animator animator) {

                    }

                    @Override
                    public void onAnimationEnd(Animator animator) {
                        upNextFragment.setColorLight(colorLight);
                        upNextFragment.setSlidingUpPanelLayout(slidingUpPanelLayout);
                        getActivity().getSupportFragmentManager()
                                .beginTransaction().replace(R.id.panel_holder, upNextFragment)
                                .commit();
                    }

                    @Override
                    public void onAnimationCancel(Animator animator) {

                    }

                    @Override
                    public void onAnimationRepeat(Animator animator) {

                    }
                });
                anim.start();
            }
        });
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

    public void setMiniPlayerAlpha(int alpha) {
        miniController.setAlpha(alpha);
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

    private void setStatusBarColor(int color) {
        ((MainActivity) getActivity()).setStatusBarColor(color);
    }

    private void handleStatusBarColor() {
        slidingUpPanelLayout.setPanelSlideListener(
                new SlidingUpPanelLayout.PanelSlideListener() {
                    @Override
                    public void onPanelSlide(View panel, float slideOffset) {
                        if (slideOffset == 1 && colorLight != 0 && isVisible())
                            setStatusBarColor(colorDark);
                        else if (isVisible())
                            setStatusBarColor(ContextCompat.getColor(context, R.color.colorPrimaryDark));
                        View nowPlayingCard = PlayerFragment.miniController;
                        nowPlayingCard.setAlpha(1 - slideOffset);
                    }

                    @Override
                    public void onPanelCollapsed(View panel) {
                        View nowPlayingCard = PlayerFragment.miniController;
                        nowPlayingCard.setAlpha(1);
                        if (revealView.getVisibility() == View.VISIBLE) {
                            revealView.setVisibility(View.INVISIBLE);
                            DrawableCompat.setTint(backButton.getDrawable(), colorTo);
                            DrawableCompat.setTint(overflowMenu.getDrawable(), colorTo);
                        }
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

    private void changeButtonColor() {
        DrawableCompat.setTint(backButton.getDrawable(), colorTo);
        DrawableCompat.setTint(overflowMenu.getDrawable(), colorTo);
        DrawableCompat.setTint(volumeSeekBar.getThumb(), colorTo);
        DrawableCompat.setTint(volumeSeekBar.getProgressDrawable(), colorTo);
        DrawableCompat.setTint(pause.getDrawable(), colorTo);
        DrawableCompat.setTint(prev.getDrawable(), colorTo);
        DrawableCompat.setTint(next.getDrawable(), colorTo);
        DrawableCompat.setTint(shuffle.getDrawable(), colorTo);
        DrawableCompat.setTint(repeat.getDrawable(), colorTo);
        DrawableCompat.setTint(noVolume.getDrawable(), colorTo);
        DrawableCompat.setTint(fullVolume.getDrawable(), colorTo);
        DrawableCompat.setTint(playlist.getDrawable(), colorTo);
        DrawableCompat.setTint(waveIcon.getDrawable(), colorTo);
        DrawableCompat.setTint(volumeSeekBar.getProgressDrawable(), colorTo);
    }

    private void updatePlayer(Intent intent) {
        String name = intent.getStringExtra("songName");
        currentSong = new PlayerDBHandler(context)
                .getSongFromId(intent.getLongExtra("songId", 0));
        String path = ListSongs.getAlbumArt(context,
                currentSong.getAlbumId());
        updateMiniPlayer(name, path);
        updateMainPlayer(intent, path);
        updateNavigationHeader(intent);
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
                if (path != null && (new File(path)).exists()) {
                    Picasso.with(context).load(new File(path)).resize(size, size)
                            .centerCrop().into(new Target() {
                        @Override
                        public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                            artHolder.setImageBitmap(bitmap);
                            navArt.setImageBitmap(bitmap);
                        }

                        @Override
                        public void onBitmapFailed(Drawable errorDrawable) {

                        }

                        @Override
                        public void onPrepareLoad(Drawable placeHolderDrawable) {

                        }
                    });
                    return;
                }
                artHolder.setImageBitmap(img);
                navArt.setImageBitmap(img);
            }

            @Nullable
            @Override
            protected Object run() throws InterruptedException {
                if (path != null && (new File(path)).exists()) {
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
                        .getColor(context, R.color.colorPrimary));
                colorDark = getDarkColor(colorLight);
                colorTo = 0xffffffff;
                if (palette.getVibrantSwatch() != null)
                    colorTo = palette.getVibrantSwatch().getBodyTextColor();
                if (slidingUpPanelLayout.isPanelExpanded()) {
                    setStatusBarColor(colorDark);
                }
                animateColorChangeView(controllHolder, colorLight);
                animateColorChangeView(currentSongHolder, colorDark);
                changeButtonColor();
                largeSongTitle.setTextColor(colorTo);
                currentSeekText.setTextColor(colorTo);
                totalSeekText.setTextColor(colorTo);
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

    public void onBackPressed() {
        if (isVisible())
            slidingUpPanelLayout.collapsePanel();
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

    public void setUpNextFragment(UpNextFragment upNextFragment) {
        this.upNextFragment = upNextFragment;
    }

    public void setNavigationHeader(View navigationHeader) {
        navArt = (ImageView) navigationHeader.findViewById(R.id.nav_header_img);
        navTitle = (TextView) navigationHeader.findViewById(R.id.nav_header_title);
        navSubTitle = (TextView) navigationHeader.findViewById(R.id.nav_header_album);
    }

    private enum PlayerState {
        PLAYING, PAUSED
    }
}
