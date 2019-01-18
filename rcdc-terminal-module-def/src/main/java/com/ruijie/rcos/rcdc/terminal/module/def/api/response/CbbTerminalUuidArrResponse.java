package com.ruijie.rcos.rcdc.terminal.module.def.api.response;

import java.util.UUID;
import com.ruijie.rcos.sk.modulekit.api.comm.DefaultResponse;

/**
 * 
 * Description: 终端uuid响应对象
 * Copyright: Copyright (c) 2018
 * Company: Ruijie Co., Ltd.
 * Create Time: 2019年1月17日
 * 
 * @author nt
 */
public class CbbTerminalUuidArrResponse extends DefaultResponse {

    private UUID[] terminalUUIDArr;

    public UUID[] getTerminalUUIDArr() {
        return terminalUUIDArr;
    }

    public void setTerminalUUIDArr(UUID[] terminalUUIDArr) {
        this.terminalUUIDArr = terminalUUIDArr;
    }
    
}
