package com.ruijie.rcos.rcdc.terminal.module.impl.entity;

import com.ruijie.rcos.rcdc.terminal.module.def.api.enums.CbbSystemUpgradeStateEnums;

import javax.persistence.*;
import java.util.Date;
import java.util.UUID;

/**
 * 
 * Description: Function Description
 * Copyright: Copyright (c) 2018
 * Company: Ruijie Co., Ltd.
 * Create Time: 2019年2月13日
 * 
 * @author nt
 */
@Entity
@Table(name = "t_cbb_sys_upgrade_terminal")
public class TerminalSystemUpgradeTerminalEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    /** 
     * 刷机任务id 
    **/
    private UUID sysUpgradeId;

    /** 
     * 刷机终端id 
    **/
    private String terminalId;

    /** 
     * 开始刷机时间 
    **/
    private Date startTime;

    /** 
     * 创建时间 
    **/
    private Date createTime;

    /** 
     * 刷机状态 
    **/
    @Enumerated(EnumType.STRING)
    private CbbSystemUpgradeStateEnums state;

    /** 
     * 版本号，实现乐观锁 
    **/
    @Version
    private Integer version;

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public UUID getSysUpgradeId() {
        return sysUpgradeId;
    }

    public void setSysUpgradeId(UUID sysUpgradeId) {
        this.sysUpgradeId = sysUpgradeId;
    }

    public String getTerminalId() {
        return terminalId;
    }

    public void setTerminalId(String terminalId) {
        this.terminalId = terminalId;
    }

    public Date getStartTime() {
        return startTime;
    }

    public void setStartTime(Date startTime) {
        this.startTime = startTime;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public CbbSystemUpgradeStateEnums getState() {
        return state;
    }

    public void setState(CbbSystemUpgradeStateEnums state) {
        this.state = state;
    }

    public Integer getVersion() {
        return version;
    }

    public void setVersion(Integer version) {
        this.version = version;
    }

}
