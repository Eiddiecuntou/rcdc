package com.ruijie.rcos.rcdc.terminal.module.def.api.response.terminal;

import com.ruijie.rcos.rcdc.terminal.module.def.api.dto.terminal.TerminalDTO;
import com.ruijie.rcos.sk.modulekit.api.comm.DefaultResponse;

/**
 * 
 * Description: 获取终端信息响应
 * Copyright: Copyright (c) 2018
 * Company: Ruijie Co., Ltd.
 * Create Time: 2018年12月18日
 * 
 * @author nt
 */
public class GetTerminalResponse extends DefaultResponse {

    /**
     * 终端信息
     */
    private TerminalDTO terminalDTO;
    
    public GetTerminalResponse() {
    }

    public GetTerminalResponse(TerminalDTO terminalDTO) {
        this.setStatus(Status.SUCCESS);
        this.terminalDTO = terminalDTO;
    }

    public TerminalDTO getTerminalDTO() {
        return terminalDTO;
    }

    public void setTerminalDTO(TerminalDTO terminalDTO) {
        this.terminalDTO = terminalDTO;
    }
    
}
