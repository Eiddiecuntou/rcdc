package com.ruijie.rcos.rcdc.terminal.module.def.api.dto.updatelist;

/**
 * Description: 通用升级updatelistDTO
 * Copyright: Copyright (c) 2018
 * Company: Ruijie Co., Ltd.
 * Create Time: 2019/11/7
 *
 * @author nt
 */
public class CbbCommonUpdateListDTO extends CbbBaseUpdateListDTO<CbbCommonComponentVersionInfoDTO> {

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
