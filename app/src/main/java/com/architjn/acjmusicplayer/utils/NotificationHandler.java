package com.architjn.acjmusicplayer.utils;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.support.v7.graphics.Palette;
import android.widget.RemoteViews;

import com.architjn.acjmusicplayer.R;
import com.architjn.acjmusicplayer.service.PlayerService;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import java.io.File;

/**
 * Created by architjn on 15/12/15.
 */
public class NotificationHandler {

    private static final int NOTIFICATION_ID = 272448;
    private static final String TAG = "NotificationHandler-TAG";
    private Context context;
    private PlayerService service;
    private boolean notificationActive;

    private Notification notificationCompat;
    private NotificationManager notificationManager;

    public NotificationHandler(Context context, PlayerService service) {
        this.context = context;
        this.service = service;
    }

    private Notification.Builder createBuiderNotification(boolean removable) {
        Intent notificationIntent = new Intent();
        notificationIntent.setAction(PlayerService.ACTION_NOTI_CLICK);
        PendingIntent contentIntent = PendingIntent.getBroadcast(context, 0, notificationIntent, 0);
        Intent deleteIntent = new Intent();
        deleteIntent.setAction(PlayerService.ACTION_NOTI_REMOVE);
        PendingIntent deletePendingIntent = PendingIntent.getBroadcast(context, 0, deleteIntent, 0);
        if (removable)
            return new Notification.Builder(context)
                    .setOngoing(false)
                    .setSmallIcon(R.drawable.ic_audiotrack_white_24dp)
                    .setContentIntent(contentIntent)
                    .setDeleteIntent(deletePendingIntent);
        else
            return new Notification.Builder(context)
                    .setOngoing(true)
                    .setSmallIcon(R.drawable.ic_audiotrack_white_24dp)
                    .setContentIntent(contentIntent)
                    .setDeleteIntent(deletePendingIntent);
    }

    public void setNotificationPlayer(boolean removable) {
        notificationCompat = createBuiderNotification(removable).build();
        RemoteViews notiLayoutBig = new RemoteViews(context.getPackageName(),
                R.layout.notification_layout);
        RemoteViews notiCollapsedView = new RemoteViews(context.getPackageName(),
                R.layout.notification_small);
        if (Build.VERSION.SDK_INT >= 16) {
            notificationCompat.bigContentView = notiLayoutBig;
        }
        notificationCompat.contentView = notiCollapsedView;
        notificationCompat.priority = Notification.PRIORITY_MAX;
        notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        if (!removable)
            service.startForeground(NOTIFICATION_ID, notificationCompat);
        notificationManager.notify(NOTIFICATION_ID, notificationCompat);
        notificationActive = true;
    }


    public void changeNotificationDetails(String songName, String artistName, long albumId, boolean playing) {
        if (Build.VERSION.SDK_INT >= 16) {
            notificationCompat.bigContentView.setTextViewText(R.id.noti_name, songName);
            notificationCompat.bigContentView.setTextViewText(R.id.noti_artist, artistName);
            notificationCompat.contentView.setTextViewText(R.id.noti_name, songName);
            notificationCompat.contentView.setTextViewText(R.id.noti_artist, artistName);
            Intent playClick = new Intent();
            playClick.setAction(PlayerService.ACTION_PAUSE_SONG);
            PendingIntent playClickIntent = PendingIntent.getBroadcast(context, 21021, playClick, 0);
            notificationCompat.bigContentView.setOnClickPendingIntent(R.id.noti_play_button, playClickIntent);
            notificationCompat.contentView.setOnClickPendingIntent(R.id.noti_play_button, playClickIntent);
            Intent prevClick = new Intent();
            prevClick.setAction(PlayerService.ACTION_PREV_SONG);
            PendingIntent prevClickIntent = PendingIntent.getBroadcast(context, 21121, prevClick, 0);
            notificationCompat.bigContentView.setOnClickPendingIntent(R.id.noti_prev_button, prevClickIntent);
            notificationCompat.contentView.setOnClickPendingIntent(R.id.noti_prev_button, prevClickIntent);
            Intent nextClick = new Intent();
            nextClick.setAction(PlayerService.ACTION_NEXT_SONG);
            PendingIntent nextClickIntent = PendingIntent.getBroadcast(context, 21221, nextClick, 0);
            notificationCompat.bigContentView.setOnClickPendingIntent(R.id.noti_next_button, nextClickIntent);
            notificationCompat.contentView.setOnClickPendingIntent(R.id.noti_next_button, nextClickIntent);
            String path = ListSongs.getAlbumArt(context, albumId);
            int playStateRes;
            if (playing)
                playStateRes = R.drawable.ic_pause_white_48dp;
            else
                playStateRes = R.drawable.ic_play_arrow_white_48dp;
            notificationCompat.bigContentView
                    .setImageViewResource(R.id.noti_play_button, playStateRes);
            notificationCompat.contentView
                    .setImageViewResource(R.id.noti_play_button, playStateRes);
            if (path != null && !path.matches("")) {
                Picasso.with(context).load(new File(path)).into(new Target() {
                    @Override
                    public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                        notificationCompat.bigContentView.setImageViewBitmap(R.id.noti_album_art, bitmap);
                        notificationCompat.contentView.setImageViewBitmap(R.id.noti_album_art, bitmap);
                        Palette.from(bitmap).generate(new Palette.PaletteAsyncListener() {
                            @Override
                            public void onGenerated(Palette palette) {
                                notificationCompat.color = palette.getDarkVibrantColor(
                                        palette.getDarkMutedColor(
                                                palette.getMutedColor(0xffffffff)));
                                notificationManager.notify(NOTIFICATION_ID, notificationCompat);
                            }
                        });
                    }

                    @Override
                    public void onBitmapFailed(Drawable errorDrawable) {
                        setDefaultImageView();
                    }

                    @Override
                    public void onPrepareLoad(Drawable placeHolderDrawable) {
                    }
                });
            }else {
                setDefaultImageView();
            }
        }
    }

    private void setDefaultImageView() {
        notificationCompat.bigContentView.setImageViewResource(R.id.noti_album_art,
                R.drawable.default_art);
        notificationCompat.contentView.setImageViewResource(R.id.noti_album_art, R.drawable.default_art);
        notificationManager.notify(NOTIFICATION_ID, notificationCompat);
    }

    public void updateNotificationView() {
        notificationManager.notify(NOTIFICATION_ID, notificationCompat);
    }

    public boolean isNotificationActive() {
        return notificationActive;
    }

    public void setNotificationActive(boolean notificationActive) {
        this.notificationActive = notificationActive;
    }

    public Notification getNotificationCompat() {
        return notificationCompat;
    }
}
