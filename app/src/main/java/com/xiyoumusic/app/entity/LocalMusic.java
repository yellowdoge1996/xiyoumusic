package com.xiyoumusic.app.entity;


import android.text.TextUtils;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.io.Serializable;

/**
 * 单曲信息
 * Created by wcy on 2015/11/27.
 */
@Entity
public class LocalMusic implements Serializable {
    private static final long serialVersionUID = 536871008;

    @PrimaryKey
    private Long id;

    @ColumnInfo(name = "type")
    private int type; // 歌曲类型:本地/网络
    @ColumnInfo(name = "songId")
    private long songId; // [本地]歌曲ID
    @ColumnInfo(name = "title")
    private String title; // 音乐标题
    @ColumnInfo(name = "artist")
    private String artist; // 艺术家
    @ColumnInfo(name = "album")
    private String album; // 专辑
    @ColumnInfo(name = "albumId")
    private long albumId; // [本地]专辑ID
    @ColumnInfo(name = "coverPath")
    private String coverPath; // [在线]专辑封面路径
    @ColumnInfo(name = "duration")
    private long duration; // 持续时间
    @ColumnInfo(name = "path")
    private String path; // 播放地址
    @ColumnInfo(name = "fileName")
    private String fileName; // [本地]文件名
    @ColumnInfo(name = "fileSize")
    private long fileSize; // [本地]文件大小

    public interface Type {
        int LOCAL = 0;
        int ONLINE = 1;
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof LocalMusic)) {
            return false;
        }
        LocalMusic music = (LocalMusic) o;
        if (music.songId > 0 && music.songId == this.songId) {
            return true;
        }
        return TextUtils.equals(music.title, this.title)
                && TextUtils.equals(music.artist, this.artist)
                && TextUtils.equals(music.album, this.album)
                && music.duration == this.duration;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public long getSongId() {
        return songId;
    }

    public void setSongId(long songId) {
        this.songId = songId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getArtist() {
        return artist;
    }

    public void setArtist(String artist) {
        this.artist = artist;
    }

    public String getAlbum() {
        return album;
    }

    public void setAlbum(String album) {
        this.album = album;
    }

    public long getAlbumId() {
        return albumId;
    }

    public void setAlbumId(long albumId) {
        this.albumId = albumId;
    }

    public String getCoverPath() {
        return coverPath;
    }

    public void setCoverPath(String coverPath) {
        this.coverPath = coverPath;
    }

    public long getDuration() {
        return duration;
    }

    public void setDuration(long duration) {
        this.duration = duration;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public long getFileSize() {
        return fileSize;
    }

    public void setFileSize(long fileSize) {
        this.fileSize = fileSize;
    }
}
