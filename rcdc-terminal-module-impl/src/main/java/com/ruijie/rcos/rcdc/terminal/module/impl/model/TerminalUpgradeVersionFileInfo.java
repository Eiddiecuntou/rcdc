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
    
    /**
     * 升级包名称
     */
    private String imgName;
    
    /**
     * 内部版本号
     */
    private String version;
    
    /**
     * 更新包类型
     */
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

    @Override
    public String toString() {
        return "TerminalUpgradeVersionFileInfo [imgName=" + imgName + ", version=" + version + ", packageType="
                + packageType + "]";
    }
   
}
