package com.example.admin.myapplication.model;

public class Music {
    private long mId;
    private String mNameSong;
    private String mUriSong;
    private String mAuthorSong;
    private long mSize;
    private long mTime;

    public Music(long mId, String mNameSong, String mUriSong, String mAuthorSong, long mSize, long mTime) {
        this.mId = mId;
        this.mNameSong = mNameSong;
        this.mUriSong = mUriSong;
        this.mAuthorSong = mAuthorSong;
        this.mSize = mSize;
        this.mTime = mTime;
    }

    public long getmId() {
        return mId;
    }

    public void setmId(long mId) {
        this.mId = mId;
    }

    public String getmNameSong() {
        return mNameSong;
    }

    public void setmNameSong(String mNameSong) {
        this.mNameSong = mNameSong;
    }

    public String getmUriSong() {
        return mUriSong;
    }

    public void setmUriSong(String mUriSong) {
        this.mUriSong = mUriSong;
    }

    public String getmAuthorSong() {
        return mAuthorSong;
    }

    public void setmAuthorSong(String mAuthorSong) {
        this.mAuthorSong = mAuthorSong;
    }

    public long getmSize() {
        return mSize;
    }

    public void setmSize(long mSize) {
        this.mSize = mSize;
    }

    public long getmTime() {
        return mTime;
    }

    public void setmTime(long mTime) {
        this.mTime = mTime;
    }
}
