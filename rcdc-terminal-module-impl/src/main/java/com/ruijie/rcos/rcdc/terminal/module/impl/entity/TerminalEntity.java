package com.ruijie.rcos.rcdc.terminal.module.impl.entity;


import java.util.Date;
import java.util.UUID;
import javax.persistence.*;

import com.ruijie.rcos.rcdc.terminal.module.def.api.enums.*;
import com.ruijie.rcos.rcdc.terminal.module.def.enums.CbbTerminalPlatformEnums;

/**
 * Description: 终端基本信息实体类
 * Copyright: Copyright (c) 2018
 * Company: Ruijie Co., Ltd.
 * Create Time: 2018/10/30
 *
 * @author Jarman
 */
@Entity
@Table(name = "t_cbb_terminal")
public class TerminalEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    private String terminalName;

    private String terminalId;

    private String macAddr;

    private String ip;

    private String subnetMask;

    private String gateway;

    private String mainDns;

    private String secondDns;

    @Enumerated(EnumType.STRING)
    private CbbGetNetworkModeEnums getIpMode;

    @Enumerated(EnumType.STRING)
    private CbbGetNetworkModeEnums getDnsMode;

    private String productType;

    @Enumerated(EnumType.STRING)
    private CbbTerminalPlatformEnums platform;

    private String serialNumber;

    private String cpuType;

    private Long memorySize;

    private Long diskSize;

    private String terminalOsType;

    private String terminalOsVersion;

    private String rainOsVersion;

    private String rainUpgradeVersion;

    private String hardwareVersion;

    @Enumerated(EnumType.STRING)
    private CbbNetworkModeEnums networkAccessMode;

    private Date createTime;

    private Date lastOnlineTime;

    private Date lastOfflineTime;

    @Version
    private Integer version;

    @Enumerated(EnumType.STRING)
    private CbbTerminalStateEnums state;

    private UUID groupId;

    private String osInnerVersion;

    @Enumerated(EnumType.STRING)
    private CbbIDVTerminalModeEnums idvTerminalMode;

    private String ssid;

    @Enumerated(EnumType.STRING)
    private CbbTerminalWirelessAuthModeEnums wirelessAuthMode;

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getTerminalName() {
        return terminalName;
    }

    public void setTerminalName(String terminalName) {
        this.terminalName = terminalName;
    }

    public String getTerminalId() {
        return terminalId;
    }

    public void setTerminalId(String terminalId) {
        this.terminalId = terminalId;
    }

    public String getMacAddr() {
        return macAddr;
    }

    public void setMacAddr(String macAddr) {
        this.macAddr = macAddr;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public String getSubnetMask() {
        return subnetMask;
    }

    public void setSubnetMask(String subnetMask) {
        this.subnetMask = subnetMask;
    }

    public String getGateway() {
        return gateway;
    }

    public void setGateway(String gateway) {
        this.gateway = gateway;
    }

    public String getMainDns() {
        return mainDns;
    }

    public void setMainDns(String mainDns) {
        this.mainDns = mainDns;
    }

    public String getSecondDns() {
        return secondDns;
    }

    public void setSecondDns(String secondDns) {
        this.secondDns = secondDns;
    }


    public String getProductType() {
        return productType;
    }

    public void setProductType(String productType) {
        this.productType = productType;
    }

    public CbbTerminalPlatformEnums getPlatform() {
        return platform;
    }

    public void setPlatform(CbbTerminalPlatformEnums platform) {
        this.platform = platform;
    }

    public String getSerialNumber() {
        return serialNumber;
    }

    public void setSerialNumber(String serialNumber) {
        this.serialNumber = serialNumber;
    }

    public String getCpuType() {
        return cpuType;
    }

    public void setCpuType(String cpuType) {
        this.cpuType = cpuType;
    }

    public Long getMemorySize() {
        return memorySize;
    }

    public void setMemorySize(Long memorySize) {
        this.memorySize = memorySize;
    }

    public Long getDiskSize() {
        return diskSize;
    }

    public void setDiskSize(Long diskSize) {
        this.diskSize = diskSize;
    }

    public String getTerminalOsType() {
        return terminalOsType;
    }

    public void setTerminalOsType(String terminalOsType) {
        this.terminalOsType = terminalOsType;
    }

    public String getTerminalOsVersion() {
        return terminalOsVersion;
    }

    public void setTerminalOsVersion(String terminalOsVersion) {
        this.terminalOsVersion = terminalOsVersion;
    }

    public String getRainOsVersion() {
        return rainOsVersion;
    }

    public void setRainOsVersion(String rainOsVersion) {
        this.rainOsVersion = rainOsVersion;
    }

    public String getRainUpgradeVersion() {
        return rainUpgradeVersion;
    }

    public void setRainUpgradeVersion(String rainUpgradeVersion) {
        this.rainUpgradeVersion = rainUpgradeVersion;
    }

    public String getHardwareVersion() {
        return hardwareVersion;
    }

    public void setHardwareVersion(String hardwareVersion) {
        this.hardwareVersion = hardwareVersion;
    }

    public CbbGetNetworkModeEnums getGetIpMode() {
        return getIpMode;
    }

    public void setGetIpMode(CbbGetNetworkModeEnums getIpMode) {
        this.getIpMode = getIpMode;
    }

    public CbbGetNetworkModeEnums getGetDnsMode() {
        return getDnsMode;
    }

    public void setGetDnsMode(CbbGetNetworkModeEnums getDnsMode) {
        this.getDnsMode = getDnsMode;
    }

    public CbbNetworkModeEnums getNetworkAccessMode() {
        return networkAccessMode;
    }

    public void setNetworkAccessMode(CbbNetworkModeEnums networkAccessMode) {
        this.networkAccessMode = networkAccessMode;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public Date getLastOnlineTime() {
        return lastOnlineTime;
    }

    public void setLastOnlineTime(Date lastOnlineTime) {
        this.lastOnlineTime = lastOnlineTime;
    }

    public Date getLastOfflineTime() {
        return lastOfflineTime;
    }

    public void setLastOfflineTime(Date lastOfflineTime) {
        this.lastOfflineTime = lastOfflineTime;
    }

    public Integer getVersion() {
        return version;
    }

    public void setVersion(Integer version) {
        this.version = version;
    }

    public CbbTerminalStateEnums getState() {
        return state;
    }

    public void setState(CbbTerminalStateEnums state) {
        this.state = state;
    }

    public UUID getGroupId() {
        return groupId;
    }

    public void setGroupId(UUID groupId) {
        this.groupId = groupId;
    }

    public String getOsInnerVersion() {
        return osInnerVersion;
    }

    public void setOsInnerVersion(String osInnerVersion) {
        this.osInnerVersion = osInnerVersion;
    }
    public CbbIDVTerminalModeEnums getIdvTerminalMode() {
        return idvTerminalMode;
    }

    public void setIdvTerminalMode(CbbIDVTerminalModeEnums idvTerminalMode) {
        this.idvTerminalMode = idvTerminalMode;
    }

    public String getSsid() {
        return ssid;
    }

    public void setSsid(String ssid) {
        this.ssid = ssid;
    }

    public CbbTerminalWirelessAuthModeEnums getWirelessAuthMode() {
        return wirelessAuthMode;
    }

    public void setWirelessAuthMode(CbbTerminalWirelessAuthModeEnums wirelessAuthMode) {
        this.wirelessAuthMode = wirelessAuthMode;
    }
}
