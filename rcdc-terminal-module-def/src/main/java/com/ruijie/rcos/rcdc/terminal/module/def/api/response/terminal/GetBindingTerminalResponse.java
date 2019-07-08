package com.ruijie.rcos.rcdc.terminal.module.def.api.response.terminal;

import com.ruijie.rcos.sk.modulekit.api.comm.DefaultResponse;

/**
 * 
 * Description: Function Description
 * Copyright: Copyright (c) 2018
 * Company: Ruijie Co., Ltd.
 * Create Time: 2019年1月2日
 * 
 * @author nt
 */
public class GetBindingTerminalResponse extends DefaultResponse {

    private String terminalId;

    
    public GetBindingTerminalResponse() {
    }

    public GetBindingTerminalResponse(String terminalId) {
        this.terminalId = terminalId;
    }

    public String getTerminalId() {
        return terminalId;
    }

    public void setTerminalId(String terminalId) {
        this.terminalId = terminalId;
    }
    
}
