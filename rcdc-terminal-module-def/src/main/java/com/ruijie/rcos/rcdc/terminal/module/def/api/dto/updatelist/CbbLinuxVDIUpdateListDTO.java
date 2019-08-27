package com.ruijie.rcos.rcdc.terminal.module.def.api.dto.updatelist;

/**
 * 
 * Description: linuxVDI终端组件升级版本信息
 * Copyright: Copyright (c) 2018
 * Company: Ruijie Co., Ltd.
 * Create Time: 2018年12月14日
 * 
 * @author nt
 */
public class CbbLinuxVDIUpdateListDTO extends CbbCommonUpdatelistDTO<CbbLinuxVDIComponentVersionInfoDTO> {

    private String baseVersion;

    public CbbLinuxVDIUpdateListDTO() {

    }

    public CbbLinuxVDIUpdateListDTO(String version, String baseVersion, Integer componentSize) {
        super(version, componentSize);
        this.baseVersion = baseVersion;
    }

    public String getBaseVersion() {
        return baseVersion;
    }

    public void setBaseVersion(String baseVersion) {
        this.baseVersion = baseVersion;
    }

}
