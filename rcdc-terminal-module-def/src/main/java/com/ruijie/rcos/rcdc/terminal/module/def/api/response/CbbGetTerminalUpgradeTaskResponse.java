package com.ruijie.rcos.rcdc.terminal.module.def.api.response;

import org.springframework.util.Assert;
import com.ruijie.rcos.rcdc.terminal.module.def.api.dto.CbbSystemUpgradeTaskDTO;
import com.ruijie.rcos.sk.modulekit.api.comm.DefaultResponse;

/**
 * 
 * Description: 获取升级任务信息响应
 * Copyright: Copyright (c) 2018
 * Company: Ruijie Co., Ltd.
 * Create Time: 2019年3月19日
 * 
 * @author nt
 */
public class CbbGetTerminalUpgradeTaskResponse extends DefaultResponse {

    private CbbSystemUpgradeTaskDTO upgradeTask;
    
    public CbbGetTerminalUpgradeTaskResponse(CbbSystemUpgradeTaskDTO upgradeTask) {
        Assert.notNull(upgradeTask, "upgradeTask can not be null");
        this.upgradeTask = upgradeTask;
    }

    public CbbSystemUpgradeTaskDTO getUpgradeTask() {
        return upgradeTask;
    }

    public void setUpgradeTask(CbbSystemUpgradeTaskDTO upgradeTask) {
        this.upgradeTask = upgradeTask;
    }
    
}
