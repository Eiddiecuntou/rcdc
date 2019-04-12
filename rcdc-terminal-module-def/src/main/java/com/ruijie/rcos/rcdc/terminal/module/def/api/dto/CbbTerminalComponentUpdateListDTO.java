package com.ruijie.rcos.rcdc.terminal.module.def.api.dto;

import java.util.List;

/**
 * 
 * Description: 终端组件升级版本信息
 * Copyright: Copyright (c) 2018
 * Company: Ruijie Co., Ltd.
 * Create Time: 2018年12月14日
 * 
 * @author nt
 */
public class CbbTerminalComponentUpdateListDTO {

    private String version;

    private String baseVersion;

    private Integer componentSize;

    private String limitVersion;

    private List<CbbTerminalComponentVersionInfoDTO> componentList;

    public CbbTerminalComponentUpdateListDTO() {
        
    }

    public CbbTerminalComponentUpdateListDTO(String version, String baseVersion, Integer componentSize) {
        this.version = version;
        this.baseVersion = baseVersion;
        this.componentSize = componentSize;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public Integer getComponentSize() {
        return componentSize;
    }

    public void setComponentSize(Integer componentSize) {
        this.componentSize = componentSize;
    }

    public List<CbbTerminalComponentVersionInfoDTO> getComponentList() {
        return componentList;
    }

    public void setComponentList(List<CbbTerminalComponentVersionInfoDTO> componentList) {
        this.componentList = componentList;
    }

    public String getBaseVersion() {
        return baseVersion;
    }

    public void setBaseVersion(String baseVersion) {
        this.baseVersion = baseVersion;
    }

    public String getLimitVersion() {
        return limitVersion;
    }

    public void setLimitVersion(String limitVersion) {
        this.limitVersion = limitVersion;
    }

}
