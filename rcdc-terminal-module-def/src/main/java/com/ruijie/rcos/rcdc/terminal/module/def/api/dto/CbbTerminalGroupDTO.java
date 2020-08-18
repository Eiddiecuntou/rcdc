package com.ruijie.rcos.rcdc.terminal.module.def.api.dto;

import com.ruijie.rcos.sk.base.annotation.NotBlank;
import com.ruijie.rcos.sk.base.annotation.TextShort;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;

import java.util.UUID;

/**
 * 
 * Description: 创建终端分组请求参数
 * Copyright: Copyright (c) 2018
 * Company: Ruijie Co., Ltd.
 * Create Time: 2018年12月19日
 * 
 * @author nt
 */
public class CbbTerminalGroupDTO {

    /**
     * 终端分组名称
     */
    @NotBlank
    @TextShort
    private String groupName;
    
    /**
     * 父级分组id
     */
    @Nullable
    private UUID parentGroupId;

    public CbbTerminalGroupDTO() {
    }

    public CbbTerminalGroupDTO(String groupName, @Nullable UUID parentGroupId) {
        Assert.hasText(groupName, "groupName can not be blank");

        this.groupName = groupName;
        this.parentGroupId = parentGroupId;
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
