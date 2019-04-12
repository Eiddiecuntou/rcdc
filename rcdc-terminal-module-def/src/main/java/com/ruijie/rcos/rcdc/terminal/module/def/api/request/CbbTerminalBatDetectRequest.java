package com.ruijie.rcos.rcdc.terminal.module.def.api.request;

import com.ruijie.rcos.sk.base.annotation.NotEmpty;
import com.ruijie.rcos.sk.modulekit.api.comm.Request;

/**
 * Description: 终端批量检测请求参数对象
 * Copyright: Copyright (c) 2018
 * Company: Ruijie Co., Ltd.
 * Create Time: 2018/11/1
 *
 * @author Jarman
 */
public class CbbTerminalBatDetectRequest implements Request {

    @NotEmpty
    private String[] terminalIdArr;

    public CbbTerminalBatDetectRequest() {
        
    }

    public CbbTerminalBatDetectRequest(String[] terminalIdArr) {
        this.terminalIdArr = terminalIdArr;
    }

    public String[] getTerminalIdArr() {
        return terminalIdArr;
    }

    public void setTerminalIdArr(String[] terminalIdArr) {
        this.terminalIdArr = terminalIdArr;
    }

}
