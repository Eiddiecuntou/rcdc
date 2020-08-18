package com.ruijie.rcos.rcdc.terminal.module.impl.dto;

/**
 * Description: 通用升级updatelistDTO
 * Copyright: Copyright (c) 2018
 * Company: Ruijie Co., Ltd.
 * Create Time: 2019/11/7
 *
 * @author nt
 */
public class CommonUpdateListDTO extends BaseUpdateListDTO<CommonComponentVersionInfoDTO> {

    private String baseVersion;

    private String osLimit;

    public String getBaseVersion() {
        return baseVersion;
    }

    public void setBaseVersion(String baseVersion) {
        this.baseVersion = baseVersion;
    }

    public String getOsLimit() {
        return osLimit;
    }

    public void setOsLimit(String osLimit) {
        this.osLimit = osLimit;
    }
}
