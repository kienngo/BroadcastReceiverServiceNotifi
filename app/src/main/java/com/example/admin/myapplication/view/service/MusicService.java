package com.example.admin.myapplication.view.service;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;

import com.example.admin.myapplication.R;
import com.example.admin.myapplication.model.Music;
import com.example.admin.myapplication.model.data.MusicManagerAction;
import com.example.admin.myapplication.view.activity.detail.OnUpdateUserInterface;
import com.example.admin.myapplication.view.activity.music.MusicActivity;
import com.example.admin.myapplication.view.activity.music.NotificationAction;

import java.util.List;

public class MusicService extends Service implements OnUpdateUserInterface {
    private static final int REQUEST_CODE = 0;
    private static final int FOREGROUND_SERVICE = 101;
    private static final String PREVIOUS = "Prev";
    private static final String PLAY = "Play";
    private static final String NEXT = "Next";
    private static final String CLOSE = "Close";

    private IBinder mIBinder = new MusicBinder();
    private MusicManagerAction mMusicManagerAction;
    private NotificationCompat.Builder mBuilder;

    // interface between detail activity and service
    private OnUpdateUserInterface mOnUpdateUserInterface;

    public void onUpdateUserInterface(OnUpdateUserInterface onUpdateUserInterface) {
        this.mOnUpdateUserInterface = onUpdateUserInterface;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        mMusicManagerAction = new MusicManagerAction(this);
        mMusicManagerAction.onUpdateUserInterface(this);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        handleIntent(intent);
        return START_NOT_STICKY;
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        handleIntent(intent);
        return mIBinder;
    }

    private void handleIntent(Intent intent) {
        String action = intent != null ? intent.getAction() : null;
        if (action == null) {
            return;
        }
        switch (action) {
            case NotificationAction.START_FOREGROUND_ACTION:
                startForeground();
                break;
            case NotificationAction.PLAY_ACTION:
                selectPlayAndPause();
                break;
            case NotificationAction.NEXT_ACTION:
                nextSong();
                break;
            case NotificationAction.PREV_ACTION:
                previousSong();
                break;
            case NotificationAction.CLOSE_ACTION:

                break;
            case NotificationAction.STOP_FOREGROUND_ACTION:
                stopForeground(true);
                stopSelf();
            default:
                break;
        }
    }

    private void startForeground() {
        startForeground(FOREGROUND_SERVICE,
                getNotification(mMusicManagerAction.getCurrentSong().getmNameSong(),
                        mMusicManagerAction.getCurrentSong().getmAuthorSong(), false));
    }

    //---------------------------------------------------------------------------------------------//
    // all funtion interface from service and activity
    public void setAllCurrentSongs(List<Music> lstMusics) {
        mMusicManagerAction.setAllCurrentSongs(lstMusics);
    }

    public void setIndexCurrentSong(int pos) {
        mMusicManagerAction.setIndexCurrentSong(pos);
    }

    public void playSong(int pos) {
        mMusicManagerAction.create(pos);
        mMusicManagerAction.start();
    }

    public void seek(int progress) {
        mMusicManagerAction.seek(progress);
    }

    public int getDuration() {
        return mMusicManagerAction.getDuration();
    }

    public int getPosition() {
        return mMusicManagerAction.getPosition();
    }

    public void nextSong() {
        mMusicManagerAction.nextSong();
    }

    public void previousSong() {
        mMusicManagerAction.previousSong();
    }

    public boolean isOnlyPlaying() {
        return mMusicManagerAction.isOnlyPlaying();
    }

    public void selectPlayAndPause() {
        mOnUpdateUserInterface.onUpdateUserInterface(mMusicManagerAction.selectPlayAndPause());
        updateNotification(mMusicManagerAction.getCurrentSong().getmNameSong(), mMusicManagerAction.getCurrentSong().getmAuthorSong(), true);
    }

    private int getIdIcon(boolean isPlayOrPause) {
        if (isPlayOrPause) {
            if (isOnlyPlaying()) {
                return android.R.drawable.ic_media_pause;
            } else {
                return android.R.drawable.ic_media_play;
            }
        }
        return android.R.drawable.ic_media_pause;
    }

    //---------------------------------------------------------------------------------------------//
    public Notification getNotification(String title, String artist, boolean isPlayOrPause) {
        Intent ncIntent = new Intent(this, MusicActivity.class);
        ncIntent.setAction(NotificationAction.MAIN_ACTION);
        ncIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        PendingIntent pIntent = PendingIntent.getActivity(this, REQUEST_CODE,
                ncIntent, REQUEST_CODE);

        Intent prevIntent = new Intent(this, MusicService.class);
        prevIntent.setAction(NotificationAction.PREV_ACTION);
        PendingIntent pPrevIntent = PendingIntent.getService(this, REQUEST_CODE,
                prevIntent, REQUEST_CODE);

        Intent playIntent = new Intent(this, MusicService.class);
        playIntent.setAction(NotificationAction.PLAY_ACTION);
        PendingIntent pPlayIntent = PendingIntent.getService(this, REQUEST_CODE,
                playIntent, REQUEST_CODE);

        Intent nextIntent = new Intent(this, MusicService.class);
        nextIntent.setAction(NotificationAction.NEXT_ACTION);
        PendingIntent pNextIntent = PendingIntent.getService(this, REQUEST_CODE,
                nextIntent, REQUEST_CODE);

        Intent closeIntent = new Intent(this, MusicService.class);
        closeIntent.setAction(NotificationAction.CLOSE_ACTION);
        PendingIntent pCloseIntent = PendingIntent.getService(this, REQUEST_CODE,
                closeIntent, REQUEST_CODE);

        mBuilder = new NotificationCompat.Builder(getApplicationContext(), "")
                .setContentTitle(title)
                .setContentText(artist)
                .setSmallIcon(R.drawable.ic_music)
                .setContentIntent(pIntent)
                .setAutoCancel(true)
                .setOngoing(false)
                .setPriority(Notification.PRIORITY_MAX)
                .addAction(android.R.drawable.ic_media_previous, PREVIOUS, pPrevIntent)
                .addAction(getIdIcon(isPlayOrPause), PLAY, pPlayIntent)
                .addAction(android.R.drawable.ic_media_next, NEXT, pNextIntent)
                .addAction(android.R.drawable.ic_menu_close_clear_cancel, CLOSE, pCloseIntent)
                .setStyle(new android.support.v4.media.app.NotificationCompat.MediaStyle()
                        .setShowActionsInCompactView(NotificationStyle.STYLE_0, NotificationStyle.STYLE_1, NotificationStyle.STYLE_2, NotificationStyle.STYLE_3));
        return mBuilder.build();
    }

    // update user interface to notification when play or pause
    private void updateNotification(String title, String artist, boolean isClickPlayOrPause) {
        Notification notification = getNotification(title, artist, isClickPlayOrPause);
        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(FOREGROUND_SERVICE, notification);
    }

    @Override
    public void onUpdateUserInterface(int state) {
    }

    @Override
    public void onUpdateUserInterface(String title, String album) {
        updateNotification(title, album, true);
    }

    public class MusicBinder extends Binder {
        public MusicService getService() {
            return MusicService.this;
        }
    }
}
