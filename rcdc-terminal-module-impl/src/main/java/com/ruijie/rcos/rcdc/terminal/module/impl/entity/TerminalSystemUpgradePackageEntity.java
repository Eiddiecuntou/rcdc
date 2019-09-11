package com.ruijie.rcos.rcdc.terminal.module.impl.entity;

import java.util.Date;
import java.util.UUID;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Version;
import com.ruijie.rcos.rcdc.terminal.module.def.enums.CbbSystemUpgradeDistributionModeEnums;
import com.ruijie.rcos.rcdc.terminal.module.def.enums.CbbSystemUpgradePackageOriginEnums;
import com.ruijie.rcos.rcdc.terminal.module.def.enums.CbbTerminalPlatformEnums;

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

    @Enumerated(EnumType.STRING)
    private CbbTerminalPlatformEnums packageType;

    private Date uploadTime;

    private String packageVersion;

    @Version
    private int version;


    @Enumerated(EnumType.STRING)
    private CbbSystemUpgradePackageOriginEnums origin;

    @Enumerated(EnumType.STRING)
    private CbbSystemUpgradeDistributionModeEnums distributionMode;

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


    public CbbTerminalPlatformEnums getPackageType() {
        return packageType;
    }


    public void setPackageType(CbbTerminalPlatformEnums packageType) {
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

}
