package com.ruijie.rcos.rcdc.terminal.module.def.api.dto.updatelist;

import org.springframework.util.Assert;

/**
 * Description: Function Description
 * Copyright: Copyright (c) 2019
 * Company: Ruijie Co., Ltd.
 * Create Time: 2019/10/10
 *
 * @author XiaoJiaXin
 */
public class CbbAndroidVDIUpdateListDTO extends CbbCommonUpdatelistDTO<CbbVDIComponentCommonVersionInfoDTO> {

    private String baseVersion;

    private String osLimit;

    private String versionLimit;

    public CbbAndroidVDIUpdateListDTO() {

    }

    public CbbAndroidVDIUpdateListDTO(CbbAndroidVDIUpdateListDTO updatelist) {
        Assert.notNull(updatelist, "updatelist cannot be null");
        Assert.notNull(updatelist.getVersion(), "version cannot be null");
        Assert.notNull(updatelist.getComponentSize(), "componentSize cannot be null");
        Assert.notNull(updatelist.getOsLimit(), "osLimit cannot be null");
        Assert.notNull(updatelist.getBaseVersion(), "baseVersion cannot be null");
        super.setVersion(updatelist.getVersion());
        super.setComponentSize(updatelist.getComponentSize());
        this.osLimit = updatelist.getOsLimit();
        this.baseVersion = updatelist.getBaseVersion();
    }

    public String getOsLimit() {
        return osLimit;
    }

    public void setOsLimit(String osLimit) {
        this.osLimit = osLimit;
    }

    public String getBaseVersion() {
        return baseVersion;
    }

    public void setBaseVersion(String baseVersion) {
        this.baseVersion = baseVersion;
    }

    public String getVersionLimit() {
        return versionLimit;
    }

    public void setVersionLimit(String versionLimit) {
        this.versionLimit = versionLimit;
    }
}
