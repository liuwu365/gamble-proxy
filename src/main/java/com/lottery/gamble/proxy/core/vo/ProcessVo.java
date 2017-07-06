package com.lottery.gamble.proxy.core.vo;

import java.util.Date;

/**
 * Created by zhuzongguang on 2016/6/28.
 */
public class ProcessVo{

    private Long crId;
    private String crName;
    private Byte crBo;
    private Byte crStatus;
    private Byte crPhase;
    private Date crStartTime;
    private Byte crWeight;
    private Long coId;
    private String coName;

    public Long getCrId() {
        return crId;
    }

    public void setCrId(Long crId) {
        this.crId = crId;
    }

    public String getCrName() {
        return crName;
    }

    public void setCrName(String crName) {
        this.crName = crName;
    }

    public Byte getCrBo() {
        return crBo;
    }

    public void setCrBo(Byte crBo) {
        this.crBo = crBo;
    }

    public Byte getCrStatus() {
        return crStatus;
    }

    public void setCrStatus(Byte crStatus) {
        this.crStatus = crStatus;
    }

    public Byte getCrPhase() {
        return crPhase;
    }

    public void setCrPhase(Byte crPhase) {
        this.crPhase = crPhase;
    }

    public Date getCrStartTime() {
        return crStartTime;
    }

    public void setCrStartTime(Date crStartTime) {
        this.crStartTime = crStartTime;
    }

    public Byte getCrWeight() {
        return crWeight;
    }

    public void setCrWeight(Byte crWeight) {
        this.crWeight = crWeight;
    }

    public Long getCoId() {
        return coId;
    }

    public void setCoId(Long coId) {
        this.coId = coId;
    }

    public String getCoName() {
        return coName;
    }

    public void setCoName(String coName) {
        this.coName = coName;
    }

}
