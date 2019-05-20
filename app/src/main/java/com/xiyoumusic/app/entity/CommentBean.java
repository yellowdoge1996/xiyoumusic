package com.xiyoumusic.app.entity;

import java.util.List;

public class CommentBean extends PingLun{
    private List<HuifuDetails> huiFuList;
    private String txlj;
    private String sr;
    private String gxqm;

    public List<HuifuDetails> getHuiFuList() {
        return huiFuList;
    }

    public void setHuiFuList(List<HuifuDetails> huiFuList) {
        this.huiFuList = huiFuList;
    }

    public String getTxlj() {
        return txlj;
    }

    public void setTxlj(String txlj) {
        this.txlj = txlj;
    }

    public String getSr() {
        return sr;
    }

    public void setSr(String sr) {
        this.sr = sr;
    }

    public String getGxqm() {
        return gxqm;
    }

    public void setGxqm(String gxqm) {
        this.gxqm = gxqm;
    }
}
