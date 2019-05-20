package com.xiyoumusic.app.entity;

public class GuanZhuKey {
    private String gzzxh;

    private String bgzzxh;

    public String getGzzxh() {
        return gzzxh;
    }

    public void setGzzxh(String gzzxh) {
        this.gzzxh = gzzxh == null ? null : gzzxh.trim();
    }

    public String getBgzzxh() {
        return bgzzxh;
    }

    public void setBgzzxh(String bgzzxh) {
        this.bgzzxh = bgzzxh == null ? null : bgzzxh.trim();
    }
}