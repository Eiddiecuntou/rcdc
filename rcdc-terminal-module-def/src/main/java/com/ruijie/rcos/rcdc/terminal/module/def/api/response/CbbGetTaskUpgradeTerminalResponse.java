package com.ruijie.rcos.rcdc.terminal.module.def.api.response;

import java.util.List;
import org.springframework.util.Assert;
import com.ruijie.rcos.rcdc.terminal.module.def.api.dto.CbbSystemUpgradeTaskTerminalDTO;
import com.ruijie.rcos.sk.modulekit.api.comm.DefaultResponse;

/**
 * 
 * Description: 获取刷机任务终端列表响应
 * Copyright: Copyright (c) 2018
 * Company: Ruijie Co., Ltd.
 * Create Time: 2019年3月18日
 * 
 * @author nt
 */
public class CbbGetTaskUpgradeTerminalResponse extends DefaultResponse {
    
    private List<CbbSystemUpgradeTaskTerminalDTO> upgradeTerminalList;
    
    public CbbGetTaskUpgradeTerminalResponse(List<CbbSystemUpgradeTaskTerminalDTO> upgradeTerminalList) {
        Assert.notNull(upgradeTerminalList, "upgradeTerminalList can not be null");
        this.upgradeTerminalList = upgradeTerminalList;
    }

    public List<CbbSystemUpgradeTaskTerminalDTO> getUpgradeTerminalList() {
        return upgradeTerminalList;
    }

    public void setUpgradeTerminalList(List<CbbSystemUpgradeTaskTerminalDTO> upgradeTerminalList) {
        this.upgradeTerminalList = upgradeTerminalList;
    }
    
}
