package com.ruijie.rcos.rcdc.terminal.module.def.api.dto;

import com.alibaba.fastjson.annotation.JSONField;

/**
 * Description: 终端磁盘信息dto
 * Copyright: Copyright (c) 2020
 * Company: Ruijie Co., Ltd.
 * Create Time: 2020/9/16 10:28 上午
 *
 * @author zhouhuan
 */
public class CbbTerminalDiskInfoDTO {

    @JSONField(name = "dev_name")
    private String devName;

    @JSONField(name = "dev_type")
    private String devType;

    @JSONField(name = "dev_form")
    private String devForm;

    /**
     * 磁盘空间总大小，单位是byte
     */
    @JSONField(name = "dev_totalsize")
    private String devTotalSize;

    @JSONField(name = "dev_media")
    private String devMedia;

    @JSONField(name = "dev_state")
    private String devState;

    @JSONField(name = "dev_sn")
    private String devSn;

    @JSONField(name = "dev_firmwareVersion")
    private String devFirmwareVersion;

    @JSONField(name = "dev_health")
    private String devHealth;

    @JSONField(name = "dev_powerOnhour")
    private String devPowerOnhour;

    /**
     * 表示磁盘曾经写入过多少G
     */
    @JSONField(name = "dev_totalWritten")
    private String devTotalWritten;

    @JSONField(name = "dev_readIops")
    private String devReadIops;

    @JSONField(name = "dev_writeIops")
    private String devWriteIops;

    @JSONField(name = "dev_model")
    private String devModel;

    @JSONField(name = "dev_avil")
    private String devAvil;

    public String getDevName() {
        return devName;
    }

    public void setDevName(String devName) {
        this.devName = devName;
    }

    public String getDevType() {
        return devType;
    }

    public void setDevType(String devType) {
        this.devType = devType;
    }

    public String getDevForm() {
        return devForm;
    }

    public void setDevForm(String devForm) {
        this.devForm = devForm;
    }

    public String getDevTotalSize() {
        return devTotalSize;
    }

    public void setDevTotalSize(String devTotalSize) {
        this.devTotalSize = devTotalSize;
    }

    public String getDevMedia() {
        return devMedia;
    }

    public void setDevMedia(String devMedia) {
        this.devMedia = devMedia;
    }

    public String getDevState() {
        return devState;
    }

    public void setDevState(String devState) {
        this.devState = devState;
    }

    public String getDevSn() {
        return devSn;
    }

    public void setDevSn(String devSn) {
        this.devSn = devSn;
    }

    public String getDevFirmwareVersion() {
        return devFirmwareVersion;
    }

    public void setDevFirmwareVersion(String devFirmwareVersion) {
        this.devFirmwareVersion = devFirmwareVersion;
    }

    public String getDevHealth() {
        return devHealth;
    }

    public void setDevHealth(String devHealth) {
        this.devHealth = devHealth;
    }

    public String getDevPowerOnhour() {
        return devPowerOnhour;
    }

    public void setDevPowerOnhour(String devPowerOnhour) {
        this.devPowerOnhour = devPowerOnhour;
    }

    public String getDevTotalWritten() {
        return devTotalWritten;
    }

    public void setDevTotalWritten(String devTotalWritten) {
        this.devTotalWritten = devTotalWritten;
    }

    public String getDevReadIops() {
        return devReadIops;
    }

    public void setDevReadIops(String devReadIops) {
        this.devReadIops = devReadIops;
    }

    public String getDevWriteIops() {
        return devWriteIops;
    }

    public void setDevWriteIops(String devWriteIops) {
        this.devWriteIops = devWriteIops;
    }

    public String getDevModel() {
        return devModel;
    }

    public void setDevModel(String devModel) {
        this.devModel = devModel;
    }

    public String getDevAvil() {
        return devAvil;
    }

    public void setDevAvil(String devAvil) {
        this.devAvil = devAvil;
    }
}
