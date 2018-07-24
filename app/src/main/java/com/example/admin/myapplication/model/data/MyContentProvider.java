package com.example.admin.myapplication.model.data;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.provider.MediaStore;

import com.example.admin.myapplication.model.Music;

import java.util.ArrayList;
import java.util.List;

public class MyContentProvider {
    private ContentResolver mContentResolver;
    public static final String[] COLUMN_NAME = {
            MediaStore.Audio.Media._ID,
            MediaStore.Audio.Media.DATA,
            MediaStore.Audio.Media.TITLE,
            MediaStore.Audio.Media.SIZE,
            MediaStore.Audio.Media.DURATION,
            MediaStore.Audio.Media.ALBUM
    };

    public MyContentProvider(Context context) {
        mContentResolver = context.getContentResolver();
    }

    public List<Music> readData() {
        List<Music> arrAudio = new ArrayList<Music>();
        Cursor cursor = mContentResolver.query
                (MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, COLUMN_NAME, null, null, null);
        cursor.moveToFirst();
        while (cursor.isAfterLast() == false) {
            int id = cursor.getInt(MediaPlayerState.ROW_TABLE_0);
            String data = cursor.getString(MediaPlayerState.ROW_TABLE_1);
            int size = cursor.getInt(MediaPlayerState.ROW_TABLE_3);
            String title = cursor.getString(MediaPlayerState.ROW_TABLE_2);
            int duration = cursor.getInt(MediaPlayerState.ROW_TABLE_4);
            String album = cursor.getString(MediaPlayerState.ROW_TABLE_5);
            Music audioItem = new Music(id, title, data, album, size, duration);
            arrAudio.add(audioItem);
            cursor.moveToNext();
        }
        return arrAudio;
    }
}
