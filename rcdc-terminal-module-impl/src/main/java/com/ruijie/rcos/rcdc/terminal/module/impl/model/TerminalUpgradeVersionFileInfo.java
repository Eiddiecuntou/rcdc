package com.ruijie.rcos.rcdc.terminal.module.impl.model;

import com.ruijie.rcos.rcdc.terminal.module.def.enums.CbbTerminalTypeEnums;

/**
 * 
 * Description: 升级文件内的版本文件信息
 * Copyright: Copyright (c) 2018
 * Company: Ruijie Co., Ltd.
 * Create Time: 2018年11月19日
 * 
 * @author nt
 */
public class TerminalUpgradeVersionFileInfo {

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
                '}';
    }
}
