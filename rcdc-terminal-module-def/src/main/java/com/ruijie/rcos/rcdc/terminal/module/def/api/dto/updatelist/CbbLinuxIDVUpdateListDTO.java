package com.ruijie.rcos.rcdc.terminal.module.def.api.dto.updatelist;

/**
 * Description: Function Description
 * Copyright: Copyright (c) 2019
 * Company: Ruijie Co., Ltd.
 * Create Time: 2019/10/29
 *
 * @author hs
 */
public class CbbLinuxIDVUpdateListDTO extends CbbCommonUpdatelistDTO<CbbVDIComponentCommonVersionInfoDTO> {

    private String baseVersion;

    private String osLimit;

    private String versionLimit;

    public CbbLinuxIDVUpdateListDTO() {
    }

    public CbbLinuxIDVUpdateListDTO(String version, String baseVersion, Integer componentSize) {
        super(version, componentSize);
        this.baseVersion = baseVersion;
    }

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

    public String getVersionLimit() {
        return versionLimit;
    }

    public void setVersionLimit(String versionLimit) {
        this.versionLimit = versionLimit;
    }
}
