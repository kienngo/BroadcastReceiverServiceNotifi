package com.example.admin.myapplication.view.activity.music;

import android.Manifest;
import android.app.Service;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;

import com.example.admin.myapplication.R;
import com.example.admin.myapplication.model.Music;
import com.example.admin.myapplication.presenter.MusicPresenter;
import com.example.admin.myapplication.presenter.listener.InitView;
import com.example.admin.myapplication.presenter.listener.OnClickItemView;
import com.example.admin.myapplication.view.activity.detail.DetailMusicActivity;
import com.example.admin.myapplication.view.adapter.MusicAdapter;
import com.example.admin.myapplication.view.service.MusicService;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MusicActivity extends AppCompatActivity implements MusicViewImp, InitView {
    private static final String TAG = "MainActivity";
    private final String mError = "error";
    private final int PICK_FROM_GALLERY = 1;
    private final int INIT_INDEX = 0;

    @BindView(R.id.recycler_music)
    public RecyclerView mRecyclerMusic;

    private MusicPresenter mMusicPresenter;
    private MusicAdapter mMusicAdapter;
    private List<Music> mLstMusic;

    private ServiceConnection mConnection;
    private MusicService mMusicService;
    private boolean mIsConnect;
    private boolean mIsForegroundRunning;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        initView();
        connectService();
    }

    @Override
    public void fncResultData(List<Music> lstMusic) {
        mLstMusic = new ArrayList<>();
        mLstMusic = lstMusic;
        mMusicAdapter = new MusicAdapter(lstMusic, this, mOnClickItemView);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this,
                LinearLayoutManager.VERTICAL, false);
        mRecyclerMusic.setLayoutManager(layoutManager);
        mRecyclerMusic.setAdapter(mMusicAdapter);
    }

    @Override
    public void initView() {
        try {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)
                    != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE}, PICK_FROM_GALLERY);
            } else {
                mMusicPresenter = new MusicPresenter(this, MusicActivity.this);
                mMusicPresenter.fncRequestGetDataFromAct();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String permissions[], @NonNull int[] grantResults) {
        switch (requestCode) {
            case PICK_FROM_GALLERY:
                if (grantResults.length > INIT_INDEX && grantResults[INIT_INDEX] == PackageManager.PERMISSION_GRANTED) {
                    mMusicPresenter = new MusicPresenter(this, MusicActivity.this);
                    mMusicPresenter.fncRequestGetDataFromAct();
                } else {
                    Log.e(TAG, mError);
                }
                break;
        }
    }

    private OnClickItemView mOnClickItemView = new OnClickItemView() {
        @Override
        public void onClickItem(View view, int pos) {
            playDetailMusic(pos, mLstMusic);
        }
    };

    // create service connection
    private void connectService() {
        mConnection = new ServiceConnection() {
            @Override
            public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
                if (iBinder instanceof MusicService.MusicBinder) {
                    mIsConnect = true;
                    mMusicService = ((MusicService.MusicBinder) iBinder).getService();
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

    // play music when onclick item in list
    private void playDetailMusic(int position, List<Music> lstMusics) {
        setIndexCurrentSong(position); // set index current for song
        setAllCurrentSongs(lstMusics); // init all song for music manager
        playSong(position);            // play song for index select
        startMusicForegroundService();
        showDetailMusic();
    }

    private void playSong(int pos) {
        mMusicService.playSong(pos);
    }

    private void setAllCurrentSongs(List<Music> lstMusics) {
        mMusicService.setAllCurrentSongs(lstMusics);
    }

    private void setIndexCurrentSong(int pos) {
        if (!mIsConnect) {
            mMusicService.setIndexCurrentSong(INIT_INDEX);
        }

        mMusicService.setIndexCurrentSong(pos);
    }

    public void startMusicForegroundService() {
        if (mIsForegroundRunning) {
            return;
        }
        Intent startIntent = new Intent(this, MusicService.class);
        startIntent.setAction(NotificationAction.START_FOREGROUND_ACTION);
        startService(startIntent);
        mIsForegroundRunning = true;
    }

    private void showDetailMusic() {
        Intent intent = new Intent(this, DetailMusicActivity.class);
        startActivity(intent);
    }

    @Override
    protected void onDestroy() {
        unbindService(mConnection);
        mIsConnect = false;
        super.onDestroy();
    }

    @Override
    protected void onRestart() {
        super.onRestart();
    }
}
