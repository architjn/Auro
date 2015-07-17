package com.architjn.acjmusicplayer.ui.layouts.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.architjn.acjmusicplayer.service.MusicService;

/**
 * Created by architjn on 06/07/15.
 */
public class DummyActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //nothing here, just a dummy activity
        super.onCreate(savedInstanceState);
        Intent requestSongDetials = new Intent();
        requestSongDetials.setAction(MusicService.ACTION_REQUEST_SONG_DETAILS);
        sendBroadcast(requestSongDetials);
    }
}
