package com.xiyoumusic.app.entity;

public class PingLun {
    private Long plid;

    private String plnr;

    private String plzxh;

    private Long gqid;

    private String plznc;

    public Long getPlid() {
        return plid;
    }

    public void setPlid(Long plid) {
        this.plid = plid;
    }

    public String getPlnr() {
        return plnr;
    }

    public void setPlnr(String plnr) {
        this.plnr = plnr == null ? null : plnr.trim();
    }

    public String getPlzxh() {
        return plzxh;
    }

    public void setPlzxh(String plzxh) {
        this.plzxh = plzxh == null ? null : plzxh.trim();
    }

    public Long getGqid() {
        return gqid;
    }

    public void setGqid(Long gqid) {
        this.gqid = gqid;
    }

    public String getPlznc() {
        return plznc;
    }

    public void setPlznc(String plznc) {
        this.plznc = plznc == null ? null : plznc.trim();
    }
}