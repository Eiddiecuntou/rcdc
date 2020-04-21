package com.ruijie.rcos.rcdc.terminal.module.def.api.dto;

import com.ruijie.rcos.rcdc.terminal.module.def.api.enums.CbbSystemUpgradeModeEnums;
import com.ruijie.rcos.rcdc.terminal.module.def.api.enums.CbbSystemUpgradeTaskStateEnums;
import com.ruijie.rcos.rcdc.terminal.module.def.enums.CbbSystemUpgradeDistributionModeEnums;
import com.ruijie.rcos.rcdc.terminal.module.def.enums.CbbSystemUpgradePackageOriginEnums;
import com.ruijie.rcos.rcdc.terminal.module.def.enums.CbbTerminalTypeEnums;

import java.util.Date;
import java.util.UUID;

/**
 * 
 * Description: 终端系统升级包信息
 * Copyright: Copyright (c) 2018
 * Company: Ruijie Co., Ltd.
 * Create Time: 2018年11月19日
 * 
 * @author nt
 */
public class CbbTerminalSystemUpgradePackageInfoDTO {

    private UUID id;

    private String name;

    /**
     * 刷机包平台类型
     */
    private CbbTerminalTypeEnums packageType;

    private CbbSystemUpgradePackageOriginEnums origin;

    private CbbSystemUpgradeDistributionModeEnums distributionMode;

    private CbbSystemUpgradeTaskStateEnums state;

    private UUID upgradeTaskId;

    private Date uploadTime;

    private CbbSystemUpgradeModeEnums upgradeMode;

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public CbbTerminalTypeEnums getPackageType() {
        return packageType;
    }

    public void setPackageType(CbbTerminalTypeEnums packageType) {
        this.packageType = packageType;
    }

    public CbbSystemUpgradePackageOriginEnums getOrigin() {
        return origin;
    }

    public void setOrigin(CbbSystemUpgradePackageOriginEnums origin) {
        this.origin = origin;
    }

    public CbbSystemUpgradeDistributionModeEnums getDistributionMode() {
        return distributionMode;
    }

    public void setDistributionMode(CbbSystemUpgradeDistributionModeEnums distributionMode) {
        this.distributionMode = distributionMode;
    }

    public CbbSystemUpgradeTaskStateEnums getState() {
        return state;
    }

    public void setState(CbbSystemUpgradeTaskStateEnums state) {
        this.state = state;
    }

    public UUID getUpgradeTaskId() {
        return upgradeTaskId;
    }

    public void setUpgradeTaskId(UUID upgradeTaskId) {
        this.upgradeTaskId = upgradeTaskId;
    }

    public Date getUploadTime() {
        return uploadTime;
    }

    public void setUploadTime(Date uploadTime) {
        this.uploadTime = uploadTime;
    }

    public CbbSystemUpgradeModeEnums getUpgradeMode() {
        return upgradeMode;
    }

    public void setUpgradeMode(CbbSystemUpgradeModeEnums upgradeMode) {
        this.upgradeMode = upgradeMode;
    }
}
