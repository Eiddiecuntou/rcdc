package com.ruijie.rcos.rcdc.terminal.module.impl.entity;

import java.util.Date;
import java.util.UUID;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Version;
import com.ruijie.rcos.rcdc.terminal.module.def.api.enums.CbbSystemUpgradeStateEnums;

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
    
    private UUID sysUpgradeId;
    
    private String terminalId;
    
    private Date startTime;
    
    private Date createTime;
    
    @Enumerated(EnumType.STRING)
    private CbbSystemUpgradeStateEnums state;

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
