package com.ruijie.rcos.rcdc.terminal.module.impl.entity;

import com.ruijie.rcos.rcdc.terminal.module.def.enums.CbbTerminalPlatformEnums;

import javax.persistence.*;
import java.util.UUID;

/**
 * 终端视图查询实体
 * Description: Function Description
 * Copyright: Copyright (c) 2018
 * Company: Ruijie Co., Ltd.
 * Create Time: 2018年10月30日
 * 
 * @author chenzj
 */
@Entity
@Table(name = "v_cbb_terminal_stat")
public class ViewTerminalStatEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    @Enumerated(EnumType.STRING)
    private CbbTerminalPlatformEnums platform;

    private String state;

    @Version
    private Integer version;

    private UUID groupId;

    public CbbTerminalPlatformEnums getPlatform() {
        return platform;
    }

    public void setPlatform(CbbTerminalPlatformEnums platform) {
        this.platform = platform;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public Integer getVersion() {
        return version;
    }

    public void setVersion(Integer version) {
        this.version = version;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public UUID getGroupId() {
        return groupId;
    }

    public void setGroupId(UUID groupId) {
        this.groupId = groupId;
    }
}
