package com.ruijie.rcos.rcdc.terminal.module.impl.model;

import com.ruijie.rcos.rcdc.terminal.module.def.api.enums.CbbSystemUpgradeStateEnums;
import com.ruijie.rcos.rcdc.terminal.module.def.enums.TerminalPlatformEnums;

/**
 * 
 * Description: 终端系统写入的升级文件升级信息
 * Copyright: Copyright (c) 2018
 * Company: Ruijie Co., Ltd.
 * Create Time: 2018年11月24日
 * 
 * @author nt
 */
public class TerminalSystemUpgradeInfo {
    
    /**
     * 终端id
     */
    private String terminalId;
    
    /**
     * 终端类型
     */
    private TerminalPlatformEnums platform;
    
    /**
     * 升级状态
     */
    private CbbSystemUpgradeStateEnums state;
    
    /**
     * 内部版本号
     */
    private String internalVersion;
    
    /**
     * 外部版本号
     */
    private String externalVersion;
    
    /**
     * 最后一次更新时间
     */
    private Long lastUpdateTime;

    public String getTerminalId() {
        return terminalId;
    }

    public void setTerminalId(String terminalId) {
        this.terminalId = terminalId;
    }

    public CbbSystemUpgradeStateEnums getState() {
        return state;
    }

    public void setState(CbbSystemUpgradeStateEnums state) {
        this.state = state;
    }

    public String getInternalVersion() {
        return internalVersion;
    }

    public void setInternalVersion(String internalVersion) {
        this.internalVersion = internalVersion;
    }

    public String getExternalVersion() {
        return externalVersion;
    }

    public void setExternalVersion(String externalVersion) {
        this.externalVersion = externalVersion;
    }

    public Long getLastUpdateTime() {
        return lastUpdateTime;
    }

    public void setLastUpdateTime(Long lastUpdateTime) {
        this.lastUpdateTime = lastUpdateTime;
    }

    public TerminalPlatformEnums getPlatform() {
        return platform;
    }

    public void setPlatform(TerminalPlatformEnums platform) {
        this.platform = platform;
    }

    @Override
    public String toString() {
        return "TerminalSystemUpgradeInfo [terminalId=" + terminalId + ", platform=" + platform + ", state=" + state
                + ", internalVersion=" + internalVersion + ", externalVersion=" + externalVersion + ", lastUpdateTime="
                + lastUpdateTime + "]";
    }
    
}
