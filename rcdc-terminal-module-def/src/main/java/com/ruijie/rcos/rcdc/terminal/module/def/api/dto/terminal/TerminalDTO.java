package com.ruijie.rcos.rcdc.terminal.module.def.api.dto.terminal;

import java.util.Date;
import java.util.UUID;
import com.ruijie.rcos.rcdc.terminal.module.def.api.enums.CbbTerminalStateEnums;

/**
 * 
 * Description: 终端信息DTO
 * Copyright: Copyright (c) 2018
 * Company: Ruijie Co., Ltd.
 * Create Time: 2018年12月18日
 * 
 * @author nt
 */
public class TerminalDTO {

    /**
     * 终端id
     */
    private String id;

    /**
     * 终端名称
     */
    private String terminalName;

    /**
     * 分组id
     */
    private UUID terminalGroupId;

    /**
     * 分组名称
     */
    private String terminalGroupName;

    private String[] terminalGroupNameArr;

    /**
     * 终端状态
     */
    private CbbTerminalStateEnums terminalState;

    /**
     * 终端系统版本号
     */
    private String rainOsVersion;

    /**
     * 硬件版本号
     */
    private String hardwareVersion;

    /**
     * 软件版本号，指组件升级包的版本号
     */
    private String rainUpgradeVersion;

    /**
     * 终端最近一次离线的时间点
     */
    private Date lastOfflineTime;

    /**
     * 终端最近接入的时间点
     */
    private Date lastOnlineTime;

    /**
     * 终端最近接入的时间点
     */
    private String macAddr;

    /**
     * 磁盘大小，单位kb
     */
    private Long diskSize;

    /**
     * cup型号
     */
    private String cpuMode;

    /**
     * 终端ip地址
     */
    private String ip;

    /**
     * 内存大小，单位kb
     */
    private Long memorySize;

    /**
     * ip是否冲突
     */
    private Integer ipConflict;

    /**
     * 外网访问是否正常
     */
    private Integer accessInternet;

    /**
     * 丢包率
     */
    private Double packetLossRate;

    /**
     * 网络时延
     */
    private Double delay;

    /**
     * 带宽大小
     */
    private Double bandwidth;

    /**
     * 检测时间
     */
    private Date detectTime;

    private String productType;

    /**
     * 带宽阈值
     */
    private Double bandwidthThreshold;

    /**
     * 丢包率阈值
     */
    private Double packetLossRateThreshold;

    /**
     * 时延阈值
     */
    private Integer delayThreshold;
    
    private String detectState;


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTerminalName() {
        return terminalName;
    }

    public void setTerminalName(String terminalName) {
        this.terminalName = terminalName;
    }

    public UUID getTerminalGroupId() {
        return terminalGroupId;
    }

    public void setTerminalGroupId(UUID terminalGroupId) {
        this.terminalGroupId = terminalGroupId;
    }

    public String getTerminalGroupName() {
        return terminalGroupName;
    }

    public void setTerminalGroupName(String terminalGroupName) {
        this.terminalGroupName = terminalGroupName;
    }

    public CbbTerminalStateEnums getTerminalState() {
        return terminalState;
    }

    public void setTerminalState(CbbTerminalStateEnums terminalState) {
        this.terminalState = terminalState;
    }

    public String getRainOsVersion() {
        return rainOsVersion;
    }

    public void setRainOsVersion(String rainOsVersion) {
        this.rainOsVersion = rainOsVersion;
    }

    public String getHardwareVersion() {
        return hardwareVersion;
    }

    public void setHardwareVersion(String hardwareVersion) {
        this.hardwareVersion = hardwareVersion;
    }

    public String getRainUpgradeVersion() {
        return rainUpgradeVersion;
    }

    public void setRainUpgradeVersion(String rainUpgradeVersion) {
        this.rainUpgradeVersion = rainUpgradeVersion;
    }

    public Date getLastOfflineTime() {
        return lastOfflineTime;
    }

    public void setLastOfflineTime(Date lastOfflineTime) {
        this.lastOfflineTime = lastOfflineTime;
    }

    public Date getLastOnlineTime() {
        return lastOnlineTime;
    }

    public void setLastOnlineTime(Date lastOnlineTime) {
        this.lastOnlineTime = lastOnlineTime;
    }

    public String getMacAddr() {
        return macAddr;
    }

    public void setMacAddr(String macAddr) {
        this.macAddr = macAddr;
    }

    public Long getDiskSize() {
        return diskSize;
    }

    public void setDiskSize(Long diskSize) {
        this.diskSize = diskSize;
    }

    public Long getMemorySize() {
        return memorySize;
    }

    public void setMemorySize(Long memorySize) {
        this.memorySize = memorySize;
    }

    public String getCpuMode() {
        return cpuMode;
    }

    public void setCpuMode(String cpuMode) {
        this.cpuMode = cpuMode;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }


    public Integer getIpConflict() {
        return ipConflict;
    }

    public void setIpConflict(Integer ipConflict) {
        this.ipConflict = ipConflict;
    }

    public Integer getAccessInternet() {
        return accessInternet;
    }

    public void setAccessInternet(Integer accessInternet) {
        this.accessInternet = accessInternet;
    }

    public Double getPacketLossRate() {
        return packetLossRate;
    }

    public void setPacketLossRate(Double packetLossRate) {
        this.packetLossRate = packetLossRate;
    }

    public Double getDelay() {
        return delay;
    }

    public void setDelay(Double delay) {
        this.delay = delay;
    }

    public Double getBandwidth() {
        return bandwidth;
    }

    public void setBandwidth(Double bandwidth) {
        this.bandwidth = bandwidth;
    }

    public Date getDetectTime() {
        return detectTime;
    }

    public void setDetectTime(Date detectTime) {
        this.detectTime = detectTime;
    }

    public String getProductType() {
        return productType;
    }

    public void setProductType(String productType) {
        this.productType = productType;
    }

    public Double getBandwidthThreshold() {
        return bandwidthThreshold;
    }

    public void setBandwidthThreshold(Double bandwidthThreshold) {
        this.bandwidthThreshold = bandwidthThreshold;
    }

    public Double getPacketLossRateThreshold() {
        return packetLossRateThreshold;
    }

    public void setPacketLossRateThreshold(Double packetLossRateThreshold) {
        this.packetLossRateThreshold = packetLossRateThreshold;
    }

    public Integer getDelayThreshold() {
        return delayThreshold;
    }

    public void setDelayThreshold(Integer delayThreshold) {
        this.delayThreshold = delayThreshold;
    }

    public String getDetectState() {
        return detectState;
    }

    public void setDetectState(String detectState) {
        this.detectState = detectState;
    }

    public String[] getTerminalGroupNameArr() {
        return terminalGroupNameArr;
    }

    public void setTerminalGroupNameArr(String[] terminalGroupNameArr) {
        this.terminalGroupNameArr = terminalGroupNameArr;
    }
}
