package com.ruijie.rcos.rcdc.terminal.module.def.api.dto;

import java.util.Date;
import java.util.UUID;
import com.ruijie.rcos.rcdc.terminal.module.def.enums.SystemUpgradeDistributionModeEnums;
import com.ruijie.rcos.rcdc.terminal.module.def.enums.SystemUpgradePackageOriginEnums;
import com.ruijie.rcos.rcdc.terminal.module.def.enums.TerminalPlatformEnums;

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
    private TerminalPlatformEnums packageType;
    
    private SystemUpgradePackageOriginEnums origin;
    
    private SystemUpgradeDistributionModeEnums distributionMode;
    
    private Boolean isUpgrading;
    
    private UUID upgradeTaskId;
    
    private Date uploadTime;

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

    public TerminalPlatformEnums getPackageType() {
        return packageType;
    }

    public void setPackageType(TerminalPlatformEnums packageType) {
        this.packageType = packageType;
    }

    public SystemUpgradePackageOriginEnums getOrigin() {
        return origin;
    }

    public void setOrigin(SystemUpgradePackageOriginEnums origin) {
        this.origin = origin;
    }

    public SystemUpgradeDistributionModeEnums getDistributionMode() {
        return distributionMode;
    }

    public void setDistributionMode(SystemUpgradeDistributionModeEnums distributionMode) {
        this.distributionMode = distributionMode;
    }

    public Boolean getIsUpgrading() {
        return isUpgrading;
    }

    public void setIsUpgrading(Boolean isUpgrading) {
        this.isUpgrading = isUpgrading;
    }

    public UUID getUpgradeTaskId() {
        return upgradeTaskId;
    }

    public void setUpgradeTaskId(UUID upgradeTaskId) {
        this.upgradeTaskId = upgradeTaskId;
    }

}
