package com.ruijie.rcos.rcdc.terminal.module.def.api.request;

import com.ruijie.rcos.sk.base.annotation.NotBlank;
import com.ruijie.rcos.sk.modulekit.api.comm.Request;

/**
 * 
 * Description: 移除终端系统升级任务请求参数
 * Copyright: Copyright (c) 2018
 * Company: Ruijie Co., Ltd.
 * Create Time: 2018年11月30日
 * 
 * @author nt
 */
public class CbbRemoveTerminalSystemUpgradeTaskRequest implements Request {
    
    /**
     * 终端id
     */
    @NotBlank
    private String terminalId;
    

    public String getTerminalId() {
        return terminalId;
    }

    public void setTerminalId(String terminalId) {
        this.terminalId = terminalId;
    }

}
