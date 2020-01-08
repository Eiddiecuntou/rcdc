package com.ruijie.rcos.rcdc.terminal.module.impl.service.impl.handler.systemupgrade;

import java.util.UUID;

import com.ruijie.rcos.rcdc.terminal.module.def.api.enums.CbbSystemUpgradeModeEnums;

/**
 * Description: Function Description
 * Copyright: Copyright (c) 2018
 * Company: Ruijie Co., Ltd.
 * Create Time: 2019/11/11
 *
 * @author nt
 */
public class LinuxVDICheckResultContent {

    /**
     * 升级镜像名称
     */
    private String imgName;

    /**
     * 版本号 （保留兼容旧版本）
     */
    private String isoVersion;

    /**
     * 版本号
     */
    private String packageVersion;

    /**
     * 升级方式
     */
    private CbbSystemUpgradeModeEnums upgradeMode;

    private UUID taskId;

    /** samba 信息 */
    private String sambaUserName;

    private String sambaPassword;

    private String sambaIp;

    private String sambaPort;

    private String sambaFilePath;

    private String upgradePackageName;

    public String getImgName() {
        return imgName;
    }

    public void setImgName(String imgName) {
        this.imgName = imgName;
    }

    public String getIsoVersion() {
        return isoVersion;
    }

    public void setIsoVersion(String isoVersion) {
        this.isoVersion = isoVersion;
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

    public String getSambaUserName() {
        return sambaUserName;
    }

    public void setSambaUserName(String sambaUserName) {
        this.sambaUserName = sambaUserName;
    }

    public String getSambaPassword() {
        return sambaPassword;
    }

    public void setSambaPassword(String sambaPassword) {
        this.sambaPassword = sambaPassword;
    }

    public String getSambaIp() {
        return sambaIp;
    }

    public void setSambaIp(String sambaIp) {
        this.sambaIp = sambaIp;
    }

    public String getSambaPort() {
        return sambaPort;
    }

    public void setSambaPort(String sambaPort) {
        this.sambaPort = sambaPort;
    }

    public String getSambaFilePath() {
        return sambaFilePath;
    }

    public void setSambaFilePath(String sambaFilePath) {
        this.sambaFilePath = sambaFilePath;
    }

    public String getUpgradePackageName() {
        return upgradePackageName;
    }

    public void setUpgradePackageName(String upgradePackageName) {
        this.upgradePackageName = upgradePackageName;
    }

    public String getPackageVersion() {
        return packageVersion;
    }

    public void setPackageVersion(String packageVersion) {
        this.packageVersion = packageVersion;
    }
}
