package com.ruijie.rcos.rcdc.terminal.module.def.api.request;

import java.util.UUID;

import com.ruijie.rcos.sk.modulekit.api.comm.IdRequest;
import org.springframework.lang.Nullable;
import com.ruijie.rcos.rcdc.terminal.module.def.api.enums.CbbSystemUpgradeStateEnums;
import com.ruijie.rcos.sk.base.annotation.NotNull;
import com.ruijie.rcos.sk.modulekit.api.comm.Request;

/**
 * 
 * Description: 获取刷机任务终端列表请求参数
 * Copyright: Copyright (c) 2018
 * Company: Ruijie Co., Ltd.
 * Create Time: 2019年3月18日
 * 
 * @author nt
 */
public class CbbGetTaskUpgradeTerminalRequest extends IdRequest {

    @Nullable
    private CbbSystemUpgradeStateEnums terminalState;

    public CbbSystemUpgradeStateEnums getTerminalState() {
        return terminalState;
    }

    public void setTerminalState(CbbSystemUpgradeStateEnums terminalState) {
        this.terminalState = terminalState;
    }

}
