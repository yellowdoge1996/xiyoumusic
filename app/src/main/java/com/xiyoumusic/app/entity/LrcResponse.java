package com.xiyoumusic.app.entity;

import java.util.List;

public class LrcResponse {
    private int count;
    private int code;
    private List<LrcInfo> result;

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public List<LrcInfo> getResult() {
        return result;
    }

    public void setResult(List<LrcInfo> result) {
        this.result = result;
    }

    public class LrcInfo{
        private long aid;
        private String lrc;
        private String artist;
        private String song;
        private long sid;

        public long getAid() {
            return aid;
        }

        public void setAid(long aid) {
            this.aid = aid;
        }

        public String getLrc() {
            return lrc;
        }

        public void setLrc(String lrc) {
            this.lrc = lrc;
        }

        public String getArtist() {
            return artist;
        }

        public void setArtist(String artist) {
            this.artist = artist;
        }

        public String getSong() {
            return song;
        }

        public void setSong(String song) {
            this.song = song;
        }

        public long getSid() {
            return sid;
        }

        public void setSid(long sid) {
            this.sid = sid;
        }
    }
}
