package com.ruijie.rcos.rcdc.terminal.module.def.api.dto.terminal;

import java.util.UUID;
import com.ruijie.rcos.rcdc.terminal.module.def.api.enums.TerminalTypeEnums;

/**
 * 终端组DTO对象
 * Description: Function Description
 * Copyright: Copyright (c) 2018
 * Company: Ruijie Co., Ltd.
 * Create Time: 2018年10月30日
 * 
 * @author chenzj
 */
public class TerminalGroupDTO {
    
    /**
     * 终端分组id
     */
    private UUID id;

    /**
     * 终端分组名称
     */
    private String groupName;

    /**
     *父级分组id
     */
    private UUID parentGroupId;
    
    /**
     * 父级分组名称
     */
    private String parentGroupName;
    
    /**
     * 终端类型
     */
    private TerminalTypeEnums terminalType = TerminalTypeEnums.VDI;
    

    public TerminalGroupDTO() {
    }

    public TerminalGroupDTO(UUID id, String groupName, UUID parentGroupId) {
        this.id = id;
        this.groupName = groupName;
        this.parentGroupId = parentGroupId;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
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

    public String getParentGroupName() {
        return parentGroupName;
    }

    public void setParentGroupName(String parentGroupName) {
        this.parentGroupName = parentGroupName;
    }

    public TerminalTypeEnums getTerminalType() {
        return terminalType;
    }

    public void setTerminalType(TerminalTypeEnums terminalType) {
        this.terminalType = terminalType;
    }


}
