package com.ruijie.rcos.rcdc.terminal.module.def.api.response;

import com.ruijie.rcos.sk.modulekit.api.comm.DefaultResponse;

/**
 * 
 * Description: 终端id响应对象
 * Copyright: Copyright (c) 2018
 * Company: Ruijie Co., Ltd.
 * Create Time: 2019年1月17日
 * 
 * @author nt
 */
public class CbbTerminalIdResponse extends DefaultResponse {
    
    private String terminalId;

    public String getTerminalId() {
        return terminalId;
    }

    public void setTerminalId(String terminalId) {
        this.terminalId = terminalId;
    }

}
