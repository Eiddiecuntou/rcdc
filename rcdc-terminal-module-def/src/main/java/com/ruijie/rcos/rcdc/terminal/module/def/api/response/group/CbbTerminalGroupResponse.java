package com.ruijie.rcos.rcdc.terminal.module.def.api.response.group;

import com.ruijie.rcos.rcdc.terminal.module.def.api.dto.terminal.TerminalGroupDTO;
import com.ruijie.rcos.sk.modulekit.api.comm.DefaultResponse;

/**
 *  
 * Description: 终端分组dto响应
 * Copyright: Copyright (c) 2018
 * Company: Ruijie Co., Ltd.
 * Create Time: 2018年10月30日
 * 
 * @author chenzj
 */
public class CbbTerminalGroupResponse extends DefaultResponse {
    
    /**
     * 终端分组dto
     */
    private TerminalGroupDTO terminalGroupDTO;
    
    public CbbTerminalGroupResponse() {
    }

    public CbbTerminalGroupResponse(TerminalGroupDTO terminalGroupDTO) {
        this.setStatus(Status.SUCCESS);
        this.terminalGroupDTO = terminalGroupDTO;
    }

    public TerminalGroupDTO getTerminalGroupDTO() {
        return terminalGroupDTO;
    }

    public void setTerminalGroupDTO(TerminalGroupDTO terminalGroupDTO) {
        this.terminalGroupDTO = terminalGroupDTO;
    }
    
    
}
