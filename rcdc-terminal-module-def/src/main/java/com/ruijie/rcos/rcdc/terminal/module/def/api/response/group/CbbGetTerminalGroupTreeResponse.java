package com.ruijie.rcos.rcdc.terminal.module.def.api.response.group;

import com.ruijie.rcos.rcdc.terminal.module.def.api.dto.terminal.TerminalGroupTreeNodeDTO;
import com.ruijie.rcos.sk.modulekit.api.comm.DefaultResponse;

/**
 * 
 * Description: 终端分组树形结构响应
 * Copyright: Copyright (c) 2018
 * Company: Ruijie Co., Ltd.
 * Create Time: 2018年12月19日
 * 
 * @author nt
 */
public class CbbGetTerminalGroupTreeResponse extends DefaultResponse {
    
    /**
     * 终端分组树形结构数组
     */
    private TerminalGroupTreeNodeDTO[] itemArr;
    
    public CbbGetTerminalGroupTreeResponse() {
    }

    public CbbGetTerminalGroupTreeResponse(TerminalGroupTreeNodeDTO[] itemArr) {
        this.setStatus(Status.SUCCESS);
        this.itemArr = itemArr;
    }

    public TerminalGroupTreeNodeDTO[] getItemArr() {
        return itemArr;
    }

    public void setItemArr(TerminalGroupTreeNodeDTO[] itemArr) {
        this.itemArr = itemArr;
    } 
    
}
