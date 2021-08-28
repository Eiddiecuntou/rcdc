package com.ruijie.rcos.rcdc.terminal.module.def.api.dto;

import java.util.UUID;
import org.springframework.lang.Nullable;
import com.ruijie.rcos.rcdc.terminal.module.def.api.enums.CbbSystemUpgradeStateEnums;
import com.ruijie.rcos.sk.base.annotation.NotNull;

/**
 * 
 * Description: 获取刷机任务终端列表请求参数
 * Copyright: Copyright (c) 2018
 * Company: Ruijie Co., Ltd.
 * Create Time: 2019年3月18日
 * 
 * @author nt
 */
public class CbbGetTaskUpgradeTerminalDTO {

    @NotNull
    private UUID taskId;

    @Nullable
    private CbbSystemUpgradeStateEnums terminalState;

    public UUID getTaskId() {
        return taskId;
    }

    public void setTaskId(UUID taskId) {
        this.taskId = taskId;
    }

    public CbbSystemUpgradeStateEnums getTerminalState() {
        return terminalState;
    }

    public void setTerminalState(CbbSystemUpgradeStateEnums terminalState) {
        this.terminalState = terminalState;
    }

}
