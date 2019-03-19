package com.ruijie.rcos.rcdc.terminal.module.web.ctrl.vo;

import com.ruijie.rcos.rcdc.terminal.module.def.api.dto.CbbSystemUpgradeTaskDTO;
import com.ruijie.rcos.rcdc.terminal.module.def.api.dto.CbbSystemUpgradeTaskTerminalDTO;

/**
 * 
 * Description: 升级任务终端列表VO
 * Copyright: Copyright (c) 2018
 * Company: Ruijie Co., Ltd.
 * Create Time: 2019年3月19日
 * 
 * @author nt
 */
public class UpgradeTerminalListContentVO {

    private CbbSystemUpgradeTaskTerminalDTO[] itemArr;
    
    private long total;
    
    private CbbSystemUpgradeTaskDTO upgradeTask;

    public CbbSystemUpgradeTaskTerminalDTO[] getItemArr() {
        return itemArr;
    }

    public void setItemArr(CbbSystemUpgradeTaskTerminalDTO[] itemArr) {
        this.itemArr = itemArr;
    }

    public long getTotal() {
        return total;
    }

    public void setTotal(long total) {
        this.total = total;
    }

    public CbbSystemUpgradeTaskDTO getUpgradeTask() {
        return upgradeTask;
    }

    public void setUpgradeTask(CbbSystemUpgradeTaskDTO upgradeTask) {
        this.upgradeTask = upgradeTask;
    }
    
}
