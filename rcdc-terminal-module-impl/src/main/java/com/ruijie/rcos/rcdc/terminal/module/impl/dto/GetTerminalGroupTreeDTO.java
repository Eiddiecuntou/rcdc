package com.ruijie.rcos.rcdc.terminal.module.impl.dto;

import com.ruijie.rcos.rcdc.terminal.module.def.api.dto.CbbTerminalGroupTreeNodeDTO;

/**
 * 
 * Description: 终端分组树形结构响应
 * Copyright: Copyright (c) 2018
 * Company: Ruijie Co., Ltd.
 * Create Time: 2018年12月19日
 * 
 * @author nt
 */
public class GetTerminalGroupTreeDTO {
    
    /**
     * 终端分组树形结构数组
     */
    private CbbTerminalGroupTreeNodeDTO[] itemArr;
    
    public GetTerminalGroupTreeDTO() {
    }

    public GetTerminalGroupTreeDTO(CbbTerminalGroupTreeNodeDTO[] itemArr) {
        this.itemArr = itemArr;
    }

    public CbbTerminalGroupTreeNodeDTO[] getItemArr() {
        return itemArr;
    }

    public void setItemArr(CbbTerminalGroupTreeNodeDTO[] itemArr) {
        this.itemArr = itemArr;
    } 
    
}
