package com.ruijie.rcos.rcdc.terminal.module.def.api.dto;

import java.util.Date;
import java.util.UUID;
import com.ruijie.rcos.rcdc.terminal.module.def.api.enums.CbbSystemUpgradeTaskStateEnums;

/**
 * 
 * Description: 终端系统刷机任务
 * Copyright: Copyright (c) 2018
 * Company: Ruijie Co., Ltd.
 * Create Time: 2018年11月27日
 * 
 * @author nt
 */
public class CbbSystemUpgradeTaskDTO {

    private UUID id;

    private String packageVersion;

    private String packageName;

    private Date createTime;

    private CbbSystemUpgradeTaskStateEnums upgradeTaskState;

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getPackageVersion() {
        return packageVersion;
    }

    public void setPackageVersion(String packageVersion) {
        this.packageVersion = packageVersion;
    }

    public String getPackageName() {
        return packageName;
    }

    public void setPackageName(String packageName) {
        this.packageName = packageName;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public CbbSystemUpgradeTaskStateEnums getUpgradeTaskState() {
        return upgradeTaskState;
    }

    public void setUpgradeTaskState(CbbSystemUpgradeTaskStateEnums upgradeTaskState) {
        this.upgradeTaskState = upgradeTaskState;
    }

}
