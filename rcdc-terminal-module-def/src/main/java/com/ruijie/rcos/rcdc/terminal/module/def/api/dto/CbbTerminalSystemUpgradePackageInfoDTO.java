package com.ruijie.rcos.rcdc.terminal.module.def.api.dto;

import java.util.Date;
import java.util.UUID;
import com.ruijie.rcos.rcdc.terminal.module.def.api.enums.CbbTerminalTypeEnums;

/**
 * 
 * Description: 终端系统升级包信息
 * Copyright: Copyright (c) 2018
 * Company: Ruijie Co., Ltd.
 * Create Time: 2018年11月19日
 * 
 * @author "nt"
 */
public class CbbTerminalSystemUpgradePackageInfoDTO {
    
    private UUID id;

    private String name;


    /**
     * 包类型，VDI ISO、IDV ISO、OTA
     */
    private CbbTerminalTypeEnums packageType;
    
    private String origin;
    
    private String distributionMode;

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

    public String getOrigin() {
        return origin;
    }

    public void setOrigin(String origin) {
        this.origin = origin;
    }

    public String getDistributionMode() {
        return distributionMode;
    }

    public void setDistributionMode(String distributionMode) {
        this.distributionMode = distributionMode;
    }

    
}
