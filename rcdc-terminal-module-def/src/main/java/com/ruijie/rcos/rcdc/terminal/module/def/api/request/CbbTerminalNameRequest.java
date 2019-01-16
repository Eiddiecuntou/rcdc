
package com.ruijie.rcos.rcdc.terminal.module.def.api.request;

import com.ruijie.rcos.sk.modulekit.api.comm.Request;

import com.ruijie.rcos.sk.base.annotation.NotNull;

/**
 * Description: 封装terminalName请求参数对象
 * Copyright: Copyright (c) 2018
 * Company: Ruijie Co., Ltd.
 * Create Time: 2018/11/1
 *
 * @author Jarman
 */
public class CbbTerminalNameRequest implements Request {

    @NotNull
    private String terminalId;

    @NotNull
    private String terminalName;

    public String getTerminalName() {
        return terminalName;
    }

    public String getTerminalId() {
        return terminalId;
    }

    public void setTerminalId(String terminalId) {
        this.terminalId = terminalId;
    }

    public void setTerminalName(String terminalName) {
        this.terminalName = terminalName;
    }
}
