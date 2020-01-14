package com.ruijie.rcos.rcdc.terminal.module.impl.entity;

import javax.persistence.*;
import java.util.Date;
import java.util.UUID;

/**
 * Description: Function Description
 * Copyright: Copyright (c) 2018
 * Company: Ruijie Co., Ltd.
 * Create Time: 2019年2月13日
 *
 * @author nt
 */
@Entity
@Table(name = "t_cbb_sys_upgrade_terminal_group")
public class TerminalSystemUpgradeTerminalGroupEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    private UUID sysUpgradeId;

    private UUID terminalGroupId;

    private Date createTime;

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

    public UUID getTerminalGroupId() {
        return terminalGroupId;
    }

    public void setTerminalGroupId(UUID terminalGroupId) {
        this.terminalGroupId = terminalGroupId;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public Integer getVersion() {
        return version;
    }

    public void setVersion(Integer version) {
        this.version = version;
    }
}
