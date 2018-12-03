package com.ruijie.rcos.rcdc.terminal.module.impl.message;

/**
 * 
 * Description: 系统升级消息
 * Copyright: Copyright (c) 2018
 * Company: Ruijie Co., Ltd.
 * Create Time: 2018年11月22日
 * 
 * @author "nt"
 */
public class TerminalSystemUpgradeMsg {
    
    /**
     * 系统升级包名称
     */
    private String packageName;
    
    /**
     * 系统升级包路径
     */
    private String packagePath;
    
    
    /**
     * 内部版本号
     */
    private String internalVersion;
    
    /**
     * 外部版本号
     */
    private String externalVersion;
    
    

    public TerminalSystemUpgradeMsg() {
    }

    public TerminalSystemUpgradeMsg(String packageName, String packagePath, String internalVersion,
            String externalVersion) {
        this.packageName = packageName;
        this.packagePath = packagePath;
        this.internalVersion = internalVersion;
        this.externalVersion = externalVersion;
    }

    public String getPackageName() {
        return packageName;
    }

    public void setPackageName(String packageName) {
        this.packageName = packageName;
    }

    public String getPackagePath() {
        return packagePath;
    }

    public void setPackagePath(String packagePath) {
        this.packagePath = packagePath;
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

    
}
