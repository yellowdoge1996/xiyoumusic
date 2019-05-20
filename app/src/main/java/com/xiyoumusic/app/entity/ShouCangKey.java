package com.xiyoumusic.app.entity;

public class ShouCangKey {
    private String xh;

    private Long gqid;

    public String getXh() {
        return xh;
    }

    public void setXh(String xh) {
        this.xh = xh == null ? null : xh.trim();
    }

    public Long getGqid() {
        return gqid;
    }

    public void setGqid(Long gqid) {
        this.gqid = gqid;
    }
}