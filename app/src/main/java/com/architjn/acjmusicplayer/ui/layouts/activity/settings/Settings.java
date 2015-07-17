package com.architjn.acjmusicplayer.ui.layouts.activity.settings;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.architjn.acjmusicplayer.R;

import java.io.File;

/**
 * Created by architjn on 08/06/15.
 */
public class Settings extends PreferenceActivity {

    private Context context;
    private SharedPreferences shp;
    private Preference theme, scan;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.settings);
        shp = getSharedPreferences("amp_pref", Context.MODE_PRIVATE);

        //Adding toolbar
        LinearLayout root = (LinearLayout) findViewById(android.R.id.list).getParent().getParent().getParent();
        Toolbar bar = (Toolbar) LayoutInflater.from(this).inflate(R.layout.main_action_bar, root, false);
        bar.setTitle(getResources().getString(R.string.settings));
        bar.setBackgroundColor(shp.getInt("amp_actionbar", getResources().getColor(R.color.ColorPrimary)));
        root.addView(bar, 0);

        this.context = this;

        setIds();

        setListeners();
    }

    private void setIds() {
        theme = findPreference("pref_theme");
        scan = findPreference("pref_scan");
    }

    private void setListeners() {
        theme.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                startActivity(new Intent(context, ThemeSettings.class));
                return true;
            }
        });
        scan.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
//                sendBroadcast(new Intent(Intent.ACTION_MEDIA_MOUNTED, Uri
//                        .parse("file://"
//                                + Environment.getExternalStorageDirectory())));
                File file = new File("file://"
                        + Environment.getExternalStorageDirectory());
                MediaScannerConnection.scanFile(Settings.this, new String[]{file.getAbsolutePath()},
                        null, new MediaScannerConnection.OnScanCompletedListener() {
                            public void onScanCompleted(String path, Uri uri) {

                                runOnUiThread(new Runnable() {
                                    public void run() {
                                        Toast.makeText(Settings.this, "Scan complete", Toast.LENGTH_SHORT).show();
                                    }
                                });
                            }
                        });
                return true;
            }
        });
    }
}
