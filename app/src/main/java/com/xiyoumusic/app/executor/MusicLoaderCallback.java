package com.xiyoumusic.app.executor;

import android.app.LoaderManager;
import android.content.Context;
import android.content.Loader;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.BaseColumns;
import android.provider.MediaStore;
import android.webkit.ValueCallback;

import com.xiyoumusic.app.entity.LocalMusic;
import com.xiyoumusic.app.utils.ConstantTools;
import com.xiyoumusic.app.utils.CoverLoader;
import com.xiyoumusic.app.utils.ParseUtils;
import com.xiyoumusic.app.utils.SPTool;
import com.xiyoumusic.app.utils.SystemUtils;

import java.util.ArrayList;
import java.util.List;

public class MusicLoaderCallback implements LoaderManager.LoaderCallbacks {
    private final List<LocalMusic> musicList;
    private final Context context;
    private final ValueCallback<List<LocalMusic>> callback;

    public MusicLoaderCallback(Context context, ValueCallback<List<LocalMusic>> callback) {
        this.context = context;
        this.callback = callback;
        this.musicList = new ArrayList<>();
    }

    public Loader onCreateLoader(int id, Bundle args) {
        return new MusicCursorLoader(context);
    }

    public void onLoadFinished(Loader var1, Object var2) {
        this.onLoadFinished(var1, (Cursor) var2);
    }

    public void onLoaderReset(Loader loader) {
    }

    public void onLoadFinished(Loader loader, Cursor data) {
        if (data == null) {
            return;
        }

        long filterTime = ParseUtils.parseLong(SPTool.getSharedPreferences().getString(ConstantTools.FILTER_SIZE, "0")) * 1000;
        long filterSize = ParseUtils.parseLong(SPTool.getSharedPreferences().getString(ConstantTools.FILTER_TIME, "0")) * 1024;

        int counter = 0;
        musicList.clear();
        while (data.moveToNext()) {
            // 是否为音乐，魅族手机上始终为0
            int isMusic = data.getInt(data.getColumnIndex(MediaStore.Audio.AudioColumns.IS_MUSIC));
            if (!SystemUtils.isFlyme() && isMusic == 0) {
                continue;
            }
            long duration = data.getLong(data.getColumnIndex(MediaStore.Audio.Media.DURATION));
            if (duration < filterTime) {
                continue;
            }
            long fileSize = data.getLong(data.getColumnIndex(MediaStore.Audio.Media.SIZE));
            if (fileSize < filterSize) {
                continue;
            }

            long id = data.getLong(data.getColumnIndex(BaseColumns._ID));
            String title = data.getString(data.getColumnIndex(MediaStore.Audio.AudioColumns.TITLE));
            String artist = data.getString(data.getColumnIndex(MediaStore.Audio.AudioColumns.ARTIST));
            String album = data.getString(data.getColumnIndex(MediaStore.Audio.AudioColumns.ALBUM));
            long albumId = data.getLong(data.getColumnIndex(MediaStore.Audio.AudioColumns.ALBUM_ID));
            String path = data.getString(data.getColumnIndex(MediaStore.Audio.AudioColumns.DATA));
            String fileName = data.getString(data.getColumnIndex(MediaStore.Audio.AudioColumns.DISPLAY_NAME));

            LocalMusic music = new LocalMusic();
            music.setSongId(id);
            music.setType(LocalMusic.Type.LOCAL);
            music.setTitle(title);
            music.setArtist(artist);
            music.setAlbum(album);
            music.setAlbumId(albumId);
            music.setDuration(duration);
            music.setPath(path);
            music.setFileName(fileName);
            music.setFileSize(fileSize);
            if (++counter <= 20) {
                // 只加载前20首的缩略图
                CoverLoader.get().loadThumb(music);
            }
            musicList.add(music);
        }

        callback.onReceiveValue(musicList);
    }
}
