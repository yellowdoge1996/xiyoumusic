package com.xiyoumusic.app.database;


import android.content.Context;

import androidx.room.Database;
import androidx.room.RoomDatabase;

import com.xiyoumusic.app.entity.LocalMusic;

@Database(entities = {LocalMusic.class}, version = 1, exportSchema = false)
public abstract class AppDataBase extends RoomDatabase {
    public abstract LocalMusicDao getLocalMusicDao();

    public static AppDataBase getInstance(Context context) {
        return DataBaseManager.getInstance().getDataBase(context,AppDataBase.class);
    }
}
