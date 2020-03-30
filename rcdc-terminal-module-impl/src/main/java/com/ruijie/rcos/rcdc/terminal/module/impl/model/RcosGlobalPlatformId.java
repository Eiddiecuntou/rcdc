package com.ruijie.rcos.rcdc.terminal.module.impl.model;

/**
 * Description:
 * Copyright: Copyright (c) 2020
 * Company: Ruijie Co., Ltd.
 * Create Time: 2020年3月30日
 *
 * @author jarman
 */
public class RcosGlobalPlatformId {

    private String platformId;

    private Boolean managed;

    public String getPlatformId() {
        return platformId;
    }

    public void setPlatformId(String platformId) {
        this.platformId = platformId;
    }

    public Boolean getManaged() {
        return managed;
    }

    public void setManaged(Boolean managed) {
        this.managed = managed;
    }
}
