package com.ruijie.rcos.rcdc.terminal.module.def.api.request.terminal;

import java.util.UUID;
import org.springframework.lang.Nullable;
import com.ruijie.rcos.rcdc.terminal.module.def.api.enums.TerminalTypeEnums;
import com.ruijie.rcos.sk.base.annotation.NotNull;
import com.ruijie.rcos.sk.modulekit.api.comm.Request;

/**
 * 
 * Description: 获取终端分组树形结构
 * Copyright: Copyright (c) 2018
 * Company: Ruijie Co., Ltd.
 * Create Time: 2018年12月19日
 * 
 * @author nt
 */
public class GetTerminalGroupTreeRequest implements Request {

    /**
     * 父级节点
     */
    @Nullable
    private UUID parentGroupId;
    
    /**
     * 终端类型
     */
    @NotNull
    private TerminalTypeEnums terminalType;
    
    public GetTerminalGroupTreeRequest() {
    }

    public GetTerminalGroupTreeRequest(UUID parentGroupId, TerminalTypeEnums terminalType) {
        this.parentGroupId = parentGroupId;
        this.terminalType = terminalType;
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
