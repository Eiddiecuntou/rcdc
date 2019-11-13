package com.ruijie.rcos.rcdc.terminal.module.def.api.dto.updatelist;

import org.springframework.util.Assert;

import java.io.Serializable;
import java.util.List;

/**
 * Description: Function Description
 * Copyright: Copyright (c) 2018
 * Company: Ruijie Co., Ltd.
 * Create Time: 2019/8/1
 *
 * @param <T> 升级组件版本信息DTO
 * @author nt
 */
public class CbbBaseUpdateListDTO<T> implements Serializable {

    private String version;

    private Integer componentSize;

    private String limitVersion;

    private String validateMd5;

    private List<T> componentList;

    public CbbBaseUpdateListDTO() {
    }

    public CbbBaseUpdateListDTO(String version, Integer componentSize) {
        Assert.hasText(version, "version cannot be null");
        Assert.notNull(componentSize, "componentSize cannot be null");
        this.version = version;
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

    public String getLimitVersion() {
        return limitVersion;
    }

    public void setLimitVersion(String limitVersion) {
        this.limitVersion = limitVersion;
    }

    public String getValidateMd5() {
        return validateMd5;
    }

    public void setValidateMd5(String validateMd5) {
        this.validateMd5 = validateMd5;
    }

    public List<T> getComponentList() {
        return componentList;
    }

    public void setComponentList(List<T> componentList) {
        this.componentList = componentList;
    }
}
