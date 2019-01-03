package com.ruijie.rcos.rcdc.terminal.module.def.api.request;

import com.ruijie.rcos.rcdc.terminal.module.def.api.enums.CbbTerminalTypeEnums;
import com.ruijie.rcos.sk.base.annotation.NotBlank;
import com.ruijie.rcos.sk.base.annotation.NotNull;
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
    @NotBlank
    private String terminalId;

    /**
     * 终端类型
     */
    @NotNull
    private CbbTerminalTypeEnums terminalType;
    

    public CbbAddTerminalSystemUpgradeTaskRequest() {
    }

    public CbbAddTerminalSystemUpgradeTaskRequest(String terminalId, CbbTerminalTypeEnums terminalType) {
        this.terminalId = terminalId;
        this.terminalType = terminalType;
    }

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
