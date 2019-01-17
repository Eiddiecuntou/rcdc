package com.ruijie.rcos.rcdc.terminal.module.def.api.request;

import java.util.UUID;
import com.ruijie.rcos.sk.base.annotation.NotNull;
import com.ruijie.rcos.sk.modulekit.api.comm.Request;

/**
 * Description: 终端检测请求参数对象
 * Copyright: Copyright (c) 2018
 * Company: Ruijie Co., Ltd.
 * Create Time: 2018/11/1
 *
 * @author Jarman
 */
public class CbbTerminalDetectRequest implements Request {
   
    @NotNull
    private UUID cbbTerminalId;
    
    public CbbTerminalDetectRequest(UUID cbbTerminalId) {
        this.cbbTerminalId = cbbTerminalId;
    }

    public UUID getCbbTerminalId() {
        return cbbTerminalId;
    }

    public void setCbbTerminalId(UUID cbbTerminalId) {
        this.cbbTerminalId = cbbTerminalId;
    }

}