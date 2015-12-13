package com.architjn.acjmusicplayer.utils;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.widget.EditText;

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

    public int dpToPx(int dp, Context context) {
        DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
        int px = Math.round(dp * (displayMetrics.xdpi / DisplayMetrics.DENSITY_DEFAULT));
        return px;
    }

    public void addToPlaylist(final Activity activity, final long songId) {
        final AddToPlaylistDialogListAdapter adapter = new AddToPlaylistDialogListAdapter(context,
                playlistDbHelper.getAllPlaylist(), songId);
        AlertDialog.Builder alert = new AlertDialog.Builder(context);

        alert.setTitle(R.string.choose_playlist);
        alert.setPositiveButton(R.string.new_playlist, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                newPlaylistDialog(activity, songId);
            }
        });
        alert.setNegativeButton(R.string.close, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                dialog.dismiss();
            }
        });
        RecyclerView rv = (RecyclerView) activity.getLayoutInflater()
                .inflate(R.layout.addtoplaylist, null);
        rv.setLayoutManager(new LinearLayoutManager(context));
        rv.addItemDecoration(new SimpleDividerItemDecoration(context, 0));
        rv.setAdapter(adapter);
        alert.setView(rv);
        AlertDialog dialog = alert.create();
        adapter.setDialog(dialog);
        dialog.show();
    }

    private void newPlaylistDialog(final Activity activity, final long songId) {
        final EditText edittext = new EditText(context);
        android.app.AlertDialog.Builder alert = new android.app.AlertDialog.Builder(context);
        alert.setTitle("New Playlist");

        alert.setView(edittext);

        alert.setPositiveButton("Done", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                //What ever you want to do with the value
                new PlaylistDBHelper(context).createPlaylist(edittext.getText().toString());
                addToPlaylist(activity, songId);
            }
        });

        alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                // what ever you want to do with No option.
            }
        });

        alert.show();
    }

}
