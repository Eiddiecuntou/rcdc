package com.ruijie.rcos.rcdc.terminal.module.def.api.request.terminal;

import java.util.UUID;
import org.springframework.lang.Nullable;
import com.ruijie.rcos.sk.base.annotation.NotBlank;
import com.ruijie.rcos.sk.base.annotation.NotNull;
import com.ruijie.rcos.sk.base.annotation.TextShort;
import com.ruijie.rcos.sk.modulekit.api.comm.Request;

/**
 * 
 * Description: 编辑终端组请求参数类
 * Copyright: Copyright (c) 2018
 * Company: Ruijie Co., Ltd.
 * Create Time: 2018年10月30日
 * 
 * @author chenzj
 */
public class EditTerminalGroupRequest implements Request {

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
    

    public EditTerminalGroupRequest() {
    }

    public EditTerminalGroupRequest(UUID id, String groupName, UUID parentGroupId) {
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
