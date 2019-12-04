package com.ruijie.rcos.rcdc.terminal.module.def.api.response.group;

import com.ruijie.rcos.rcdc.terminal.module.def.api.dto.terminal.TerminalGroupDTO;
import com.ruijie.rcos.sk.modulekit.api.comm.DefaultResponse;

/**
 * Description:
 * Copyright: Copyright (c) 2019
 * Company: Ruijie Co., Ltd.
 * Create Time: 2019/12/4 19:41
 *
 * @author conghaifeng
 */
public class CbbCreateTerminalGroupResponse extends DefaultResponse {

    private TerminalGroupDTO terminalGroupDTO;

    public CbbCreateTerminalGroupResponse(TerminalGroupDTO terminalGroupDTO) {
        this.terminalGroupDTO = terminalGroupDTO;
        this.setStatus(Status.SUCCESS);
    }

    public TerminalGroupDTO getTerminalGroupDTO() {
        return terminalGroupDTO;
    }

    public void setTerminalGroupDTO(TerminalGroupDTO terminalGroupDTO) {
        this.terminalGroupDTO = terminalGroupDTO;
    }
}
