package com.xiyoumusic.app.entity;

import java.util.List;

public class MyResponse<T extends List> {
    private String state = null;
    private String msg = null;
    private String error = null;
    private List<User> userData = null;
    private List<GuanZhu> gzData = null;

    private List<Music> musicData = null;
    private List<ShouCang> scDate = null;
    private List<DianZan> dzData = null;
    private List<PingLun> plData = null;
    private List<HuiFu> hfData = null;

    private List<LiuYan> lyData = null;
    private List<CommentBean> commentData = null;
    private List<Dongtai> dongtaiData = null;
    public MyResponse() {
    }

    public List<Dongtai> getDongtaiData() {
        return dongtaiData;
    }

    public void setDongtaiData(List<Dongtai> dongtaiData) {
        this.dongtaiData = dongtaiData;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }

    public List<User> getUserData() {
        return userData;
    }

    public void setUserData(List<User> userData) {
        this.userData = userData;
    }

    public List<GuanZhu> getGzData() {
        return gzData;
    }

    public void setGzData(List<GuanZhu> gzData) {
        this.gzData = gzData;
    }

    public List<Music> getMusicData() {
        return musicData;
    }

    public void setMusicData(List<Music> musicData) {
        this.musicData = musicData;
    }

    public List<ShouCang> getScDate() {
        return scDate;
    }

    public void setScDate(List<ShouCang> scDate) {
        this.scDate = scDate;
    }

    public List<DianZan> getDzData() {
        return dzData;
    }

    public void setDzData(List<DianZan> dzData) {
        this.dzData = dzData;
    }

    public List<PingLun> getPlData() {
        return plData;
    }

    public void setPlData(List<PingLun> plData) {
        this.plData = plData;
    }

    public List<HuiFu> getHfData() {
        return hfData;
    }

    public void setHfData(List<HuiFu> hfData) {
        this.hfData = hfData;
    }

    public List<LiuYan> getLyData() {
        return lyData;
    }

    public void setLyData(List<LiuYan> lyData) {
        this.lyData = lyData;
    }

    public List<CommentBean> getCommentData() {
        return commentData;
    }

    public void setCommentData(List<CommentBean> commentData) {
        this.commentData = commentData;
    }
}
