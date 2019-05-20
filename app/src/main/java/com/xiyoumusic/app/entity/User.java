package com.xiyoumusic.app.entity;

import java.util.Date;

public class User {
    private String xh;

    private String nc;

    private String mm;

    private String uuid;

    private String gxqm;

    private String sr;

    public User() {
    }

    public User(String xh, String mm) {
        this.xh = xh;
        this.mm = mm;
    }

    private String txlj;

    public String getXh() {
        return xh;
    }

    public void setXh(String xh) {
        this.xh = xh == null ? null : xh.trim();
    }

    public String getNc() {
        return nc;
    }

    public void setNc(String nc) {
        this.nc = nc == null ? null : nc.trim();
    }

    public String getMm() {
        return mm;
    }

    public void setMm(String mm) {
        this.mm = mm == null ? null : mm.trim();
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid == null ? null : uuid.trim();
    }

    public String getGxqm() {
        return gxqm;
    }

    public void setGxqm(String gxqm) {
        this.gxqm = gxqm == null ? null : gxqm.trim();
    }

    public String getSr() {
        return sr;
    }

    public void setSr(String sr) {
        this.sr = sr;
    }

    public String getTxlj() {
        return txlj;
    }

    public void setTxlj(String txlj) {
        this.txlj = txlj == null ? null : txlj.trim();
    }
}