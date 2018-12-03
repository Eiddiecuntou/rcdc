package com.ruijie.rcos.rcdc.terminal.module.def.api.dto;

import com.ruijie.rcos.rcdc.terminal.module.def.api.enums.CbbSystemUpgradeStateEnums;
import com.ruijie.rcos.rcdc.terminal.module.def.api.enums.CbbTerminalTypeEnums;

/**
 * 
 * Description: 终端系统升级任务
 * Copyright: Copyright (c) 2018
 * Company: Ruijie Co., Ltd.
 * Create Time: 2018年11月27日
 * 
 * @author "nt"
 */
public class TerminalSystemUpgradeTaskDTO {

    
    /**
     * 终端id
     */
    private String terminalId;
    
    /**
     * 终端名称
     */
    private String terminalName;

    /**
     * 刷机开始时间
     */
    private Long startTime;

    /**
     * 升级状态
     */
    private CbbSystemUpgradeStateEnums state;

    /**
     * 终端类型
     */
    private CbbTerminalTypeEnums terminalType;

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

    public Long getStartTime() {
        return startTime;
    }

    public void setStartTime(Long startTime) {
        this.startTime = startTime;
    }

    public CbbSystemUpgradeStateEnums getState() {
        return state;
    }

    public void setState(CbbSystemUpgradeStateEnums state) {
        this.state = state;
    }

    public CbbTerminalTypeEnums getTerminalType() {
        return terminalType;
    }

    public void setTerminalType(CbbTerminalTypeEnums terminalType) {
        this.terminalType = terminalType;
    }

}
