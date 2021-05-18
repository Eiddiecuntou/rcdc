package com.ruijie.rcos.rcdc.terminal.module.def.api.dto;

import java.util.Date;
import java.util.UUID;

import com.ruijie.rcos.rcdc.terminal.module.def.api.enums.CbbNetworkModeEnums;
import com.ruijie.rcos.rcdc.terminal.module.def.api.enums.CbbSystemUpgradeStateEnums;
import com.ruijie.rcos.rcdc.terminal.module.def.enums.CbbTerminalPlatformEnums;

/**
 * 
 * Description: 系统刷机任务终端
 * Copyright: Copyright (c) 2018
 * Company: Ruijie Co., Ltd.
 * Create Time: 2018年11月27日
 * 
 * @author nt
 */
public class CbbSystemUpgradeTaskTerminalDTO {

    private UUID id;

    private String terminalId;

    private String terminalName;

    private String ip;

    private String mac;

    private Date startTime;

    private CbbSystemUpgradeStateEnums terminalUpgradeState;

    private CbbNetworkModeEnums networkMode;

    private CbbTerminalNetworkInfoDTO[] networkInfoArr;

    private CbbTerminalPlatformEnums platform;

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

    public Date getStartTime() {
        return startTime;
    }

    public void setStartTime(Date startTime) {
        this.startTime = startTime;
    }

    public CbbSystemUpgradeStateEnums getTerminalUpgradeState() {
        return terminalUpgradeState;
    }

    public void setTerminalUpgradeState(CbbSystemUpgradeStateEnums terminalUpgradeState) {
        this.terminalUpgradeState = terminalUpgradeState;
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

    public CbbTerminalPlatformEnums getPlatform() {
        return platform;
    }

    public void setPlatform(CbbTerminalPlatformEnums platform) {
        this.platform = platform;
    }
}
