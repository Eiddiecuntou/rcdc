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

    @JSONField(name = "dev_totalsize")
    private Integer devTotalSize;

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
    private Integer devPowerOnhour;

    @JSONField(name = "dev_totalWritten")
    private Integer devTotalWritten;

    @JSONField(name = "dev_readIops")
    private Integer devReadIops;

    @JSONField(name = "dev_writeIops")
    private Integer devWriteIops;

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

    public Integer getDevTotalSize() {
        return devTotalSize;
    }

    public void setDevTotalSize(Integer devTotalSize) {
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

    public Integer getDevPowerOnhour() {
        return devPowerOnhour;
    }

    public void setDevPowerOnhour(Integer devPowerOnhour) {
        this.devPowerOnhour = devPowerOnhour;
    }

    public Integer getDevTotalWritten() {
        return devTotalWritten;
    }

    public void setDevTotalWritten(Integer devTotalWritten) {
        this.devTotalWritten = devTotalWritten;
    }

    public Integer getDevReadIops() {
        return devReadIops;
    }

    public void setDevReadIops(Integer devReadIops) {
        this.devReadIops = devReadIops;
    }

    public Integer getDevWriteIops() {
        return devWriteIops;
    }

    public void setDevWriteIops(Integer devWriteIops) {
        this.devWriteIops = devWriteIops;
    }
}
