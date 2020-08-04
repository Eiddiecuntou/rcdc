package com.ruijie.rcos.rcdc.terminal.module.def.api.request.group;

import com.ruijie.rcos.sk.base.annotation.NotBlank;
import com.ruijie.rcos.sk.base.annotation.TextShort;
import org.springframework.lang.Nullable;

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
public class CbbTerminalGroupRequest {

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

    public CbbTerminalGroupRequest() {
    }

    public CbbTerminalGroupRequest(String groupName, UUID parentGroupId) {
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
