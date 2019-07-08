package com.ruijie.rcos.rcdc.terminal.module.def.api.request.terminal;

import com.ruijie.rcos.sk.base.annotation.NotBlank;
import com.ruijie.rcos.sk.modulekit.api.comm.Request;

/**
 * 
 * Description: 创建终端请求参数
 * Copyright: Copyright (c) 2018
 * Company: Ruijie Co., Ltd.
 * Create Time: 2019年1月2日
 * 
 * @author nt
 */
public class CreateTerminalRequest implements Request {
    
    @NotBlank
    private String terminalId;

    public String getTerminalId() {
        return terminalId;
    }

    public void setTerminalId(String terminalId) {
        this.terminalId = terminalId;
    }

}
