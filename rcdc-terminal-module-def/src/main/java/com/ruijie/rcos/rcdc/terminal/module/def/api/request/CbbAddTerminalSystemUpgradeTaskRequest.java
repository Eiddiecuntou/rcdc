package com.ruijie.rcos.rcdc.terminal.module.def.api.request;

import javax.validation.constraints.NotNull;
import com.ruijie.rcos.rcdc.terminal.module.def.api.enums.CbbTerminalTypeEnums;
import com.ruijie.rcos.sk.modulekit.api.comm.Request;

/**
 * 
 * Description: 添加终端系统升级任务请求参数
 * Copyright: Copyright (c) 2018
 * Company: Ruijie Co., Ltd.
 * Create Time: 2018年11月30日
 * 
 * @author nt
 */
public class CbbAddTerminalSystemUpgradeTaskRequest implements Request {
    
    /**
     * 终端id
     */
    @NotNull
    private String terminalId;

    /**
     * 终端类型
     */
    @NotNull
    private CbbTerminalTypeEnums terminalType;
    

    public String getTerminalId() {
        return terminalId;
    }

    public void setTerminalId(String terminalId) {
        this.terminalId = terminalId;
    }

    public CbbTerminalTypeEnums getTerminalType() {
        return terminalType;
    }

    public void setTerminalType(CbbTerminalTypeEnums terminalType) {
        this.terminalType = terminalType;
    }

}
