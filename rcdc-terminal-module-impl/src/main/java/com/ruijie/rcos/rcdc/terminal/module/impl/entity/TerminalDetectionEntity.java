package com.ruijie.rcos.rcdc.terminal.module.impl.entity;

import javax.persistence.*;
import java.util.Date;
import java.util.UUID;

/**
 * Description: 终端检测实体类
 * Copyright: Copyright (c) 2018
 * Company: Ruijie Co., Ltd.
 * Create Time: 2018/10/30
 *
 * @author Jarman
 */
@Entity
@Table(name = "t_terminal_detection")
public class TerminalDetectionEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;
    private Short isIpConflict;
    private String ipConflictMac;
    private Short canAccessInternet;
    private String bandwidth;
    private String packetLossRate;
    private Integer diskFreeSpace;
    private Integer diskTotalSpace;
    private Date detectTime;

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public Short getIsIpConflict() {
        return isIpConflict;
    }

    public void setIsIpConflict(Short isIpConflict) {
        this.isIpConflict = isIpConflict;
    }

    public String getIpConflictMac() {
        return ipConflictMac;
    }

    public void setIpConflictMac(String ipConflictMac) {
        this.ipConflictMac = ipConflictMac;
    }

    public Short getCanAccessInternet() {
        return canAccessInternet;
    }

    public void setCanAccessInternet(Short canAccessInternet) {
        this.canAccessInternet = canAccessInternet;
    }

    public String getBandwidth() {
        return bandwidth;
    }

    public void setBandwidth(String bandwidth) {
        this.bandwidth = bandwidth;
    }

    public String getPacketLossRate() {
        return packetLossRate;
    }

    public void setPacketLossRate(String packetLossRate) {
        this.packetLossRate = packetLossRate;
    }

    public Integer getDiskFreeSpace() {
        return diskFreeSpace;
    }

    public void setDiskFreeSpace(Integer diskFreeSpace) {
        this.diskFreeSpace = diskFreeSpace;
    }

    public Integer getDiskTotalSpace() {
        return diskTotalSpace;
    }

    public void setDiskTotalSpace(Integer diskTotalSpace) {
        this.diskTotalSpace = diskTotalSpace;
    }

    public Date getDetectTime() {
        return detectTime;
    }

    public void setDetectTime(Date detectTime) {
        this.detectTime = detectTime;
    }
}