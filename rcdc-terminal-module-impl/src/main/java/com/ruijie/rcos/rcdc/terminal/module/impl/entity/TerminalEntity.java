package com.ruijie.rcos.rcdc.terminal.module.impl.entity;


import java.util.Date;
import java.util.List;
import java.util.UUID;

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Version;

import org.apache.commons.lang3.StringUtils;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;

import com.alibaba.fastjson.JSON;
import com.ruijie.rcos.rcdc.terminal.module.def.api.dto.CbbTerminalDiskInfoDTO;
import com.ruijie.rcos.rcdc.terminal.module.def.api.dto.CbbTerminalNetCardMacInfoDTO;
import com.ruijie.rcos.rcdc.terminal.module.def.api.dto.CbbTerminalNetworkInfoDTO;
import com.ruijie.rcos.rcdc.terminal.module.def.api.enums.CbbGetNetworkModeEnums;
import com.ruijie.rcos.rcdc.terminal.module.def.api.enums.CbbNetworkModeEnums;
import com.ruijie.rcos.rcdc.terminal.module.def.api.enums.CbbTerminalLicenseTypeEnums;
import com.ruijie.rcos.rcdc.terminal.module.def.api.enums.CbbTerminalStateEnums;
import com.ruijie.rcos.rcdc.terminal.module.def.api.enums.CbbTerminalWirelessAuthModeEnums;
import com.ruijie.rcos.rcdc.terminal.module.def.enums.CbbTerminalPlatformEnums;
import com.ruijie.rcos.rcdc.terminal.module.def.enums.CbbTerminalStartMode;
import com.ruijie.rcos.rcdc.terminal.module.impl.BusinessKey;
import com.ruijie.rcos.sk.base.exception.BusinessException;

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

    public static final String BEAN_COPY_IGNORE_NETWORK_INFO_ARR = "networkInfoArr";

    public static final String BEAN_COPY_IGNORE_DISK_INFO_ARR = "diskInfoArr";

    public static final String BEAN_COPY_IGGNORE_NET_CARD_MAC_INFO_ARR = "netCardMacInfoArr";

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

    @Enumerated(EnumType.STRING)
    private CbbTerminalLicenseTypeEnums authType;

    @Version
    private Integer version;

    @Enumerated(EnumType.STRING)
    private CbbTerminalStateEnums state;

    private UUID groupId;

    private String osInnerVersion;

    private String ssid;

    @Enumerated(EnumType.STRING)
    private CbbTerminalWirelessAuthModeEnums wirelessAuthMode;

    private String productId;

    private String networkInfos;

    private String allDiskInfo;

    private Integer wirelessNetCardNum;

    private Integer ethernetNetCardNum;

    private Boolean enableProxy;

    private String allNetCardMacInfo;

    private String supportWorkMode;

    @Enumerated(EnumType.STRING)
    private CbbTerminalStartMode startMode;

    /**
     * 获取网络信息对象数组
     *
     * @return CbbTerminalNetworkInfoDTO[]
     * @throws BusinessException 业务异常
     */
    public CbbTerminalNetworkInfoDTO[] getNetworkInfoArr() throws BusinessException {
        if (StringUtils.isBlank(networkInfos)) {
            return new CbbTerminalNetworkInfoDTO[0];
        }

        List<CbbTerminalNetworkInfoDTO> networkInfoDTOList;
        try {
            networkInfoDTOList = JSON.parseArray(networkInfos, CbbTerminalNetworkInfoDTO.class);
        } catch (Exception e) {
            throw new BusinessException(BusinessKey.RCDC_TERMINAL_NETWORK_INFO_ERROR, e);
        }

        if (CollectionUtils.isEmpty(networkInfoDTOList)) {
            return new CbbTerminalNetworkInfoDTO[0];
        }

        return networkInfoDTOList.toArray(new CbbTerminalNetworkInfoDTO[networkInfoDTOList.size()]);
    }

    /**
     * 获取磁盘信息数组
     *
     * @return CbbTerminalDiskInfoDTO[]
     * @throws BusinessException 业务异常
     */
    public CbbTerminalDiskInfoDTO[] getDiskInfoArr() throws BusinessException {
        if (StringUtils.isBlank(allDiskInfo)) {
            return new CbbTerminalDiskInfoDTO[0];
        }

        List<CbbTerminalDiskInfoDTO> diskInfoDTOList;
        try {
            diskInfoDTOList = JSON.parseArray(allDiskInfo, CbbTerminalDiskInfoDTO.class);
        } catch (Exception e) {
            throw new BusinessException(BusinessKey.RCDC_TERMINAL_DISK_INFO_ERROR, e);
        }

        if (CollectionUtils.isEmpty(diskInfoDTOList)) {
            return new CbbTerminalDiskInfoDTO[0];
        }

        return diskInfoDTOList.toArray(new CbbTerminalDiskInfoDTO[diskInfoDTOList.size()]);
    }

    /**
     * 获取终端网卡mac信息数组
     * 
     * @return CbbTerminalNetCardInfoDTO[]
     * @throws BusinessException 业务异常
     */
    public CbbTerminalNetCardMacInfoDTO[] getNetCardMacInfoArr() throws BusinessException {
        if (StringUtils.isBlank(allNetCardMacInfo)) {
            return new CbbTerminalNetCardMacInfoDTO[0];
        }

        List<CbbTerminalNetCardMacInfoDTO> netCardMacInfoDTOList;
        try {
            netCardMacInfoDTOList = JSON.parseArray(allNetCardMacInfo, CbbTerminalNetCardMacInfoDTO.class);
        } catch (Exception e) {
            throw new BusinessException(BusinessKey.RCDC_TERMINAL_NET_CARD_INFO_ERROR, e);
        }

        return netCardMacInfoDTOList.toArray(new CbbTerminalNetCardMacInfoDTO[netCardMacInfoDTOList.size()]);
    }

    /**
     * 设置终端网络信息
     * 
     * @param networkInfoDTOArr 网络信息数组
     */
    public void setNetworkInfoArr(CbbTerminalNetworkInfoDTO[] networkInfoDTOArr) {
        Assert.notNull(networkInfoDTOArr, "networkInfoDTOArr can not be null");

        setNetworkInfos(JSON.toJSONString(networkInfoDTOArr));
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

    public Long getDataDiskSize() {
        return dataDiskSize;
    }

    public void setDataDiskSize(Long dataDiskSize) {
        this.dataDiskSize = dataDiskSize;
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

    public String getProductId() {
        return productId;
    }

    public void setProductId(String productId) {
        this.productId = productId;
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

    protected String getNetworkInfos() {
        return networkInfos;
    }

    protected void setNetworkInfos(String networkInfos) {
        this.networkInfos = networkInfos;
    }

    public String getAllDiskInfo() {
        return allDiskInfo;
    }

    public void setAllDiskInfo(String allDiskInfo) {
        this.allDiskInfo = allDiskInfo;
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

    public Boolean getEnableProxy() {
        return enableProxy;
    }

    public void setEnableProxy(Boolean enableProxy) {
        this.enableProxy = enableProxy;
    }

    public String getAllNetCardMacInfo() {
        return allNetCardMacInfo;
    }

    public void setAllNetCardMacInfo(String allNetCardMacInfo) {
        this.allNetCardMacInfo = allNetCardMacInfo;
    }

    public Boolean getAuthed() {
        return authed;
    }

    public void setAuthed(Boolean authed) {
        this.authed = authed;
    }

    public String getSupportWorkMode() {
        return supportWorkMode;
    }

    public void setSupportWorkMode(String supportWorkMode) {
        this.supportWorkMode = supportWorkMode;
    }

    public CbbTerminalStartMode getStartMode() {
        return startMode;
    }

    public void setStartMode(CbbTerminalStartMode startMode) {
        this.startMode = startMode;
    }
}
