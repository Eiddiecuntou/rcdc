package com.ruijie.rcos.rcdc.terminal.module.def.api.request;

import java.util.UUID;
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
public class CbbGetTaskUpgradeTerminalRequest implements Request {
    
    @NotNull
    private UUID upgradeTaskId;
    
    @Nullable
    private CbbSystemUpgradeStateEnums terminalState;

    public UUID getUpgradeTaskId() {
        return upgradeTaskId;
    }

    public void setUpgradeTaskId(UUID upgradeTaskId) {
        this.upgradeTaskId = upgradeTaskId;
    }

    public CbbSystemUpgradeStateEnums getTerminalState() {
        return terminalState;
    }

    public void setTerminalState(CbbSystemUpgradeStateEnums terminalState) {
        this.terminalState = terminalState;
    }
    
}
