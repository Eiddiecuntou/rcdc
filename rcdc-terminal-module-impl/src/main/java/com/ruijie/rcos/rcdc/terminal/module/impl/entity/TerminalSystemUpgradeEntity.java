package com.ruijie.rcos.rcdc.terminal.module.impl.entity;

import java.util.Date;
import java.util.UUID;

import javax.persistence.*;

import com.ruijie.rcos.rcdc.terminal.module.def.api.enums.CbbFlashModeEnums;
import com.ruijie.rcos.rcdc.terminal.module.def.api.enums.CbbSystemUpgradeTaskStateEnums;
import com.ruijie.rcos.rcdc.terminal.module.def.enums.CbbCpuArchType;
import com.ruijie.rcos.rcdc.terminal.module.def.enums.CbbTerminalTypeEnums;

/**
 * 
 * Description: Function Description
 * Copyright: Copyright (c) 2018
 * Company: Ruijie Co., Ltd.
 * Create Time: 2019年2月13日
 * 
 * @author nt
 */
@Entity
@Table(name = "t_cbb_sys_upgrade")
public class TerminalSystemUpgradeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    /**
     * 系统刷机包id
     **/
    private UUID upgradePackageId;

    /**
     * 刷机包版本号
     **/
    private String packageVersion;

    /**
     * 刷机包镜像名称
     **/
    private String packageName;

    @Enumerated(EnumType.STRING)
    private CbbTerminalTypeEnums packageType;

    /**
     * 生成时间
     **/
    private Date createTime;

    /**
     * 任务状态
     **/
    @Enumerated(EnumType.STRING)
    private CbbSystemUpgradeTaskStateEnums state;

    /**
     * 版本号，实现乐观锁
     **/
    @Version
    private Integer version;

    /**
     * 刷机方式
     **/
    @Enumerated(EnumType.STRING)
    private CbbFlashModeEnums flashMode;

    @Enumerated(EnumType.STRING)
    private CbbCpuArchType cpuArch;

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public UUID getUpgradePackageId() {
        return upgradePackageId;
    }

    public void setUpgradePackageId(UUID upgradePackageId) {
        this.upgradePackageId = upgradePackageId;
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

    public CbbTerminalTypeEnums getPackageType() {
        return packageType;
    }

    public void setPackageType(CbbTerminalTypeEnums packageType) {
        this.packageType = packageType;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public CbbSystemUpgradeTaskStateEnums getState() {
        return state;
    }

    public void setState(CbbSystemUpgradeTaskStateEnums state) {
        this.state = state;
    }

    public Integer getVersion() {
        return version;
    }

    public void setVersion(Integer version) {
        this.version = version;
    }

    public CbbFlashModeEnums getFlashMode() {
        return flashMode;
    }

    public void setFlashMode(CbbFlashModeEnums flashMode) {
        this.flashMode = flashMode;
    }

    public CbbCpuArchType getCpuArch() {
        return cpuArch;
    }

    public void setCpuArch(CbbCpuArchType cpuArch) {
        this.cpuArch = cpuArch;
    }
}
