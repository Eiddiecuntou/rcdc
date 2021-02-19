package com.ruijie.rcos.rcdc.terminal.module.def.api.dto;

import com.ruijie.rcos.rcdc.terminal.module.def.api.enums.CbbGetNetworkModeEnums;
import com.ruijie.rcos.rcdc.terminal.module.def.api.enums.CbbNetworkModeEnums;
import com.ruijie.rcos.rcdc.terminal.module.def.api.enums.CbbTerminalStateEnums;
import com.ruijie.rcos.rcdc.terminal.module.def.api.enums.CbbTerminalWorkModeEnums;
import com.ruijie.rcos.rcdc.terminal.module.def.enums.CbbTerminalPlatformEnums;
import com.ruijie.rcos.rcdc.terminal.module.def.enums.CbbTerminalStartMode;

import java.util.Date;
import java.util.UUID;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;

/**
 * Description: Function Description
 * Copyright: Copyright (c) 2018
 * Company: Ruijie Co., Ltd.
 * Create Time: 2018/10/31
 *
 * @author Jarman
 */
public class CbbTerminalBasicInfoDTO {

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
    private CbbTerminalPlatformEnums terminalPlatform;

    private String serialNumber;

    private String cpuType;

    private Long memorySize;

    private Long diskSize;

    private Long dataDiskSize;

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

    private Boolean authed;

    private Integer version;

    @Enumerated(EnumType.STRING)
    private CbbTerminalStateEnums state;

    private CbbTerminalNetworkInfoDTO[] networkInfoArr;

    private CbbTerminalDiskInfoDTO[] diskInfoArr;

    private CbbTerminalNetCardMacInfoDTO[] netCardMacInfoArr;

    private Integer wirelessNetCardNum;

    private Integer ethernetNetCardNum;

    private CbbTerminalWorkModeEnums[] supportWorkModeArr;

    private CbbTerminalStartMode startMode;

    private Boolean supportTcStart;

    public Long getDataDiskSize() {
        return dataDiskSize;
    }

    public void setDataDiskSize(Long dataDiskSize) {
        this.dataDiskSize = dataDiskSize;
    }

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

    public String getProductType() {
        return productType;
    }

    public void setProductType(String productType) {
        this.productType = productType;
    }

    public CbbTerminalPlatformEnums getTerminalPlatform() {
        return terminalPlatform;
    }

    public void setTerminalPlatform(CbbTerminalPlatformEnums terminalPlatform) {
        this.terminalPlatform = terminalPlatform;
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

    public String getTerminalIdForOptLog() {
        return getMacAddr();
    }

    public CbbTerminalNetworkInfoDTO[] getNetworkInfoArr() {
        return networkInfoArr;
    }

    public void setNetworkInfoArr(CbbTerminalNetworkInfoDTO[] networkInfoArr) {
        this.networkInfoArr = networkInfoArr;
    }

    public CbbTerminalDiskInfoDTO[] getDiskInfoArr() {
        return diskInfoArr;
    }

    public void setDiskInfoArr(CbbTerminalDiskInfoDTO[] diskInfoArr) {
        this.diskInfoArr = diskInfoArr;
    }

    public Integer getWirelessNetCardNum() {
        return wirelessNetCardNum;
    }

    public void setWirelessNetCardNum(Integer wirelessNetCardNum) {
        this.wirelessNetCardNum = wirelessNetCardNum;
    }

    public Integer getEthernetNetCardNum() {
        return ethernetNetCardNum;
    }

    public void setEthernetNetCardNum(Integer ethernetNetCardNum) {
        this.ethernetNetCardNum = ethernetNetCardNum;
    }

    public CbbTerminalNetCardMacInfoDTO[] getNetCardMacInfoArr() {
        return netCardMacInfoArr;
    }

    public void setNetCardMacInfoArr(CbbTerminalNetCardMacInfoDTO[] netCardMacInfoArr) {
        this.netCardMacInfoArr = netCardMacInfoArr;
    }

    public Boolean getAuthed() {
        return authed;
    }

    public void setAuthed(Boolean authed) {
        this.authed = authed;
    }

    public CbbTerminalWorkModeEnums[] getSupportWorkModeArr() {
        return supportWorkModeArr;
    }

    public void setSupportWorkModeArr(CbbTerminalWorkModeEnums[] supportWorkModeArr) {
        this.supportWorkModeArr = supportWorkModeArr;
    }

    public CbbTerminalStartMode getStartMode() {
        return startMode;
    }

    public void setStartMode(CbbTerminalStartMode startMode) {
        this.startMode = startMode;
    }

    public Boolean getSupportTcStart() {
        return supportTcStart;
    }

    public void setSupportTcStart(Boolean supportTcStart) {
        this.supportTcStart = supportTcStart;
    }
}
