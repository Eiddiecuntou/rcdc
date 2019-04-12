package com.ruijie.rcos.rcdc.terminal.module.impl.message;

/**
 * 
 * Description: 系统升级消息
 * Copyright: Copyright (c) 2018
 * Company: Ruijie Co., Ltd.
 * Create Time: 2018年11月22日
 * 
 * @author nt
 */
public class TerminalSystemUpgradeMsg {

    /**
     * 升级镜像名称
     */
    private String imgName;

    /**
     * 版本号
     */
    private String isoVersion;


    public TerminalSystemUpgradeMsg() {
        
    }

    public TerminalSystemUpgradeMsg(String imgName, String isoVersion) {
        this.imgName = imgName;
        this.isoVersion = isoVersion;
    }

    public String getImgName() {
        return imgName;
    }

    public void setImgName(String imgName) {
        this.imgName = imgName;
    }

    public String getIsoVersion() {
        return isoVersion;
    }

    public void setIsoVersion(String isoVersion) {
        this.isoVersion = isoVersion;
    }

    @Override
    public String toString() {
        return "TerminalSystemUpgradeMsg [imgName=" + imgName + ", isoVersion=" + isoVersion + "]";
    }

}
