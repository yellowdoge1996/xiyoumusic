package com.xiyoumusic.app.entity;

public class HuiFu {
    private Long hfid;

    private String hfnr;

    private Long plid;

    private String hfzxh;

    private Long hfmbid;

    private String bhfzxh;

    public Long getHfid() {
        return hfid;
    }

    public void setHfid(Long hfid) {
        this.hfid = hfid;
    }

    public String getHfnr() {
        return hfnr;
    }

    public void setHfnr(String hfnr) {
        this.hfnr = hfnr == null ? null : hfnr.trim();
    }

    public Long getPlid() {
        return plid;
    }

    public void setPlid(Long plid) {
        this.plid = plid;
    }

    public String getHfzxh() {
        return hfzxh;
    }

    public void setHfzxh(String hfzxh) {
        this.hfzxh = hfzxh == null ? null : hfzxh.trim();
    }

    public Long getHfmbid() {
        return hfmbid;
    }

    public void setHfmbid(Long hfmbid) {
        this.hfmbid = hfmbid;
    }

    public String getBhfzxh() {
        return bhfzxh;
    }

    public void setBhfzxh(String bhfzxh) {
        this.bhfzxh = bhfzxh == null ? null : bhfzxh.trim();
    }
}