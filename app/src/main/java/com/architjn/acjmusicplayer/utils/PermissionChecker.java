package com.architjn.acjmusicplayer.utils;

import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.View;

import com.architjn.acjmusicplayer.R;

/**
 * Created by architjn on 08/12/15.
 */
public class PermissionChecker {

    public static final int REQUEST_CODE = 7;
    private static final String TAG = "PermissionChecker";
    private OnPermissionResponse response;


    public interface OnPermissionResponse {
        void onAccepted();

        void onDecline();
    }

    private Context context;
    private Activity activity;
    private View baseView;

    public PermissionChecker(Context context, Activity activity, View baseView) {
        this.context = context;
        this.activity = activity;
        this.baseView = baseView;
    }

    public void check(final String permission, final String customMsg, final OnPermissionResponse response) {
        this.response = response;
        new Thread(new Runnable() {
            @Override
            public void run() {
                if (ContextCompat.checkSelfPermission(context, permission) ==
                        PackageManager.PERMISSION_GRANTED) {
                    activity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            response.onAccepted();
                        }
                    });
                } else {
                    Log.v(TAG, "Waiting");
                    if (ActivityCompat.shouldShowRequestPermissionRationale((Activity) context,
                            permission)) {
                        Snackbar.make(baseView, customMsg,
                                Snackbar.LENGTH_INDEFINITE)
                                .setAction(R.string.ok, new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        ActivityCompat.requestPermissions((Activity) context,
                                                new String[]{permission},
                                                REQUEST_CODE);
                                    }
                                })
                                .show();
                    } else {
                        ActivityCompat.requestPermissions(((Activity) context),
                                new String[]{permission},
                                REQUEST_CODE);
                    }
                }
            }
        }).start();
    }

    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, final
    @NonNull int[] grantResults) {
        if (requestCode == PermissionChecker.REQUEST_CODE) {

            activity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (grantResults.length == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                        // Storage permission has been granted
                        response.onAccepted();
                        Log.v(TAG, "After Waiting Accepted");
                    } else {
                        //Storage permission has been denied
                        response.onDecline();
                    }
                }
            });
        }
    }

}
