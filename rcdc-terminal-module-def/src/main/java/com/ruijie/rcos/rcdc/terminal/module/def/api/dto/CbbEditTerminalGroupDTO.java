package com.ruijie.rcos.rcdc.terminal.module.def.api.dto;

import com.ruijie.rcos.sk.base.annotation.NotBlank;
import com.ruijie.rcos.sk.base.annotation.NotNull;
import com.ruijie.rcos.sk.base.annotation.TextShort;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;

import java.util.UUID;

/**
 * 
 * Description: 编辑终端组请求参数类
 * Copyright: Copyright (c) 2018
 * Company: Ruijie Co., Ltd.
 * Create Time: 2018年10月30日
 * 
 * @author chenzj
 */
public class CbbEditTerminalGroupDTO {

    /**
     * 终端分组id
     */
    @NotNull
    private UUID id;
    
    /**
     * 分组名称
     */
    @NotBlank
    @TextShort
    private String groupName;
    
    /**
     * 父级分组id
     */
    @Nullable
    private UUID parentGroupId;
    

    public CbbEditTerminalGroupDTO() {
    }

    public CbbEditTerminalGroupDTO(UUID id, String groupName, @Nullable UUID parentGroupId) {
        Assert.notNull(id, "id can not be null");
        Assert.hasText(groupName, "groupName can not be blank");

        this.id = id;
        this.groupName = groupName;
        this.parentGroupId = parentGroupId;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID uuid) {
        this.id = uuid;
    }

    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    public UUID getParentGroupId() {
        return parentGroupId;
    }

    public void setParentGroupId(UUID parentGroupId) {
        this.parentGroupId = parentGroupId;
    }
    
}
