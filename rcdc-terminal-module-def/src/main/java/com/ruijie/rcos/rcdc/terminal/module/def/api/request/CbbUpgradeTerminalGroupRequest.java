package com.ruijie.rcos.rcdc.terminal.module.def.api.request;

import java.util.UUID;

import com.ruijie.rcos.sk.base.annotation.NotNull;
import com.ruijie.rcos.sk.modulekit.api.comm.Request;

/**
 * 
 * Description: 取消终端刷机
 * Copyright: Copyright (c) 2018
 * Company: Ruijie Co., Ltd.
 * Create Time: 2019年3月18日
 * 
 * @author nt
 */
public class CbbUpgradeTerminalGroupRequest implements Request {

    @NotNull
    private UUID upgradeTaskId;

    @NotNull
    private UUID[] terminalGroupIdArr;

    public UUID getUpgradeTaskId() {
        return upgradeTaskId;
    }

    public void setUpgradeTaskId(UUID upgradeTaskId) {
        this.upgradeTaskId = upgradeTaskId;
    }

    public UUID[] getTerminalGroupIdArr() {
        return terminalGroupIdArr;
    }

    public void setTerminalGroupIdArr(UUID[] terminalGroupIdArr) {
        this.terminalGroupIdArr = terminalGroupIdArr;
    }
}
