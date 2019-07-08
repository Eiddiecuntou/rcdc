package com.ruijie.rcos.rcdc.terminal.module.def.api.request.terminal;

import java.util.UUID;
import org.springframework.lang.Nullable;
import com.ruijie.rcos.rcdc.terminal.module.def.api.enums.TerminalTypeEnums;
import com.ruijie.rcos.sk.base.annotation.NotBlank;
import com.ruijie.rcos.sk.base.annotation.NotNull;
import com.ruijie.rcos.sk.base.annotation.TextShort;
import com.ruijie.rcos.sk.modulekit.api.comm.Request;

/**
 * 
 * Description: 创建终端分组请求参数
 * Copyright: Copyright (c) 2018
 * Company: Ruijie Co., Ltd.
 * Create Time: 2018年12月19日
 * 
 * @author nt
 */
public class CreateTerminalGroupRequest implements Request {

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
    
    /**
     * 终端类型
     */
    @NotNull
    private TerminalTypeEnums terminalType = TerminalTypeEnums.VDI;
    
    

    public CreateTerminalGroupRequest() {
    }

    public CreateTerminalGroupRequest(String groupName, UUID parentGroupId) {
        this.groupName = groupName;
        this.parentGroupId = parentGroupId;
    }

    public CreateTerminalGroupRequest(String groupName, UUID parentGroupId, TerminalTypeEnums terminalType) {
        this.groupName = groupName;
        this.parentGroupId = parentGroupId;
        this.terminalType = terminalType;
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

    public TerminalTypeEnums getTerminalType() {
        return terminalType;
    }

    public void setTerminalType(TerminalTypeEnums terminalType) {
        this.terminalType = terminalType;
    }
    
}
