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

    private String terminalId;

    /**
     * ip冲突结果，如果有冲突则保存冲突的mac地址，否则为空值
     */
    private String ipConflict;

    /**
     * 是否可访问外网，0不能访问，1可访问
     */
    private Integer canAccessInternet;

    /**
     * 带宽大小
     */
    private Double bandwidth;

    /**
     * 丢包率
     */
    private Double packetLossRate;

    /**
     * 网络时延
     */
    private Double networkDelay;

    /**
     * 检测时间
     */
    private Date detectTime;
    
    @Version
    private int version;

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getTerminalId() {
        return terminalId;
    }

    public void setTerminalId(String terminalId) {
        this.terminalId = terminalId;
    }

    public String getIpConflict() {
        return ipConflict;
    }

    public void setIpConflict(String ipConflict) {
        this.ipConflict = ipConflict;
    }

    public Integer getCanAccessInternet() {
        return canAccessInternet;
    }

    public void setCanAccessInternet(Integer canAccessInternet) {
        this.canAccessInternet = canAccessInternet;
    }

    public Double getBandwidth() {
        return bandwidth;
    }

    public void setBandwidth(Double bandwidth) {
        this.bandwidth = bandwidth;
    }

    public Double getPacketLossRate() {
        return packetLossRate;
    }

    public void setPacketLossRate(Double packetLossRate) {
        this.packetLossRate = packetLossRate;
    }

    public Date getDetectTime() {
        return detectTime;
    }

    public void setDetectTime(Date detectTime) {
        this.detectTime = detectTime;
    }

    public Double getNetworkDelay() {
        return networkDelay;
    }

    public void setNetworkDelay(Double networkDelay) {
        this.networkDelay = networkDelay;
    }
    
    public void setVersion(int version) {
        this.version = version;
    }
    
    public int getVersion() {
        return version;
    }
}