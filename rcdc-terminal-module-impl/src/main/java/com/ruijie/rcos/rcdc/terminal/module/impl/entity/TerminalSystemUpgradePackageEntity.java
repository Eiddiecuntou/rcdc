package com.ruijie.rcos.rcdc.terminal.module.impl.entity;

import com.ruijie.rcos.rcdc.terminal.module.def.api.enums.CbbSystemUpgradeModeEnums;
import com.ruijie.rcos.rcdc.terminal.module.def.enums.SystemUpgradeDistributionModeEnums;
import com.ruijie.rcos.rcdc.terminal.module.def.enums.SystemUpgradePackageOriginEnums;
import com.ruijie.rcos.rcdc.terminal.module.def.enums.TerminalTypeEnums;

import javax.persistence.*;
import java.util.Date;
import java.util.UUID;

/**
 * Description: 终端系统升级包实体类
 * Copyright: Copyright (c) 2018
 * Company: Ruijie Co., Ltd.
 * Create Time: 2018/10/30
 *
 * @author Jarman
 */
@Entity
@Table(name = "t_cbb_sys_upgrade_package")
public class TerminalSystemUpgradePackageEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    private String imgName;

    private String packageName;

    private String filePath;

    private String fileMD5;

    private String seedPath;

    private String seedMD5;

    @Enumerated(EnumType.STRING)
    private CbbSystemUpgradeModeEnums upgradeMode;

    @Enumerated(EnumType.STRING)
    private TerminalTypeEnums packageType;

    private Date uploadTime;

    private String packageVersion;

    @Version
    private int version;


    @Enumerated(EnumType.STRING)
    private SystemUpgradePackageOriginEnums origin;

    @Enumerated(EnumType.STRING)
    private SystemUpgradeDistributionModeEnums distributionMode;

    private Boolean isDelete;

    public UUID getId() {
        return id;
    }


    public void setId(UUID id) {
        this.id = id;
    }


    public String getImgName() {
        return imgName;
    }


    public void setImgName(String imgName) {
        this.imgName = imgName;
    }


    public String getPackageName() {
        return packageName;
    }


    public void setPackageName(String packageName) {
        this.packageName = packageName;
    }

    public String getFilePath() {
        return filePath;
    }


    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public String getFileMD5() {
        return fileMD5;
    }

    public void setFileMD5(String fileMD5) {
        this.fileMD5 = fileMD5;
    }

    public String getSeedPath() {
        return seedPath;
    }

    public void setSeedPath(String seedPath) {
        this.seedPath = seedPath;
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

    public TerminalTypeEnums getPackageType() {
        return packageType;
    }

    public void setPackageType(TerminalTypeEnums packageType) {
        this.packageType = packageType;
    }

    public Date getUploadTime() {
        return uploadTime;
    }


    public void setUploadTime(Date uploadTime) {
        this.uploadTime = uploadTime;
    }


    public String getPackageVersion() {
        return packageVersion;
    }


    public void setPackageVersion(String packageVersion) {
        this.packageVersion = packageVersion;
    }


    public int getVersion() {
        return version;
    }


    public void setVersion(int version) {
        this.version = version;
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

    public Boolean getIsDelete() {
        return isDelete;
    }

    public void setIsDelete(Boolean isDelete) {
        this.isDelete = isDelete;
    }

}
