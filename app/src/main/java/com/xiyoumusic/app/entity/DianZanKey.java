package com.xiyoumusic.app.entity;

public class DianZanKey {
    private String dzzxh;

    private Long gqid;

    public String getDzzxh() {
        return dzzxh;
    }

    public void setDzzxh(String dzzxh) {
        this.dzzxh = dzzxh == null ? null : dzzxh.trim();
    }

    public Long getGqid() {
        return gqid;
    }

    public void setGqid(Long gqid) {
        this.gqid = gqid;
    }
}