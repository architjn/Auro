package com.architjn.acjmusicplayer.ui.layouts.activity.settings;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.widget.LinearLayout;

import com.architjn.acjmusicplayer.R;

/**
 * Created by architjn on 08/06/15.
 */
public class AlbumScreenSettings extends PreferenceActivity {

    private SharedPreferences shp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.pref_albumscreen);
        shp = getSharedPreferences("amp_pref", Context.MODE_PRIVATE);

        //Adding toolbar
        LinearLayout root = (LinearLayout) findViewById(android.R.id.list).getParent().getParent().getParent();
        Toolbar bar = (Toolbar) LayoutInflater.from(this).inflate(R.layout.main_action_bar, root, false);
        bar.setTitle(getResources().getString(R.string.pref_album_view));
        bar.setBackgroundColor(shp.getInt("amp_actionbar", getResources().getColor(R.color.ColorPrimary)));
        root.addView(bar, 0);

    }

}
