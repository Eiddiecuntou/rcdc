package com.ruijie.rcos.rcdc.terminal.module.def.api.request;

import java.util.UUID;

import com.ruijie.rcos.rcdc.terminal.module.def.api.enums.CbbSystemUpgradeModeEnums;
import com.ruijie.rcos.sk.base.annotation.NotEmpty;
import com.ruijie.rcos.sk.base.annotation.NotNull;
import com.ruijie.rcos.sk.modulekit.api.comm.Request;

/**
 * 
 * Description: 添加终端刷机任务请求参数
 * Copyright: Copyright (c) 2018
 * Company: Ruijie Co., Ltd.
 * Create Time: 2019年2月14日
 * 
 * @author nt
 */
public class CbbAddSystemUpgradeTaskRequest implements Request {

    @NotNull
    private UUID packageId;

    @NotNull
    private CbbSystemUpgradeModeEnums upgradeMode;

    @NotEmpty
    private String[] terminalIdArr;

    public UUID getPackageId() {
        return packageId;
    }

    public void setPackageId(UUID packageId) {
        this.packageId = packageId;
    }

    public String[] getTerminalIdArr() {
        return terminalIdArr;
    }

    public void setTerminalIdArr(String[] terminalIdArr) {
        this.terminalIdArr = terminalIdArr;
    }

    public CbbSystemUpgradeModeEnums getUpgradeMode() {
        return upgradeMode;
    }

    public void setUpgradeMode(CbbSystemUpgradeModeEnums upgradeMode) {
        this.upgradeMode = upgradeMode;
    }
}
