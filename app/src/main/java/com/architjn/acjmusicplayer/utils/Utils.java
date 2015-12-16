package com.architjn.acjmusicplayer.utils;

import android.app.Activity;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.architjn.acjmusicplayer.R;
import com.architjn.acjmusicplayer.utils.adapters.AddToPlaylistDialogListAdapter;

/**
 * Created by architjn on 09/12/15.
 */
public class Utils {

    private final PlaylistDBHelper playlistDbHelper;
    private Context context;

    public Utils(Context context) {
        this.context = context;
        playlistDbHelper = new PlaylistDBHelper(context);
    }

    public int dpToPx(int dp) {
        DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
        int px = Math.round(dp * (displayMetrics.xdpi / DisplayMetrics.DENSITY_DEFAULT));
        return px;
    }

    public void addToPlaylist(final Activity activity, final long songId) {
        final AddToPlaylistDialogListAdapter adapter = new AddToPlaylistDialogListAdapter(context,
                playlistDbHelper.getAllPlaylist(), songId);
        RecyclerView rv = (RecyclerView) activity.getLayoutInflater()
                .inflate(R.layout.addtoplaylist, null);
        rv.setLayoutManager(new LinearLayoutManager(context));
        rv.addItemDecoration(new SimpleDividerItemDecoration(context, 0));
        rv.setAdapter(adapter);
        MaterialDialog dialog = new MaterialDialog.Builder(context)
                .title(R.string.add_to_playlist)
                .customView(rv, false)
                .positiveText(R.string.new_playlist)
                .negativeText(R.string.close)
                .onPositive(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog materialDialog, @NonNull DialogAction dialogAction) {
                        newPlaylistDialog(activity, songId);
                    }
                })
                .onNegative(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog materialDialog, @NonNull DialogAction dialogAction) {
                        materialDialog.dismiss();
                    }
                })
                .show();
        adapter.setDialog(dialog);
    }

    private void newPlaylistDialog(final Activity activity, final long songId) {
        new MaterialDialog.Builder(context)
                .title(R.string.new_playlist)
                .input(null, null, new MaterialDialog.InputCallback() {
                    @Override
                    public void onInput(MaterialDialog dialog, CharSequence input) {
                        if (!input.toString().matches("")) {
                            new PlaylistDBHelper(context).createPlaylist(input.toString());
                            addToPlaylist(activity, songId);
                        }
                    }
                }).show();
    }

}
