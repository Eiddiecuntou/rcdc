package com.ruijie.rcos.rcdc.terminal.module.def.api.dto;

import com.ruijie.rcos.rcdc.terminal.module.def.api.enums.CbbNetworkModeEnums;
import com.ruijie.rcos.rcdc.terminal.module.def.api.enums.CbbTerminalStateEnums;

import java.util.Date;
import java.util.UUID;

/**
 * 
 * Description: 可升级终端列表展示信息
 * Copyright: Copyright (c) 2018
 * Company: Ruijie Co., Ltd.
 * Create Time: 2018年12月18日
 * 
 * @author nt
 */
public class CbbUpgradeableTerminalListDTO {

    /**
     * 终端id
     */
    private String id;

    /**
     * 终端名称
     */
    private String terminalName;

    /**
     * ip
     */
    private String ip;

    /**
     * mac
     */
    private String mac;

    /**
     * 终端状态
     */
    private CbbTerminalStateEnums terminalState;

    /**
     * 产品型号
     */
    private String productType;

    private Date lastUpgradeTime;

    private UUID groupId;

    private CbbNetworkModeEnums networkMode;

    private CbbTerminalNetworkInfoDTO[] networkInfoArr;

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

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public String getMac() {
        return mac;
    }

    public void setMac(String mac) {
        this.mac = mac;
    }

    public CbbTerminalStateEnums getTerminalState() {
        return terminalState;
    }

    public void setTerminalState(CbbTerminalStateEnums terminalState) {
        this.terminalState = terminalState;
    }

    public String getProductType() {
        return productType;
    }

    public void setProductType(String productType) {
        this.productType = productType;
    }

    public Date getLastUpgradeTime() {
        return lastUpgradeTime;
    }

    public void setLastUpgradeTime(Date lastUpgradeTime) {
        this.lastUpgradeTime = lastUpgradeTime;
    }

    public UUID getGroupId() {
        return groupId;
    }

    public void setGroupId(UUID groupId) {
        this.groupId = groupId;
    }

    public CbbNetworkModeEnums getNetworkMode() {
        return networkMode;
    }

    public void setNetworkMode(CbbNetworkModeEnums networkMode) {
        this.networkMode = networkMode;
    }

    public CbbTerminalNetworkInfoDTO[] getNetworkInfoArr() {
        return networkInfoArr;
    }

    public void setNetworkInfoArr(CbbTerminalNetworkInfoDTO[] networkInfoArr) {
        this.networkInfoArr = networkInfoArr;
    }
}
