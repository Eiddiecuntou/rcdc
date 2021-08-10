package com.ruijie.rcos.rcdc.terminal.module.def.api.dto;

import com.ruijie.rcos.rcdc.terminal.module.def.api.enums.CbbSystemUpgradeModeEnums;
import com.ruijie.rcos.rcdc.terminal.module.def.enums.CbbSystemUpgradeDistributionModeEnums;
import com.ruijie.rcos.rcdc.terminal.module.def.enums.CbbSystemUpgradePackageOriginEnums;
import com.ruijie.rcos.rcdc.terminal.module.def.enums.CbbTerminalTypeEnums;

import javax.persistence.*;
import java.util.Date;
import java.util.UUID;

/**
 * Description: Function Description
 * Copyright: Copyright (c)
 * Company: Ruijie Co., Ltd.
 * Create Time: 2021/8/5 21:02
 *
 * @author coderLee23
 */
public class CbbSystemUpgradePackageInfoDTO {

    private UUID id;

    /**
     * 升级包名称
     **/
    private String imgName;

    /**
     * 升级包名称
     **/
    private String packageName;

    /**
     * 刷机包存放路径
     **/
    private String filePath;

    private String fileMd5;

    private String seedPath;

    private String seedMd5;

    private String otaScriptPath;

    private String otaScriptMd5;

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
}
