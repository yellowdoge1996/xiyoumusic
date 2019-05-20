package com.xiyoumusic.app.database;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import com.xiyoumusic.app.entity.LocalMusic;

import java.util.List;

@Dao
public interface LocalMusicDao {

    @Query("select * from LocalMusic")
    List<LocalMusic> getAllMusic();

    @Query("select * from LocalMusic where id in (:userIds)")
    List<LocalMusic> loadAllByIds(int[] userIds);

    @Query("select * from LocalMusic where id = :id")
    LocalMusic getUserInfoViaId(int id);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    long[]  insertAll(LocalMusic... localMusics);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    long insertLocalMusic(LocalMusic localMusic);

    @Update
    int Update(LocalMusic... localMusics);

    @Delete
    int delete(LocalMusic... localMusics);
}
