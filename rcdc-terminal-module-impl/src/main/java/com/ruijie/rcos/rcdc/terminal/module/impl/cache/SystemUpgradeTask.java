package com.ruijie.rcos.rcdc.terminal.module.impl.cache;

import com.ruijie.rcos.rcdc.terminal.module.def.api.enums.CbbSystemUpgradeStateEnums;
import com.ruijie.rcos.rcdc.terminal.module.def.enums.TerminalPlatformEnums;

/**
 * 
 * Description: 终端系统升级任务
 * Copyright: Copyright (c) 2018
 * Company: Ruijie Co., Ltd.
 * Create Time: 2018年11月21日
 * 
 * @author nt
 */
public class SystemUpgradeTask {


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
    private TerminalPlatformEnums platform;

    /**
     * 心跳时间
     */
    private Long timeStamp;

    /**
     * 是否发送升级指令
     */
    private Boolean isSend;
    

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

    public TerminalPlatformEnums getPlatform() {
        return platform;
    }

    public void setPlatform(TerminalPlatformEnums platform) {
        this.platform = platform;
    }

    public Boolean getIsSend() {
        return isSend;
    }

    public void setIsSend(Boolean isSend) {
        this.isSend = isSend;
    }

    public Long getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(Long timeStamp) {
        this.timeStamp = timeStamp;
    }

    @Override
    public String toString() {
        return "SystemUpgradeTask [terminalId=" + terminalId + ", terminalName=" + terminalName + ", startTime="
                + startTime + ", state=" + state + ", platform=" + platform + ", timeStamp=" + timeStamp + ", isSend="
                + isSend + "]";
    }

}
