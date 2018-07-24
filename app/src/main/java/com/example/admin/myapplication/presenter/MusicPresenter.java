package com.example.admin.myapplication.presenter;

import android.content.Context;

import com.example.admin.myapplication.model.data.MyContentProvider;
import com.example.admin.myapplication.view.activity.music.MusicViewImp;

public class MusicPresenter implements MusicPresenterImp {
    private MyContentProvider myContentProvider;
    private MusicViewImp mMusicViewImp;
    private Context mContext;

    public MusicPresenter(MusicViewImp mMusicViewImp, Context context) {
        this.mMusicViewImp = mMusicViewImp;
        this.mContext = context;
        myContentProvider = new MyContentProvider(mContext);
    }

    @Override
    public void fncRequestGetDataFromAct() {
        mMusicViewImp.fncResultData(myContentProvider.readData());
    }
}
