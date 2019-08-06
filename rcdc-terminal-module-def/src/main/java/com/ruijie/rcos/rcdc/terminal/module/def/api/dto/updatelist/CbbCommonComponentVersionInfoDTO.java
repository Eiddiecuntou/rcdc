package com.ruijie.rcos.rcdc.terminal.module.def.api.dto.updatelist;

import java.io.Serializable;

/**
 * Description: Function Description
 * Copyright: Copyright (c) 2018
 * Company: Ruijie Co., Ltd.
 * Create Time: 2019/8/1
 *
 * @author nt
 */
public class CbbCommonComponentVersionInfoDTO implements Serializable {

    /**
     * 组件名
     */
    private String name;

    /**
     * 组件版本
     */
    private String version;

    /**
     * 组件支持类型 idv&vdi 以&分隔
     */
    private String platform;

    /**
     * 组件文件MD5
     */
    private String md5;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getPlatform() {
        return platform;
    }

    public void setPlatform(String platform) {
        this.platform = platform;
    }

    public String getMd5() {
        return md5;
    }

    public void setMd5(String md5) {
        this.md5 = md5;
    }
}
