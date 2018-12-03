package com.ruijie.rcos.rcdc.terminal.module.impl.model;

import com.ruijie.rcos.rcdc.terminal.module.def.api.enums.CbbTerminalTypeEnums;

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
    private String packageName;
    
    /**
     * 内部版本号
     */
    private String internalVersion;
    
    /**
     * 外部版本号
     */
    private String externalVersion;
    
    /**
     * 更新包类型
     */
    private CbbTerminalTypeEnums packageType;
   

    public String getPackageName() {
        return packageName;
    }

    public void setPackageName(String packageName) {
        this.packageName = packageName;
    }

    public String getInternalVersion() {
        return internalVersion;
    }

    public void setInternalVersion(String internalVersion) {
        this.internalVersion = internalVersion;
    }

    public String getExternalVersion() {
        return externalVersion;
    }

    public void setExternalVersion(String externalVersion) {
        this.externalVersion = externalVersion;
    }

    public CbbTerminalTypeEnums getPackageType() {
        return packageType;
    }

    public void setPackageType(CbbTerminalTypeEnums packageType) {
        this.packageType = packageType;
    }

    @Override
    public String toString() {
        return "TerminalUpgradeVersionFileInfo [packageName=" + packageName + ", internalVersion=" + internalVersion
                + ", externalVersion=" + externalVersion + ", packageType=" + packageType + ", getPackageName()="
                + getPackageName() + ", getInternalVersion()=" + getInternalVersion() + ", getExternalVersion()="
                + getExternalVersion() + ", getPackageType()=" + getPackageType() + ", getClass()=" + getClass()
                + ", hashCode()=" + hashCode() + ", toString()=" + super.toString() + "]";
    }
    
    
    
}
