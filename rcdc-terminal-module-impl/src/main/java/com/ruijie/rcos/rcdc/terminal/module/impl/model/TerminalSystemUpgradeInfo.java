package com.ruijie.rcos.rcdc.terminal.module.impl.model;

import com.ruijie.rcos.rcdc.terminal.module.def.api.enums.CbbSystemUpgradeStateEnums;

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
     * 升级状态
     */
    private CbbSystemUpgradeStateEnums state;

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

    @Override
    public String toString() {
        return "TerminalSystemUpgradeInfo [terminalId=" + terminalId + ", state=" + state + "]";
    }

}
