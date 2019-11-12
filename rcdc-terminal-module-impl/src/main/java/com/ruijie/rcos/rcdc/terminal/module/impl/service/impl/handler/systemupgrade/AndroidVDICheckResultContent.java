package com.ruijie.rcos.rcdc.terminal.module.impl.service.impl.handler.systemupgrade;

import com.ruijie.rcos.rcdc.terminal.module.def.api.enums.CbbSystemUpgradeModeEnums;

import java.util.UUID;

/**
 * Description: Function Description
 * Copyright: Copyright (c) 2018
 * Company: Ruijie Co., Ltd.
 * Create Time: 2019/11/11
 *
 * @author nt
 */
public class AndroidVDICheckResultContent {

    private String otaVersion;

    private String otaMD5;

    private String otaSeedLink;

    private String otaSeedMD5;

    private CbbSystemUpgradeModeEnums upgradeMode;

    private UUID taskId;

    public String getOtaVersion() {
        return otaVersion;
    }

    public void setOtaVersion(String otaVersion) {
        this.otaVersion = otaVersion;
    }

    public String getOtaMD5() {
        return otaMD5;
    }

    public void setOtaMD5(String otaMD5) {
        this.otaMD5 = otaMD5;
    }

    public String getOtaSeedLink() {
        return otaSeedLink;
    }

    public void setOtaSeedLink(String otaSeedLink) {
        this.otaSeedLink = otaSeedLink;
    }

    public String getOtaSeedMD5() {
        return otaSeedMD5;
    }

    public void setOtaSeedMD5(String otaSeedMD5) {
        this.otaSeedMD5 = otaSeedMD5;
    }

    public CbbSystemUpgradeModeEnums getUpgradeMode() {
        return upgradeMode;
    }

    public void setUpgradeMode(CbbSystemUpgradeModeEnums upgradeMode) {
        this.upgradeMode = upgradeMode;
    }

    public UUID getTaskId() {
        return taskId;
    }

    public void setTaskId(UUID taskId) {
        this.taskId = taskId;
    }
}
