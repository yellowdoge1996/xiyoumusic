package com.xiyoumusic.app.database;

import android.content.Context;

import androidx.room.Room;
import androidx.room.RoomDatabase;

/**
 * @Author Lyf
 * @CreateTime 2018/2/6
 * @Description DataBaseManager
 **/
public class DataBaseManager {

    private static DataBaseManager INSTANCE = null;

    private void DataBaseManager(){}

    public static DataBaseManager getInstance() {

        if(INSTANCE == null) {
            INSTANCE = new DataBaseManager();
        }

        return INSTANCE;
    }

    public <T extends RoomDatabase> T getDataBase(Context context, Class<T> tClass) {
        return Room.databaseBuilder(context, tClass, tClass.getSimpleName()).build();
    }

}