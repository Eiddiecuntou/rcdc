package com.ruijie.rcos.rcdc.terminal.module.def.api.request.group;

import java.util.UUID;

import com.ruijie.rcos.sk.base.annotation.NotNull;
import org.springframework.lang.Nullable;
import com.ruijie.rcos.sk.base.annotation.TextShort;
import com.ruijie.rcos.sk.modulekit.api.comm.Request;

/**
 * 
 * Description: 检测终端名称是否同级唯一
 * Copyright: Copyright (c) 2018
 * Company: Ruijie Co., Ltd.
 * Create Time: 2018年12月19日
 * 
 * @author nt
 */
public class CbbGetSubGroupByNameRequest implements Request {
    
    /**
     * 终端分组名称
     */
    @NotNull
    @TextShort
    private String groupName;
    
    /**
     * 父级分组id
     */
    @Nullable
    private UUID parentGroupId;
    

    public CbbGetSubGroupByNameRequest() {
    }

    public CbbGetSubGroupByNameRequest(String groupName, UUID parentGroupId) {
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
