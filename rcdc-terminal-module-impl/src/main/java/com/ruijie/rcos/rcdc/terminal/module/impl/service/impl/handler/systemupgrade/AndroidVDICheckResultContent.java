package com.ruijie.rcos.rcdc.terminal.module.impl.service.impl.handler.systemupgrade;

import com.ruijie.rcos.rcdc.terminal.module.def.api.enums.CbbSystemUpgradeModeEnums;
import com.ruijie.rcos.sk.base.support.EqualsHashcodeSupport;

import java.util.UUID;

/**
 * Description: Function Description
 * Copyright: Copyright (c) 2018
 * Company: Ruijie Co., Ltd.
 * Create Time: 2019/11/11
 *
 * @author nt
 */
public class AndroidVDICheckResultContent extends EqualsHashcodeSupport {

    private String packageName;

    private String packageVersion;

    private String packageMD5;

    private String seedLink;

    private String seedName;

    private String seedMD5;

    private CbbSystemUpgradeModeEnums upgradeMode;

    private UUID taskId;

    public String getPackageVersion() {
        return packageVersion;
    }

    public void setPackageVersion(String packageVersion) {
        this.packageVersion = packageVersion;
    }

    public String getPackageMD5() {
        return packageMD5;
    }

    public void setPackageMD5(String packageMD5) {
        this.packageMD5 = packageMD5;
    }

    public String getSeedLink() {
        return seedLink;
    }

    public void setSeedLink(String seedLink) {
        this.seedLink = seedLink;
    }

    public String getSeedMD5() {
        return seedMD5;
    }

    public void setSeedMD5(String seedMD5) {
        this.seedMD5 = seedMD5;
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

    public String getPackageName() {
        return packageName;
    }

    public void setPackageName(String packageName) {
        this.packageName = packageName;
    }

    public String getSeedName() {
        return seedName;
    }

    public void setSeedName(String seedName) {
        this.seedName = seedName;
    }
}
