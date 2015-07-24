package com.architjn.acjmusicplayer.ui.layouts.activity.settings;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.widget.LinearLayout;

import com.architjn.acjmusicplayer.R;

/**
 * Created by architjn on 08/06/15.
 */
public class ThemeSettings extends PreferenceActivity {

    private SharedPreferences shp;
    private Preference homeView, playerView, albumView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.pref_theme);
        shp = getSharedPreferences("amp_pref", Context.MODE_PRIVATE);

        //Adding toolbar
        LinearLayout root = (LinearLayout) findViewById(android.R.id.list).getParent().getParent().getParent();
        Toolbar bar = (Toolbar) LayoutInflater.from(this).inflate(R.layout.main_action_bar, root, false);
        bar.setTitle(getResources().getString(R.string.pref_theme));
        bar.setBackgroundColor(shp.getInt("amp_actionbar", getResources().getColor(R.color.ColorPrimary)));
        root.addView(bar, 0);

        setIds();

        setListeners();

    }

    private void setIds() {
        homeView = findPreference("pref_home_view");
        playerView = findPreference("pref_player_view");
        albumView = findPreference("pref_album_view");
    }

    private void setListeners() {
        homeView.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                startActivity(new Intent(ThemeSettings.this, HomeScreenSettings.class));
                return true;
            }
        });
        playerView.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                startActivity(new Intent(ThemeSettings.this, PlayerScreenSettings.class));
                return true;
            }
        });
        albumView.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                startActivity(new Intent(ThemeSettings.this, AlbumScreenSettings.class));
                return true;
            }
        });
    }

}
