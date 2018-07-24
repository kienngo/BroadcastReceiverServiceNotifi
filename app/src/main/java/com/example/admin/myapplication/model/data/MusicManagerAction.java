package com.example.admin.myapplication.model.data;

import android.content.Context;
import android.media.MediaPlayer;
import android.net.Uri;

import com.example.admin.myapplication.model.Music;
import com.example.admin.myapplication.view.activity.detail.OnUpdateUserInterface;

import java.util.List;

public class MusicManagerAction implements MediaPlayer.OnCompletionListener {
    private int mState = MediaPlayerState.PLAYING;

    private MediaPlayer mMediaPlayer;
    private Context mContext;
    private List<Music> mMusics;
    private int mIndex;
    private OnUpdateUserInterface mOnUpdateUserInterface;

    public MusicManagerAction(Context context) {
        this.mContext = context;
    }

    public void onUpdateUserInterface(OnUpdateUserInterface onUpdateUserInterface) {
        this.mOnUpdateUserInterface = onUpdateUserInterface;
    }

    public void setIndexCurrentSong(int pos) {
        mIndex = pos;
    }

    public void setAllCurrentSongs(List<Music> lstMusics) {
        mMusics = lstMusics;
    }

    public Music getCurrentSong() {
        return mMusics.get(mIndex);
    }

    public void create(int position) {
        mIndex = position;
        String data = mMusics.get(position).getmUriSong();
        Uri uri = Uri.parse(data);
        if (mMediaPlayer != null) {
            mMediaPlayer.reset();
            mMediaPlayer.release();
        }
        mMediaPlayer = MediaPlayer.create(mContext, uri);
        mMediaPlayer.setOnCompletionListener(this);
    }

    public void start() {
        if (mMediaPlayer != null) {
            mMediaPlayer.start();
        }
    }

    public boolean isOnlyPlaying() {
        return mState == MediaPlayerState.PLAYING;
    }

    public void pause() {
        if (mMediaPlayer != null) {
            mMediaPlayer.pause();
        }
    }

    public void stop() {
        if (mMediaPlayer != null) {
            mMediaPlayer.stop();
        }
    }

    public int selectPlayAndPause() {
        if (isOnlyPlaying()) {
            mState = MediaPlayerState.PAUSED;
            pause();
        } else {
            mState = MediaPlayerState.PLAYING;
            start();
        }

        return mState;
    }

    public void seek(int newPosition) {
        if (mMediaPlayer != null) {
            mMediaPlayer.seekTo(newPosition);
        }
    }

    public int getDuration() {
        if (mMediaPlayer != null) {
            return mMediaPlayer.getDuration();
        } else {
            return MediaPlayerState.RETURN_0;
        }
    }

    public int getPosition() {
        if (mMediaPlayer != null) {
            return mMediaPlayer.getCurrentPosition();
        } else {
            return MediaPlayerState.RETURN_0;
        }
    }

    public void loop(boolean isLoop) {
        if (mMediaPlayer != null) {
            mMediaPlayer.setLooping(isLoop);
        }
    }

    public void nextSong() {
        mIndex++;
        if (mIndex < mMusics.size()) {
            create(mIndex);
            start();
        } else {
            mIndex = mMusics.size() - MediaPlayerState.INCREATER;
            create(mIndex);
            start();
        }
        mOnUpdateUserInterface.onUpdateUserInterface(getCurrentSong().getmNameSong(), getCurrentSong().getmAuthorSong());
    }

    public void previousSong() {
        mIndex--;
        if (mIndex >= MediaPlayerState.RETURN_0) {
            create(mIndex);
            start();
        } else {
            mIndex = MediaPlayerState.RETURN_0;
            create(mIndex);
            start();
        }
        mOnUpdateUserInterface.onUpdateUserInterface(getCurrentSong().getmNameSong(), getCurrentSong().getmAuthorSong());
    }

    @Override
    public void onCompletion(MediaPlayer mp) {
        changeIndex(MediaPlayerState.INCREATER);
    }

    public void changeIndex(int value) {
        mIndex += value;
        if (mIndex < MediaPlayerState.RETURN_0) {
            mIndex = mMusics.size() - MediaPlayerState.INCREATER;
        } else if (mIndex > mMusics.size() - MediaPlayerState.INCREATER) {
            mIndex = MediaPlayerState.RETURN_0;
        }
        create(mIndex);
        start();
        mOnUpdateUserInterface.onUpdateUserInterface(getCurrentSong().getmNameSong(), getCurrentSong().getmAuthorSong());
    }
}
