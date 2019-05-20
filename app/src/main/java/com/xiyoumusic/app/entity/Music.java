package com.xiyoumusic.app.entity;

import java.io.Serializable;

public class Music implements Serializable {
    private static final long serialVersionUID = 536871008;

    private Long gqid;

    private String zzxh;

    private String gqmc;

    private String gqlj;

    private String gqct;

    public Long getGqid() {
        return gqid;
    }

    public void setGqid(Long gqid) {
        this.gqid = gqid;
    }

    public String getZzxh() {
        return zzxh;
    }

    public void setZzxh(String zzxh) {
        this.zzxh = zzxh == null ? null : zzxh.trim();
    }

    public String getGqmc() {
        return gqmc;
    }

    public void setGqmc(String gqmc) {
        this.gqmc = gqmc == null ? null : gqmc.trim();
    }

    public String getGqlj() {
        return gqlj;
    }

    public void setGqlj(String gqlj) {
        this.gqlj = gqlj == null ? null : gqlj.trim();
    }

    public String getGqct() {
        return gqct;
    }

    public void setGqct(String gqct) {
        this.gqct = gqct == null ? null : gqct.trim();
    }
}