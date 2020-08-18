package com.ruijie.rcos.rcdc.terminal.module.def.api.dto;

import com.ruijie.rcos.sk.base.annotation.NotNull;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;

import java.util.UUID;

/**
 * 
 * Description: 获取终端分组树形结构
 * Copyright: Copyright (c) 2018
 * Company: Ruijie Co., Ltd.
 * Create Time: 2018年12月19日
 * 
 * @author nt
 */
public class CbbGetTerminalGroupCompleteTreeDTO {

    @NotNull
    private Boolean enableFilterDefaultGroup;

    @Nullable
    private UUID filterGroupId;
    
    public CbbGetTerminalGroupCompleteTreeDTO() {
    }

    public CbbGetTerminalGroupCompleteTreeDTO(Boolean enableFilterDefaultGroup, UUID filterGroupId) {
        Assert.notNull(enableFilterDefaultGroup, "enableFilterDefaultGroup can not be null");
        Assert.notNull(filterGroupId, "filterGroupId can not be null");

        this.enableFilterDefaultGroup = enableFilterDefaultGroup;
        this.filterGroupId = filterGroupId;
    }

    public Boolean getEnableFilterDefaultGroup() {
        return enableFilterDefaultGroup;
    }

    public void setEnableFilterDefaultGroup(Boolean enableFilterDefaultGroup) {
        this.enableFilterDefaultGroup = enableFilterDefaultGroup;
    }

    public UUID getFilterGroupId() {
        return filterGroupId;
    }

    public void setFilterGroupId(UUID filterGroupId) {
        this.filterGroupId = filterGroupId;
    }
    
}
