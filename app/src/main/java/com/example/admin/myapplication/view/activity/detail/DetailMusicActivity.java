package com.example.admin.myapplication.view.activity.detail;

import android.app.Service;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.SeekBar;

import com.example.admin.myapplication.R;
import com.example.admin.myapplication.model.data.MediaPlayerState;
import com.example.admin.myapplication.view.service.MusicService;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import de.hdodenhof.circleimageview.CircleImageView;

public class DetailMusicActivity extends AppCompatActivity implements SeekBar.OnSeekBarChangeListener, OnUpdateUserInterface {
    @BindView(R.id.image_pause)
    public ImageView mImagePause;
    @BindView(R.id.image_next)
    public ImageView mImageNext;
    @BindView(R.id.image_pre)
    public ImageView mImagePre;
    @BindView(R.id.seekbar_time)
    public SeekBar mSeekTime;
    @BindView(R.id.image_song)
    public CircleImageView mCircleImageView;

    private static final int TIMER = 100;

    private MusicService mMusicService;
    private ServiceConnection mConnection;
    private boolean mIsConnect;
    private int mProgress;
    private SeekBarAsync mSeekBarAsync;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        ButterKnife.bind(this);

        connectService();
        runImageAnimation();
        mSeekTime.setOnSeekBarChangeListener(this);
        mSeekBarAsync = new SeekBarAsync();
        mSeekBarAsync.execute();
    }

    private void runImageAnimation() {
        Animation animSongImage =
                AnimationUtils.loadAnimation(this, R.anim.anim_image_detai);
        mCircleImageView.startAnimation(animSongImage);
    }

    private void connectService() {
        mConnection = new ServiceConnection() {
            @Override
            public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
                if (iBinder instanceof MusicService.MusicBinder) {
                    mIsConnect = true;
                    mMusicService = ((MusicService.MusicBinder) iBinder).getService();
                    mMusicService.onUpdateUserInterface(DetailMusicActivity.this);
                } else {
                    mIsConnect = false;
                }

            }

            @Override
            public void onServiceDisconnected(ComponentName componentName) {
                mIsConnect = false;
            }
        };

        Intent intent = new Intent(this, MusicService.class);
        bindService(intent, mConnection, Service.BIND_AUTO_CREATE);
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
        mProgress = i;
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {
        mMusicService.seek(mProgress);
        seekBar.setProgress(mProgress);
    }

    @Override
    public void onUpdateUserInterface(int state) {
        if (state == MediaPlayerState.PLAYING) {
            mImagePause.setImageResource(R.drawable.ic_play);
        } else {
            mImagePause.setImageResource(R.drawable.ic_pause);
        }
    }

    @Override
    public void onUpdateUserInterface(String title, String album) {

    }

    private class SeekBarAsync extends AsyncTask<Void, Void, Void> {
        @Override
        protected Void doInBackground(Void... voids) {
            while (true) {
                if (isCancelled()) {
                    break;
                }
                try {
                    Thread.sleep(TIMER);
                    publishProgress();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            return null;
        }

        @Override
        protected void onProgressUpdate(Void... values) {
            mSeekTime.setMax(mMusicService.getDuration());
            mSeekTime.setProgress(mMusicService.getPosition());
        }
    }

    @OnClick({R.id.image_next, R.id.image_pause, R.id.image_pre})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.image_next:
                mMusicService.nextSong();
                break;
            case R.id.image_pre:
                mMusicService.previousSong();
                break;
            case R.id.image_pause:
                mMusicService.selectPlayAndPause();
                break;
        }
    }

    @Override
    protected void onDestroy() {
        unbindService(mConnection);
        mIsConnect = false;
        if (mSeekBarAsync != null) {
            mSeekBarAsync.cancel(true);
        }
        super.onDestroy();
    }
}
