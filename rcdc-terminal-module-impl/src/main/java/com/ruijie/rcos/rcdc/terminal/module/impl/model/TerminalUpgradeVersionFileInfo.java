package com.ruijie.rcos.rcdc.terminal.module.impl.model;

import com.ruijie.rcos.rcdc.terminal.module.def.api.enums.CbbSystemUpgradeModeEnums;
import com.ruijie.rcos.rcdc.terminal.module.def.enums.CbbCpuArchType;
import com.ruijie.rcos.rcdc.terminal.module.def.enums.CbbTerminalTypeEnums;
import com.ruijie.rcos.sk.base.support.EqualsHashcodeSupport;

/**
 * 
 * Description: 升级文件内的版本文件信息
 * Copyright: Copyright (c) 2018
 * Company: Ruijie Co., Ltd.
 * Create Time: 2018年11月19日
 * 
 * @author nt
 */
public class TerminalUpgradeVersionFileInfo extends EqualsHashcodeSupport {

    private String packageName;

    private String imgName;

    private String version;

    private String filePath;

    private CbbTerminalTypeEnums packageType;

    private String fileMD5;

    private String seedLink;

    private String seedMD5;

    private String fileSaveDir;

    private String realFileName;

    private String otaScriptPath;

    private String otaScriptMD5;

    /**
     * 默认升级方式
     */
    private CbbSystemUpgradeModeEnums upgradeMode;

    private String supportCpu;

    private CbbCpuArchType cpuArch;

    private String unzipPath;

    private String mountPath;

    private String otaScriptFileName;

    public String getImgName() {
        return imgName;
    }

    public void setImgName(String imgName) {
        this.imgName = imgName;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public CbbTerminalTypeEnums getPackageType() {
        return packageType;
    }

    public void setPackageType(CbbTerminalTypeEnums packageType) {
        this.packageType = packageType;
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

    public String getSeedLink() {
        return seedLink;
    }

    public void setSeedLink(String seedLink) {
        this.seedLink = seedLink;
    }

    public String getSeedMD5() {
        return seedMD5;
    }

    public void setSeedMD5(String seedMD5) {
        this.seedMD5 = seedMD5;
    }

    public String getFileSaveDir() {
        return fileSaveDir;
    }

    public void setFileSaveDir(String fileSaveDir) {
        this.fileSaveDir = fileSaveDir;
    }

    public String getRealFileName() {
        return realFileName;
    }

    public void setRealFileName(String realFileName) {
        this.realFileName = realFileName;
    }

    public String getOtaScriptPath() {
        return otaScriptPath;
    }

    public void setOtaScriptPath(String otaScriptPath) {
        this.otaScriptPath = otaScriptPath;
    }

    public String getOtaScriptMD5() {
        return otaScriptMD5;
    }

    public void setOtaScriptMD5(String otaScriptMD5) {
        this.otaScriptMD5 = otaScriptMD5;
    }

    public CbbSystemUpgradeModeEnums getUpgradeMode() {
        return upgradeMode;
    }

    public void setUpgradeMode(CbbSystemUpgradeModeEnums upgradeMode) {
        this.upgradeMode = upgradeMode;
    }

    public String getSupportCpu() {
        return supportCpu;
    }

    public void setSupportCpu(String supportCpu) {
        this.supportCpu = supportCpu;
    }

    public CbbCpuArchType getCpuArch() {
        return cpuArch;
    }

    public void setCpuArch(CbbCpuArchType cpuArch) {
        this.cpuArch = cpuArch;
    }

    public String getUnzipPath() {
        return unzipPath;
    }

    public void setUnzipPath(String unzipPath) {
        this.unzipPath = unzipPath;
    }

    public String getMountPath() {
        return mountPath;
    }

    public void setMountPath(String mountPath) {
        this.mountPath = mountPath;
    }

    @Override
    public String toString() {
        return "TerminalUpgradeVersionFileInfo{" +
                "packageName='" + packageName + '\'' +
                ", imgName='" + imgName + '\'' +
                ", version='" + version + '\'' +
                ", filePath='" + filePath + '\'' +
                ", packageType=" + packageType +
                ", fileMD5='" + fileMD5 + '\'' +
                ", seedLink='" + seedLink + '\'' +
                ", seedMD5='" + seedMD5 + '\'' +
                ", fileSaveDir='" + fileSaveDir + '\'' +
                ", realFileName='" + realFileName + '\'' +
                ", otaScriptPath='" + otaScriptPath + '\'' +
                ", otaScriptMD5='" + otaScriptMD5 + '\'' +
                ", upgradeMode=" + upgradeMode +
                ", supportCpu='" + supportCpu + '\'' +
                ", cpuArch=" + cpuArch +
                ", unzipPath='" + unzipPath + '\'' +
                ", mountPath='" + mountPath + '\'' +
                ", otaScriptFileName='" + otaScriptFileName + '\'' +
                '}';
    }

    public String getOtaScriptFileName() {
        return otaScriptFileName;
    }

    public void setOtaScriptFileName(String otaScriptFileName) {
        this.otaScriptFileName = otaScriptFileName;
    }

}
