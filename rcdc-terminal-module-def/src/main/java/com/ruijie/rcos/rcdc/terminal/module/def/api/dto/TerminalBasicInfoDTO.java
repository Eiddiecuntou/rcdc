package com.ruijie.rcos.rcdc.terminal.module.def.api.dto;

import com.ruijie.rcos.rcdc.terminal.module.def.api.enums.GetNetworkModeEnums;
import com.ruijie.rcos.rcdc.terminal.module.def.api.enums.NetworkModeEnums;

import java.util.Date;
import java.util.UUID;

/**
 * Description: Function Description
 * Copyright: Copyright (c) 2018
 * Company: Ruijie Co., Ltd.
 * Create Time: 2018/10/31
 *
 * @author Jarman
 */
public class TerminalBasicInfoDTO {

    private UUID id;
    private String name;
    private String terminalId;
    private String macAddr;
    private String ip;
    private String subnetMask;
    private String gateway;
    private String mainDns;
    private String secondDns;
    private GetNetworkModeEnums getIpMode;
    private GetNetworkModeEnums getDnsMode;
    private String productType;
    private String terminalType;
    private String serialNumber;
    private String cpuMode;
    private Integer memorySize;
    private Integer diskSize;
    private String terminalOsType;
    private String terminalOsVersion;
    private String terminalSystemVersion;
    private String softwareVersion;
    private String hardwareVersion;
    private NetworkModeEnums networkMode;
    private Date createTime;
    private Date updateTime;
    private Date onlineTime;
    private Date offlineTime;
    private String detectResult;
    private Integer version;

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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

    public GetNetworkModeEnums getGetIpMode() {
        return getIpMode;
    }

    public void setGetIpMode(GetNetworkModeEnums getIpMode) {
        this.getIpMode = getIpMode;
    }

    public GetNetworkModeEnums getGetDnsMode() {
        return getDnsMode;
    }

    public void setGetDnsMode(GetNetworkModeEnums getDnsMode) {
        this.getDnsMode = getDnsMode;
    }

    public String getProductType() {
        return productType;
    }

    public void setProductType(String productType) {
        this.productType = productType;
    }

    public String getTerminalType() {
        return terminalType;
    }

    public void setTerminalType(String terminalType) {
        this.terminalType = terminalType;
    }

    public String getSerialNumber() {
        return serialNumber;
    }

    public void setSerialNumber(String serialNumber) {
        this.serialNumber = serialNumber;
    }

    public String getCpuMode() {
        return cpuMode;
    }

    public void setCpuMode(String cpuMode) {
        this.cpuMode = cpuMode;
    }

    public Integer getMemorySize() {
        return memorySize;
    }

    public void setMemorySize(Integer memorySize) {
        this.memorySize = memorySize;
    }

    public Integer getDiskSize() {
        return diskSize;
    }

    public void setDiskSize(Integer diskSize) {
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

    public String getTerminalSystemVersion() {
        return terminalSystemVersion;
    }

    public void setTerminalSystemVersion(String terminalSystemVersion) {
        this.terminalSystemVersion = terminalSystemVersion;
    }

    public String getSoftwareVersion() {
        return softwareVersion;
    }

    public void setSoftwareVersion(String softwareVersion) {
        this.softwareVersion = softwareVersion;
    }

    public String getHardwareVersion() {
        return hardwareVersion;
    }

    public void setHardwareVersion(String hardwareVersion) {
        this.hardwareVersion = hardwareVersion;
    }

    public NetworkModeEnums getNetworkMode() {
        return networkMode;
    }

    public void setNetworkMode(NetworkModeEnums networkMode) {
        this.networkMode = networkMode;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public Date getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }

    public Date getOnlineTime() {
        return onlineTime;
    }

    public void setOnlineTime(Date onlineTime) {
        this.onlineTime = onlineTime;
    }

    public Date getOfflineTime() {
        return offlineTime;
    }

    public void setOfflineTime(Date offlineTime) {
        this.offlineTime = offlineTime;
    }

    public String getDetectResult() {
        return detectResult;
    }

    public void setDetectResult(String detectResult) {
        this.detectResult = detectResult;
    }

    public Integer getVersion() {
        return version;
    }

    public void setVersion(Integer version) {
        this.version = version;
    }
}
