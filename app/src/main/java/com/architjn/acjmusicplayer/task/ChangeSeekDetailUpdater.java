package com.architjn.acjmusicplayer.task;

import android.os.AsyncTask;
import android.widget.TextView;

/**
 * Created by architjn on 10/07/15.
 */
public class ChangeSeekDetailUpdater extends AsyncTask<Void, Void, Void> {

    private int mSeekBarProgress;
    private TextView mTextView;
    private String min, sec;

    public ChangeSeekDetailUpdater(int seekBar, TextView textView) {
        this.mSeekBarProgress = seekBar;
        this.mTextView = textView;
    }

    @Override
    protected Void doInBackground(Void... params) {
        min = String.format("%02d", ((mSeekBarProgress / 1000) / 60));
        sec = String.format("%02d", ((mSeekBarProgress / 1000) % 60));
        return null;
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        super.onPostExecute(aVoid);
        mTextView.setText(min + ":" + sec);
    }
}
