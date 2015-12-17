package com.architjn.acjmusicplayer.ui.layouts.activity;

import android.Manifest;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import com.architjn.acjmusicplayer.R;
import com.architjn.acjmusicplayer.service.PlayerService;
import com.architjn.acjmusicplayer.utils.PermissionChecker;

/**
 * Created by architjn on 27/11/15.
 */
public class SplashActivity extends AppCompatActivity {

    private static final String TAG = "SplashActivity-log";
    private PermissionChecker permissionChecker;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        permissionChecker = new PermissionChecker(this, this, findViewById(R.id.base_view));
        permissionChecker.check(Manifest.permission.WRITE_EXTERNAL_STORAGE,
                getResources().getString(R.string.storage_permission),
                new PermissionChecker.OnPermissionResponse() {
                    @Override
                    public void onAccepted() {
                        startSplashScreen();
                    }

                    @Override
                    public void onDecline() {
                        finish();
                    }
                });
    }

    private void startSplashScreen() {
        startService(new Intent(this, PlayerService.class));
        //creating thread that will sleep for 10 seconds
        Thread t = new Thread() {
            public void run() {

                try {
                    //sleep thread for 2 seconds, time in milliseconds
                    sleep(2000);

                    //start new activity
                    Intent i = new Intent(SplashActivity.this, MainActivity.class);
                    if (!isFinishing())
                        startActivity(i);

                    //destroying Splash activity
                    finish();

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };

        //start thread
        t.start();
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        permissionChecker.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

}
