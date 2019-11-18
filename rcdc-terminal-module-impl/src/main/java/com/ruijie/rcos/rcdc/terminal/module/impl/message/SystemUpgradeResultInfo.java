package com.ruijie.rcos.rcdc.terminal.module.impl.message;

import java.util.UUID;

import com.ruijie.rcos.rcdc.terminal.module.def.api.enums.CbbSystemUpgradeStateEnums;

/**
 * Description: Function Description
 * Copyright: Copyright (c) 2019
 * Company: Ruijie Co., Ltd.
 * Create Time: 2019/10/14
 *
 * @author hs
 */
public class SystemUpgradeResultInfo {

    private CbbSystemUpgradeStateEnums upgradeState;

    private UUID taskId;

    public CbbSystemUpgradeStateEnums getUpgradeState() {
        return upgradeState;
    }

    public void setUpgradeState(CbbSystemUpgradeStateEnums upgradeState) {
        this.upgradeState = upgradeState;
    }

    public UUID getTaskId() {
        return taskId;
    }

    public void setTaskId(UUID taskId) {
        this.taskId = taskId;
    }
}
