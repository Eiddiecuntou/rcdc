package com.ruijie.rcos.rcdc.terminal.module.def.api.response.terminal;

import com.ruijie.rcos.rcdc.terminal.module.def.api.dto.terminal.RcoTerminalDTO;
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
public class GetRcoTerminalResponse extends DefaultResponse {

    private RcoTerminalDTO terminalDTO;
    
    public GetRcoTerminalResponse() {
    }

    public GetRcoTerminalResponse(RcoTerminalDTO terminalDTO) {
        this.setStatus(Status.SUCCESS);
        this.terminalDTO = terminalDTO;
    }

    public RcoTerminalDTO getTerminalDTO() {
        return terminalDTO;
    }

    public void setTerminalDTO(RcoTerminalDTO terminalDTO) {
        this.terminalDTO = terminalDTO;
    }
    
}
