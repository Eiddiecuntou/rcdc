package com.ruijie.rcos.rcdc.terminal.module.def.api.dto;

import com.alibaba.fastjson.annotation.JSONField;
import com.ruijie.rcos.rcdc.terminal.module.def.api.enums.*;
import com.ruijie.rcos.rcdc.terminal.module.def.enums.CbbTerminalPlatformEnums;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import org.springframework.lang.Nullable;

/**
 * Description: shine上传的终端基本信息
 * Copyright: Copyright (c) 2018
 * Company: Ruijie Co., Ltd.
 * Create Time: 2018/11/1
 *
 * @author Jarman
 */
public class CbbShineTerminalBasicInfo {

    @Nullable
    private String terminalName;

    @Nullable
    private String terminalId;

    @Nullable
    private String macAddr;

    @Nullable
    private String ip;

    @Nullable
    private String subnetMask;

    @Nullable
    private String gateway;

    @Nullable
    private String mainDns;

    @Nullable
    private String secondDns;

    @Nullable
    @Enumerated(EnumType.STRING)
    private CbbGetNetworkModeEnums getIpMode;

    @Nullable
    @Enumerated(EnumType.STRING)
    private CbbGetNetworkModeEnums getDnsMode;

    @Nullable
    private String productType;

    @Nullable
    @Enumerated(EnumType.STRING)
    private CbbNetworkModeEnums networkAccessMode;

    @Nullable
    private String serialNumber;

    @Nullable
    private String cpuType;

    @Nullable
    private Long memorySize;

    @Nullable
    private Long diskSize;
    
    @Nullable
    private Long dataDiskSize;

    @Nullable
    private String terminalOsType;

    @Nullable
    private String terminalOsVersion;

    @Nullable
    private String rainOsVersion;

    @Nullable
    private String rainUpgradeVersion;

    @Nullable
    private String hardwareVersion;

    @Nullable
    @Enumerated(EnumType.STRING)
    private CbbTerminalPlatformEnums platform;

    @Nullable
    private String validateMd5;

    @Nullable
    private String osInnerVersion;

    @Nullable
    private String idvTerminalMode;

    @Nullable
    private String ssid;

    @Nullable
    @JSONField(serializeUsing = CbbTerminalWirelessAuthModeEnumsSerializer.class, deserializeUsing = CbbTerminalWirelessAuthModeEnumsSerializer.class)
    private CbbTerminalWirelessAuthModeEnums wirelessAuthMode;

    @Nullable
    private CbbTerminalNetworkInfoDTO[] networkInfoArr;

    @Nullable
    private String productId;

    @Nullable
    private String allDiskInfo;

    @Nullable
    private Integer wirelessNetCardNum;

    @Nullable
    private Integer ethernetNetCardNum;

    @Nullable
    private Boolean enableProxy;

    @Nullable
    private String allNetCardMacInfo;

    @Nullable
    private Boolean supportTcStart;

    @Nullable
    private CbbTerminalWorkModeEnums[] terminalWorkSupportModeArr;

    public CbbTerminalPlatformEnums getPlatform() {
        return platform;
    }

    public void setPlatform(CbbTerminalPlatformEnums platform) {
        this.platform = platform;
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

    public CbbNetworkModeEnums getNetworkAccessMode() {
        return networkAccessMode;
    }

    public void setNetworkAccessMode(CbbNetworkModeEnums networkAccessMode) {
        this.networkAccessMode = networkAccessMode;
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

    public String getValidateMd5() {
        return validateMd5;
    }

    public void setValidateMd5(String validateMd5) {
        this.validateMd5 = validateMd5;
    }

    public String getOsInnerVersion() {
        return osInnerVersion;
    }

    public void setOsInnerVersion(String osInnerVersion) {
        this.osInnerVersion = osInnerVersion;
    }

    public String getIdvTerminalMode() {
        return idvTerminalMode;
    }

    public void setIdvTerminalMode(String idvTerminalMode) {
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

    public CbbTerminalNetworkInfoDTO[] getNetworkInfoArr() {
        return networkInfoArr;
    }

    public void setNetworkInfoArr(CbbTerminalNetworkInfoDTO[] networkInfoArr) {
        this.networkInfoArr = networkInfoArr;
    }

    public String getProductId() {
        return productId;
    }

    public void setProductId(String productId) {
        this.productId = productId;
    }

    @Nullable
    public String getAllDiskInfo() {
        return allDiskInfo;
    }

    public void setAllDiskInfo(@Nullable String allDiskInfo) {
        this.allDiskInfo = allDiskInfo;
    }

    @Nullable
    public Integer getWirelessNetCardNum() {
        return wirelessNetCardNum;
    }

    public void setWirelessNetCardNum(@Nullable Integer wirelessNetCardNum) {
        this.wirelessNetCardNum = wirelessNetCardNum;
    }

    @Nullable
    public Integer getEthernetNetCardNum() {
        return ethernetNetCardNum;
    }

    public void setEthernetNetCardNum(@Nullable Integer ethernetNetCardNum) {
        this.ethernetNetCardNum = ethernetNetCardNum;
    }

    @Nullable
    public Boolean getEnableProxy() {
        return enableProxy;
    }

    public void setEnableProxy(@Nullable Boolean enableProxy) {
        this.enableProxy = enableProxy;
    }

    @Nullable
    public CbbTerminalWorkModeEnums[] getTerminalWorkSupportModeArr() {
        return terminalWorkSupportModeArr;
    }

    public void setTerminalWorkSupportModeArr(@Nullable CbbTerminalWorkModeEnums[] terminalWorkSupportModeArr) {
        this.terminalWorkSupportModeArr = terminalWorkSupportModeArr;
    }

    @Nullable
    public String getAllNetCardMacInfo() {
        return allNetCardMacInfo;
    }

    public void setAllNetCardMacInfo(@Nullable String allNetCardMacInfo) {
        this.allNetCardMacInfo = allNetCardMacInfo;
    }

    public Long getDataDiskSize() {
        return dataDiskSize;
    }

    public void setDataDiskSize(Long dataDiskSize) {
        this.dataDiskSize = dataDiskSize;
    }

    @Nullable
    public Boolean getSupportTcStart() {
        return supportTcStart;
    }

    public void setSupportTcStart(@Nullable Boolean supportTcStart) {
        this.supportTcStart = supportTcStart;
    }
}
