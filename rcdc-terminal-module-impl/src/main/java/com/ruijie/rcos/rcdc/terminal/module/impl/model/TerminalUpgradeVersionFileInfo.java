package com.ruijie.rcos.rcdc.terminal.module.impl.model;

import com.ruijie.rcos.rcdc.terminal.module.def.enums.TerminalPlatformEnums;

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
    
    private TerminalPlatformEnums packageType;

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

    public TerminalPlatformEnums getPackageType() {
        return packageType;
    }

    public void setPackageType(TerminalPlatformEnums packageType) {
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

    @Override
    public String toString() {
        return "TerminalUpgradeVersionFileInfo [packageName=" + packageName + ", imgName=" + imgName + ", version="
                + version + ", filePath=" + filePath + ", packageType=" + packageType + "]";
    }
   
}
