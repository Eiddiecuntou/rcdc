package com.ruijie.rcos.rcdc.terminal.module.def.api.request;

import com.ruijie.rcos.rcdc.terminal.module.def.enums.TerminalTypeEnums;
import com.ruijie.rcos.sk.base.annotation.NotNull;
import com.ruijie.rcos.sk.modulekit.api.comm.Request;

/**
 * 
 * Description: 终端类型平台请求
 * Copyright: Copyright (c) 2018
 * Company: Ruijie Co., Ltd.
 * Create Time: 2019年2月21日
 * 
 * @author nt
 */
public class CbbTerminalTypeRequest implements Request {

    @NotNull
    private TerminalTypeEnums terminalType;

    public TerminalTypeEnums getTerminalType() {
        return terminalType;
    }

    public void setTerminalType(TerminalTypeEnums terminalType) {
        this.terminalType = terminalType;
    }
}
