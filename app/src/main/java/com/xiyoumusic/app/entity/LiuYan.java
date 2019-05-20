package com.xiyoumusic.app.entity;

public class LiuYan {
    private Long lyid;

    private String lyzxh;

    private String jszxh;

    private String lynr;

    public Long getLyid() {
        return lyid;
    }

    public void setLyid(Long lyid) {
        this.lyid = lyid;
    }

    public String getLyzxh() {
        return lyzxh;
    }

    public void setLyzxh(String lyzxh) {
        this.lyzxh = lyzxh == null ? null : lyzxh.trim();
    }

    public String getJszxh() {
        return jszxh;
    }

    public void setJszxh(String jszxh) {
        this.jszxh = jszxh == null ? null : jszxh.trim();
    }

    public String getLynr() {
        return lynr;
    }

    public void setLynr(String lynr) {
        this.lynr = lynr == null ? null : lynr.trim();
    }
}