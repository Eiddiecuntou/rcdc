package com.ruijie.rcos.rcdc.terminal.module.impl.entity;

import com.ruijie.rcos.rcdc.terminal.module.def.api.enums.CbbSystemUpgradeModeEnums;
import com.ruijie.rcos.rcdc.terminal.module.def.enums.CbbSystemUpgradeDistributionModeEnums;
import com.ruijie.rcos.rcdc.terminal.module.def.enums.CbbSystemUpgradePackageOriginEnums;
import com.ruijie.rcos.rcdc.terminal.module.def.enums.CbbTerminalTypeEnums;

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

    private String fileMd5;

    private String seedPath;

    private String seedMd5;

    private String otaScriptPath;

    private String otaScriptMd5;

    @Enumerated(EnumType.STRING)
    private CbbTerminalTypeEnums packageType;

    private Date uploadTime;

    private String packageVersion;

    @Version
    private int version;


    @Enumerated(EnumType.STRING)
    private CbbSystemUpgradePackageOriginEnums origin;

    @Enumerated(EnumType.STRING)
    private CbbSystemUpgradeDistributionModeEnums distributionMode;

    private Boolean isDelete;

    @Enumerated(EnumType.STRING)
    private CbbSystemUpgradeModeEnums upgradeMode;

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

    public String getFileMd5() {
        return fileMd5;
    }

    public void setFileMd5(String fileMd5) {
        this.fileMd5 = fileMd5;
    }

    public String getSeedPath() {
        return seedPath;
    }

    public void setSeedPath(String seedPath) {
        this.seedPath = seedPath;
    }

    public String getSeedMd5() {
        return seedMd5;
    }

    public void setSeedMd5(String seedMd5) {
        this.seedMd5 = seedMd5;
    }

    public CbbTerminalTypeEnums getPackageType() {
        return packageType;
    }

    public void setPackageType(CbbTerminalTypeEnums packageType) {
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

    public Boolean getIsDelete() {
        return isDelete;
    }

    public void setIsDelete(Boolean isDelete) {
        this.isDelete = isDelete;
    }

    public String getOtaScriptPath() {
        return otaScriptPath;
    }

    public void setOtaScriptPath(String otaScriptPath) {
        this.otaScriptPath = otaScriptPath;
    }

    public String getOtaScriptMd5() {
        return otaScriptMd5;
    }

    public void setOtaScriptMd5(String otaScriptMd5) {
        this.otaScriptMd5 = otaScriptMd5;
    }

    public CbbSystemUpgradeModeEnums getUpgradeMode() {
        return upgradeMode;
    }

    public void setUpgradeMode(CbbSystemUpgradeModeEnums upgradeMode) {
        this.upgradeMode = upgradeMode;
    }
}
